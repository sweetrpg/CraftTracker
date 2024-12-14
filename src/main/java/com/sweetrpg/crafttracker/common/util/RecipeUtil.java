package com.sweetrpg.crafttracker.common.util;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.util.calc.ItemCostCalculator;
import com.sweetrpg.crafttracker.common.util.calc.RecipeCostCalculator;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Helper functions for handling recipes
 */
public class RecipeUtil {

    /**
     * Looks up recipes that will output the specified item
     *
     * @param itemId The ID of the item that the recipe would produce
     * @return A {@link List} of recipes that produce the item
     */
    public static List<? extends Recipe<?>> getRecipesFor(ResourceLocation itemId) {
        CraftTracker.LOGGER.debug("RecipeUtil#getRecipesFor: {}", itemId);

        var mgr = Minecraft.getInstance().level.getRecipeManager();
        RegistryAccess access = Minecraft.getInstance().level.registryAccess();
        var recipes = mgr.getRecipes().stream()
                .filter(r -> ForgeRegistries.ITEMS.getKey(r.getResultItem(access).getItem()).equals(itemId))
                .toList();

        CraftTracker.LOGGER.debug("RecipeUtil#getRecipesFor: recipes {}", recipes.stream().map(DebugUtil::printRecipe).toList());
        return recipes;
    }

    /**
     * Checks if all the ingredients in the list are the same
     *
     * @param ingredients The list of ingredients to check
     * @return A boolean indicating if the ingredients are all the same
     */
    public static boolean areIngredientsSame(NonNullList<Ingredient> ingredients) {
        CraftTracker.LOGGER.debug("RecipeUtil#areIngredientsSame: {}", ingredients.stream().map(DebugUtil::printIngredient).toList());

        Set<String> ing = ingredients.stream()
                .map(i -> Arrays.asList(i.getItems()))
                .filter(l -> !l.isEmpty())
                .map(l -> l.get(0))
                .map(i -> ForgeRegistries.ITEMS.getKey(i.getItem()).toString())
                .collect(Collectors.toSet());

        return ing.size() == 1;
    }

    /**
     * Check if the ingredients' namespace matches the provided value
     *
     * @param namespace   The namespace to check for
     * @param ingredients The list of ingredients to check
     * @return A boolean value indicating if all the ingredients are in the specified namespace
     */
    public static boolean areIngredientsSameNamespace(String namespace, NonNullList<Ingredient> ingredients) {
        CraftTracker.LOGGER.debug("RecipeUtil#areIngredientsSame: {}", ingredients.stream().map(DebugUtil::printIngredient).toList());

        Set<String> ing = ingredients.stream()
                .map(i -> Arrays.asList(i.getItems()))
                .filter(l -> !l.isEmpty())
                .map(l -> l.get(0))
                .map(i -> ObjectUtils.defaultIfNull(ForgeRegistries.ITEMS.getKey(i.getItem()).getNamespace(), ""))
                .filter(n -> n.equals(namespace))
                .collect(Collectors.toSet());

        return ing.size() == 1;
    }

    /**
     * Given a list of recipes, return the one that is least expensive.
     *
     * @param recipes A list of recipes to examine
     * @return The least expensive recipe
     */
    public static Recipe<?> chooseLeastExpensiveOf(List<? extends Recipe<?>> recipes) {
        CraftTracker.LOGGER.debug("RecipeUtil#chooseLeastExpensiveOf: {}", recipes.stream().map(DebugUtil::printRecipe).toList());

        if(recipes.size() == 1) {
            return recipes.get(0);
        }

        List<Tuple<? extends Recipe<?>, Integer>> recipeCosts = new ArrayList<>();

        for(Recipe<?> recipe : recipes) {
            CraftTracker.LOGGER.debug("recipe: {}", DebugUtil.printRecipe(recipe));
            var cost = new RecipeCostCalculator(recipe).calculate();
            CraftTracker.LOGGER.debug("cost: {}", cost);
            var tuple = new Tuple<>(recipe, cost);

            recipeCosts.add(tuple);
        }

        CraftTracker.LOGGER.debug("sorting recipes");
        recipeCosts.sort((rc1, rc2) -> {
            var result = rc1.getB().compareTo(rc2.getB());
            if(result == 0) {
                return rc1.getA().getId().compareTo(rc2.getA().getId());
            }
            return result;
        });

        var itemToReturn = recipeCosts.get(0).getA();
        CraftTracker.LOGGER.debug("returning top item from sorted recipes: {}", DebugUtil.printRecipe(itemToReturn));
        return itemToReturn;
    }

    /**
     * Given an array of {@link ItemStack}, choose the least expensive and return it.
     *
     * @param stacks An array of item stacks
     * @return The least expensive item stack
     */
    public static ItemStack chooseLeastExpensiveOf(ItemStack[] stacks) {
        CraftTracker.LOGGER.debug("RecipeUtil#chooseLeastExpensiveOf: {}", Arrays.stream(stacks).map(DebugUtil::printItemStack).toList());

        if(stacks.length == 1) {
            CraftTracker.LOGGER.debug("only 1 item in the stack; returning that");
            return stacks[0];
        }

        List<Tuple<ItemStack, Integer>> itemCosts = new ArrayList<>();

        for(ItemStack stack : stacks) {
            CraftTracker.LOGGER.debug("stack: {}", DebugUtil.printItemStack(stack));
            var cost = new ItemCostCalculator(stack).calculate();
            CraftTracker.LOGGER.debug("cost: {}", cost);
            var tuple = new Tuple<>(stack, cost);

            itemCosts.add(tuple);
        }

        CraftTracker.LOGGER.debug("Considering the costs of {} items:", itemCosts.size());
        itemCosts.forEach(t -> {
            var s = t.getA();
            var c = t.getB();

            CraftTracker.LOGGER.debug("item: {}, cost: {}", ForgeRegistries.ITEMS.getKey(s.getItem()), c);
        });

        CraftTracker.LOGGER.debug("sorting items");
        itemCosts.sort((rc1, rc2) -> {
            var result = rc1.getB().compareTo(rc2.getB());
            if(result == 0) {
                return ForgeRegistries.ITEMS.getKey(rc1.getA().getItem()).toString()
                        .compareTo(ForgeRegistries.ITEMS.getKey(rc2.getA().getItem()).toString());
            }
            return result;
        });

        var itemToReturn = itemCosts.get(0).getA();
        CraftTracker.LOGGER.debug("returning top item from sorted items: {}", DebugUtil.printItemStack(itemToReturn));
        return itemToReturn;
    }

}
