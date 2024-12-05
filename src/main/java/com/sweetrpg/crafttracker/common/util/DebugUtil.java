package com.sweetrpg.crafttracker.common.util;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Arrays;

/**
 *
 */
public class DebugUtil {

    /**
     * @param ingredient
     * @return
     */
    public static String printIngredient(Ingredient ingredient) {
        StringBuilder builder = new StringBuilder();

        builder.append("Ingredient{\n");

        builder.append("\titems=[\n");
        Arrays.stream(ingredient.getItems())
                .forEach(i -> {
                    builder.append("\t\tregistryName=");
                    builder.append(i.getItem().getRegistryName());
                    builder.append("\n");

                    builder.append("\t\ttags=[");
                    builder.append(String.join(",", i.getTags().map(TagKey::toString).toList()));
                    builder.append("]\n");

                    builder.append("\t\tcount=");
                    builder.append(i.getCount());
                    builder.append("\n");
                });
        builder.append("\t]\n");

        builder.append("}");

        return builder.toString();
    }

    /**
     *
     * @param itemStack
     * @return
     */
    public static String printItemStack(ItemStack itemStack) {
        StringBuilder builder = new StringBuilder();

        builder.append("ItemStack{\n");

        builder.append("\tregistryName=");
        builder.append(itemStack.getItem().getRegistryName());
        builder.append("\n");

        builder.append("\tcount=");
        builder.append(itemStack.getCount());
        builder.append("\n");

        builder.append("\ttags=[");
        builder.append(String.join(",", itemStack.getTags().map(TagKey::toString).toList()));
        builder.append("]\n");

        builder.append("}");

        return builder.toString();
    }

    /**
     *
     * @param item
     * @return
     */
    public static String printItem(Item item) {
        StringBuilder builder = new StringBuilder();

        builder.append("Item{");

        builder.append("registryName=");
        builder.append(item.getRegistryName());

        builder.append("}");

        return builder.toString();
    }

    /**
     *
     * @param recipe
     * @return
     */
    public static String printRecipe(Recipe<?> recipe) {
        StringBuilder builder = new StringBuilder();

        builder.append("Recipe{\n");

        builder.append("\tid=");
        builder.append(recipe.getId());
        builder.append("\n");

        builder.append("\tresultItem=");
        builder.append(DebugUtil.printItemStack(recipe.getResultItem()));
        builder.append("\n");

        builder.append("\tingredients=[\n\t\t");
        builder.append(String.join(",\n\t\t", recipe.getIngredients().stream()
                .map(DebugUtil::printIngredient)
                .toList()));
        builder.append("\t]\n");

        builder.append("}");

        return builder.toString();
    }

}
