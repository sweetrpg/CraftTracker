package com.sweetrpg.crafttracker.common.util;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.lib.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import org.apache.commons.lang3.ObjectUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class RecipeUtil {

    public static final float NON_VANILLA_COST_MULTIPLIER = 1.2f;
    public static final float NON_CRAFTING_COST_MULTIPLIER = 1.25f;

    private static Map<ResourceLocation, Integer> ingredientCostsByTag = new HashMap<>();
    private static Map<ResourceLocation, Integer> ingredientCostOverrides = new HashMap<>();

    static {
        var mgr = Minecraft.getInstance().getResourceManager();

        var ingCostsResource = new ResourceLocation(Constants.MOD_ID, "ingredient-costs.properties");
        try {
            var costs = mgr.getResource(ingCostsResource);
            var props = new Properties();
            props.load(costs.getInputStream());
            props.entrySet().forEach(entry -> {
                var key = new ResourceLocation((String) entry.getKey());
                var value = Integer.parseInt((String) entry.getValue());
                ingredientCostsByTag.put(key, value);
            });
        }
        catch (IOException e) {
            CraftTracker.LOGGER.error("I/O exception when trying to load ingredient costs file", e);
        }

        var ingOverridesResource = new ResourceLocation(Constants.MOD_ID, "ingredient-overrides.properties");
        try {
            var costs = mgr.getResource(ingOverridesResource);
            var props = new Properties();
            props.load(costs.getInputStream());
            props.entrySet().forEach(entry -> {
                var key = new ResourceLocation((String) entry.getKey());
                var value = Integer.parseInt((String) entry.getValue());
                ingredientCostOverrides.put(key, value);
            });
        }
        catch (IOException e) {
            CraftTracker.LOGGER.error("I/O exception when trying to load ingredient overrides file", e);
        }
    }

    public static List<? extends Recipe<?>> getRecipesFor(ResourceLocation itemId) {
        CraftTracker.LOGGER.debug("RecipeUtil#getRecipesFor: {}", itemId);

        var mgr = Minecraft.getInstance().level.getRecipeManager();
        var recipes = mgr.getRecipes().stream()
                .filter(r -> r.getResultItem().getItem().getRegistryName().equals(itemId))
                .toList();

        CraftTracker.LOGGER.debug("RecipeUtil#getRecipesFor: recipes {}", recipes.stream().map(DebugUtil::printRecipe).toList());
        return recipes;
    }

    public static boolean areIngredientsSame(NonNullList<Ingredient> ingredients) {
        CraftTracker.LOGGER.debug("RecipeUtil#areIngredientsSame: {}", ingredients.stream().map(DebugUtil::printIngredient).toList());

        Set<String> ing = ingredients.stream()
                .map(i -> Arrays.asList(i.getItems()))
                .filter(l -> !l.isEmpty())
                .map(l -> l.get(0))
                .map(i -> i.getItem().getRegistryName().toString())
                .collect(Collectors.toSet());

        return ing.size() == 1;
    }

    public static boolean areIngredientsSameNamespace(String namespace, NonNullList<Ingredient> ingredients) {
        CraftTracker.LOGGER.debug("RecipeUtil#areIngredientsSame: {}", ingredients.stream().map(DebugUtil::printIngredient).toList());

        Set<String> ing = ingredients.stream()
                .map(i -> Arrays.asList(i.getItems()))
                .filter(l -> !l.isEmpty())
                .map(l -> l.get(0))
                .map(i -> ObjectUtils.defaultIfNull(i.getItem().getRegistryName().getNamespace(), ""))
                .filter(n -> n.equals(namespace))
                .collect(Collectors.toSet());

        return ing.size() == 1;
    }

    public static float calculateRecipeCost(Recipe<?> recipe) {
        CraftTracker.LOGGER.debug("RecipeUtil#calculateRecipeCost: {}", DebugUtil.printRecipe(recipe));

        float cost = recipe.getIngredients().stream()
                .map(RecipeUtil::calculateIngredientCost)
                .reduce(0f, Float::sum);

        // if the item's namespace is not 'minecraft:', increase the cost
        if(!recipe.getId().getNamespace().equals("minecraft")) {
            CraftTracker.LOGGER.debug("RecipeUtil#calculateRecipeCost: increasing cost ({}) of non-vanilla recipe {} by {}",
                    cost, DebugUtil.printRecipe(recipe), NON_VANILLA_COST_MULTIPLIER);
            cost *= NON_VANILLA_COST_MULTIPLIER;
        }

        if(!(recipe instanceof CraftingRecipe)) {
            CraftTracker.LOGGER.debug("RecipeUtil#calculateRecipeCost: increasing cost ({}) of non-crafting table recipe {} by {}",
                    cost, DebugUtil.printRecipe(recipe), NON_CRAFTING_COST_MULTIPLIER);
            cost *= NON_CRAFTING_COST_MULTIPLIER;
        }

        return cost;
    }

    public static float calculateIngredientCost(Ingredient ingredient) {
        CraftTracker.LOGGER.debug("RecipeUtil#getIngredientCost: {}", DebugUtil.printIngredient(ingredient));

        for(ItemStack stack : ingredient.getItems()) {
            // is the item in the override list?
            var itemId = stack.getItem().getRegistryName();
            var count = stack.getCount();

            if(ingredientCostOverrides.containsKey(itemId)) {
                CraftTracker.LOGGER.debug("found item {} in override list", itemId);
                return ingredientCostOverrides.get(itemId) * count;
            }

            // it's not, so check its tags
            float highestCost = 0;
            for(TagKey<Item> tag : stack.getTags().toList()) {
                var tagId = tag.location();
                if(ingredientCostsByTag.containsKey(tagId)) {
                    CraftTracker.LOGGER.debug("found item {} in tag list", tagId);
                    float cost = ingredientCostsByTag.get(tagId) * count;

                    // if the item's namespace is not 'minecraft:', increase the cost
                    if(!ObjectUtils.defaultIfNull(stack.getItem().getRegistryName().getNamespace(), "").equals("minecraft") &&
                            !ObjectUtils.defaultIfNull(tagId.getNamespace(), "").equals("minecraft")) {
                        CraftTracker.LOGGER.debug("RecipeUtil#calculateRecipeCost: increasing cost ({}) of non-vanilla item {} by {}",
                                cost, tagId, NON_VANILLA_COST_MULTIPLIER);
                        cost *= NON_VANILLA_COST_MULTIPLIER;
                    }

                    if(cost > highestCost) {
                        highestCost = cost;
                    }
                }
            }
            if(highestCost > 0) {
                return highestCost;
            }
        }

        CraftTracker.LOGGER.debug("#calculateIngredientCost: fell through to default cost");
        return 1;
    }

    public static float calculateItemCost(ItemStack stack) {
        CraftTracker.LOGGER.debug("#calculateItemCost: {}", DebugUtil.printItemStack(stack));

        var itemId = stack.getItem().getRegistryName();
        var count = stack.getCount();

        if(ingredientCostOverrides.containsKey(itemId)) {
            CraftTracker.LOGGER.debug("found item {} in override list", itemId);
            return ingredientCostOverrides.get(itemId) * count;
        }

        // it's not, so check its tags
        float highestCost = 0;
        for(TagKey<Item> tag : stack.getTags().toList()) {
            var tagId = tag.location();
            if(ingredientCostsByTag.containsKey(tagId)) {
                CraftTracker.LOGGER.debug("found item {} in tag list", tagId);
                double cost = ingredientCostsByTag.get(tagId) * count;

                // if the item's namespace is not 'minecraft:', increase the cost
                if(!ObjectUtils.defaultIfNull(stack.getItem().getRegistryName().getNamespace(), "").equals("minecraft") &&
                        !ObjectUtils.defaultIfNull(tagId.getNamespace(), "").equals("minecraft")) {
                    CraftTracker.LOGGER.debug("RecipeUtil#calculateItemCost: increasing cost ({}) of non-vanilla item {} by {}",
                            cost, tagId, NON_VANILLA_COST_MULTIPLIER);
                    cost *= NON_VANILLA_COST_MULTIPLIER;
                }

                if(cost > highestCost) {
                    highestCost = (int) cost;
                }
            }
        }
        if(highestCost > 0) {
            return highestCost;
        }

        CraftTracker.LOGGER.debug("#calculateIngredientCost: fell through to rarity");
        var rarity = stack.getItem().getRarity(stack);
        return Math.max(rarity.ordinal() * count, count);
    }

    public static Recipe<?> chooseLeastExpensiveOf(List<? extends Recipe<?>> recipes) {
        CraftTracker.LOGGER.debug("RecipeUtil#chooseLeastExpensiveOf: {}", recipes.stream().map(DebugUtil::printRecipe).toList());

        if(recipes.size() == 1) {
            return recipes.get(0);
        }

        List<Tuple<? extends Recipe<?>, Float>> recipeCosts = new ArrayList<>();

        for(Recipe<?> recipe : recipes) {
            var cost = RecipeUtil.calculateRecipeCost(recipe);
            var tuple = new Tuple<>(recipe, cost);

            recipeCosts.add(tuple);
        }

        recipeCosts.sort((rc1, rc2) -> {
            var result = rc1.getB().compareTo(rc2.getB());
            if(result == 0) {
                return rc1.getA().getId().compareTo(rc2.getA().getId());
            }
            return result;
        });

        return recipeCosts.get(0).getA();
    }

    public static ItemStack chooseLeastExpensiveOf(ItemStack[] stacks) {
        CraftTracker.LOGGER.debug("RecipeUtil#chooseLeastExpensiveOf: {}", Arrays.stream(stacks).map(DebugUtil::printItemStack).toList());

        if(stacks.length == 1) {
            return stacks[0];
        }

        List<Tuple<ItemStack, Float>> itemCosts = new ArrayList<>();

        for(ItemStack stack : stacks) {
            var cost = RecipeUtil.calculateItemCost(stack);
            var tuple = new Tuple<>(stack, cost);

            itemCosts.add(tuple);
        }

        itemCosts.sort((rc1, rc2) -> {
            var result = rc1.getB().compareTo(rc2.getB());
            if(result == 0) {
                return rc1.getA().getItem().getRegistryName().toString()
                        .compareTo(rc2.getA().getItem().getRegistryName().toString());
            }
            return result;
        });

        return itemCosts.get(0).getA();
    }

}
