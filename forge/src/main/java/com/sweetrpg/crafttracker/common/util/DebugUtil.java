package com.sweetrpg.crafttracker.common.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.ObjectUtils;

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

        builder.append("Ingredient{\n");

        builder.append("\titems=[\n");
        Arrays.stream(ingredient.getItems())
                .forEach(i -> {
                    builder.append("\t\tregistryName=");
                    builder.append(ForgeRegistries.ITEMS.getKey(i.getItem()));
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
     * Debug output for an {@link ItemStack}
     *
     * @param itemStack An item stack to output
     * @return A formatted string
     */
    public static String printItemStack(ItemStack itemStack) {
        StringBuilder builder = new StringBuilder();

        builder.append("ItemStack{\n");

        builder.append("\tregistryName=");
        builder.append(ForgeRegistries.ITEMS.getKey(itemStack.getItem()));
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
     * Debug output for an {@link Item}
     *
     * @param item An item to output
     * @return A formatted string
     */
    public static String printItem(Item item) {
        StringBuilder builder = new StringBuilder();

        builder.append("Item{");

        builder.append("registryName=");
        builder.append(ForgeRegistries.ITEMS.getKey(item));

        builder.append("}");

        return builder.toString();
    }

    /**
     * Debug output for a recipe
     *
     * @param recipe The recipe to output
     * @return A formatted string
     */
    public static String printRecipe(Recipe<?> recipe) {
        StringBuilder builder = new StringBuilder();

        builder.append("Recipe{\n");

        builder.append("\tid=");
        builder.append(recipe.getId());
        builder.append("\n");

        builder.append("\tresultItem=");
        RegistryAccess access = Minecraft.getInstance().level.registryAccess();
        builder.append(DebugUtil.printItemStack(recipe.getResultItem(access)));
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
