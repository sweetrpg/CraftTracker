package com.sweetrpg.crafttracker.common.util;


import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;

/**
 * Utility functions for formatted output of various objects.
 */
public class DebugUtil {

    /**
     * Debug output for an {@link Ingredient}
     *
     * @param ingredient The ingredient to output
     * @return A formatted string
     */
    public static String printIngredient(Ingredient ingredient) {
        StringBuilder builder = new StringBuilder();

        builder.append("Ingredient{ ");

        builder.append("items=[ ");
        Arrays.stream(ingredient.getItems())
                .forEach(i -> {
                    builder.append("registryName=");
                    builder.append(i.getItem().getRegistryName());

                    builder.append(", tags=[");
                    builder.append(String.join(",", i.getItem().getTags().stream().map(ResourceLocation::toString).toList()));
                    builder.append("]");

                    builder.append(", count=");
                    builder.append(i.getCount());
                });
        builder.append(" ]");

        builder.append(" }");

        return builder.toString();
    }

    /**
     * Debug output for an {@link ItemStack}
     *
     * @param itemStack An item stack to output
     * @return A formatted string
     */
    public static String printItemStack(ItemStack itemStack) {
        StringBuilder builder = new StringBuilder();

        builder.append("ItemStack{ ");

        builder.append("registryName=");
        builder.append(itemStack.getItem().getRegistryName());

        builder.append(", count=");
        builder.append(itemStack.getCount());

        builder.append(", tags=[");
        builder.append(String.join(",", itemStack.getItem().getTags().stream().map(ResourceLocation::toString).toList()));
        builder.append("]");

        builder.append(" }");

        return builder.toString();
    }

    /**
     * Debug output for an {@link Item}
     *
     * @param item An item to output
     * @return A formatted string
     */
    public static String printItem(Item item) {
        StringBuilder builder = new StringBuilder();

        builder.append("Item{ ");

        builder.append("registryName=");
        builder.append(item.getRegistryName());

        builder.append(" }");

        return builder.toString();
    }

    /**
     * Debug output for a recipe
     *
     * @param recipe The recipe to output
     * @return A formatted string
     */
    public static String printRecipe(IRecipe<?> recipe) {
        StringBuilder builder = new StringBuilder();

        builder.append("Recipe{ ");

        builder.append("id=");
        builder.append(recipe.getId());

        builder.append(", resultItem=");
        builder.append(DebugUtil.printItemStack(recipe.getResultItem()));

        builder.append(", ingredients=[ ");
        builder.append(String.join(",", recipe.getIngredients().stream()
                .map(DebugUtil::printIngredient)
                .toList()));
        builder.append(" ]");

        builder.append(" }");

        return builder.toString();
    }

}
