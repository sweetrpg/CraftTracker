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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class RecipeUtil {

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

        CraftTracker.LOGGER.debug("RecipeUtil#getRecipesFor: recipes {}", recipes);
        return recipes;
    }

    public static boolean areIngredientsSame(NonNullList<Ingredient> ingredients) {
        CraftTracker.LOGGER.debug("RecipeUtil#areIngredientsSame: {}", ingredients);

        Set<String> ing = ingredients.stream()
                .map(i -> Arrays.asList(i.getItems()))
                .filter(l -> !l.isEmpty())
                .map(l -> l.get(0))
                .map(i -> i.getItem().getRegistryName().toString())
                .collect(Collectors.toSet());

        return ing.size() == 1;
    }

    public static int calculateRecipeCost(Recipe<?> recipe) {
        CraftTracker.LOGGER.debug("RecipeUtil#calculateRecipeCost: {}", recipe);

        return recipe.getIngredients().stream()
                .map(RecipeUtil::calculateIngredientCost)
                .reduce(0, Integer::sum);
    }

    public static int calculateIngredientCost(Ingredient ingredient) {
        CraftTracker.LOGGER.debug("RecipeUtil#getIngredientCost: {}", ingredient);

        for(ItemStack stack : ingredient.getItems()) {
            // is the item in the override list?
            var itemId = stack.getItem().getRegistryName();
            var count = stack.getCount();

            if(ingredientCostOverrides.containsKey(itemId)) {
                CraftTracker.LOGGER.debug("found item {} in override list", itemId);
                return ingredientCostOverrides.get(itemId) * count;
            }

            // it's not, so check its tags
            if(stack.hasTag()) {
                for(TagKey<Item> tag : stack.getTags().toList()) {
                    var tagId = tag.location();
                    if(ingredientCostsByTag.containsKey(tagId)) {
                        CraftTracker.LOGGER.debug("found item {} in tag list", tagId);
                        return ingredientCostsByTag.get(tagId) * count;
                    }
                }
            }
        }

        return 1;
    }

    public static int calculateItemCost(ItemStack stack) {
        CraftTracker.LOGGER.debug("#calculateItemCost: {}", stack);

        var itemId = stack.getItem().getRegistryName();
        var count = stack.getCount();

        if(ingredientCostOverrides.containsKey(itemId)) {
            CraftTracker.LOGGER.debug("found item {} in override list", itemId);
            return ingredientCostOverrides.get(itemId) * count;
        }

        // it's not, so check its tags
        if(stack.hasTag()) {
            for(TagKey<Item> tag : stack.getTags().toList()) {
                var tagId = tag.location();
                if(ingredientCostsByTag.containsKey(tagId)) {
                    CraftTracker.LOGGER.debug("found item {} in tag list", tagId);
                    return ingredientCostsByTag.get(tagId) * count;
                }
            }
        }

        var rarity = stack.getItem().getRarity(stack);
        return rarity.ordinal();
    }

    public static int chooseLeastExpensiveOf(List<? extends Recipe<?>> recipes) {
        CraftTracker.LOGGER.debug("RecipeUtil#chooseLeastExpensiveOf: {}", recipes);

        List<Tuple<ResourceLocation, Integer>> recipeCosts = new ArrayList<>();

        for(Recipe<?> recipe : recipes) {
            var cost = RecipeUtil.calculateRecipeCost(recipe);
            var tuple = new Tuple<>(recipe.getId(), cost);

            recipeCosts.add(tuple);
        }

        int lowestCostIndex = 0;
        int lowestCost = Integer.MAX_VALUE;
        for(int i = 0; i < recipeCosts.size(); i++) {
            var cost = recipeCosts.get(i).getB();
            if(cost < lowestCost) {
                lowestCostIndex = i;
                lowestCost = cost;
            }
        }

        return lowestCostIndex;
    }

    public static int chooseLeastExpensiveOf(ItemStack[] stacks) {
        CraftTracker.LOGGER.debug("RecipeUtil#chooseLeastExpensiveOf: {}", stacks);

        List<Tuple<ResourceLocation, Integer>> itemCosts = new ArrayList<>();

        for(ItemStack stack : stacks) {
            var cost = RecipeUtil.calculateItemCost(stack);
            var tuple = new Tuple<>(stack.getItem().getRegistryName(), cost);

            itemCosts.add(tuple);
        }

        int lowestCostIndex = 0;
        int lowestCost = Integer.MAX_VALUE;
        for(int i = 0; i < itemCosts.size(); i++) {
            var cost = itemCosts.get(i).getB();
            if(cost < lowestCost) {
                lowestCostIndex = i;
                lowestCost = cost;
            }
        }

        return lowestCostIndex;
    }

}
