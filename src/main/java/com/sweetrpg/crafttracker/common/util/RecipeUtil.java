package com.sweetrpg.crafttracker.common.util;

import com.sweetrpg.crafttracker.CraftTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RecipeUtil {

    public static List<? extends Recipe<?>> getRecipesFor(ResourceLocation itemId) {
        CraftTracker.LOGGER.debug("RecipeUtil#getRecipesFor: {}", itemId);

        var mgr = Minecraft.getInstance().level.getRecipeManager();
        var recipes = mgr.getRecipes().stream()
                .filter(r -> r.getResultItem().getItem().getRegistryName().equals(itemId))
                .collect(Collectors.toUnmodifiableList());

        CraftTracker.LOGGER.debug("RecipeUtil#getRecipesFor: recipes {}", recipes);
        return recipes;
    }

    public static boolean areIngredientsSame(NonNullList<Ingredient> ingredients) {
        CraftTracker.LOGGER.debug("RecipeUtil#areIngredientsSame: {}", ingredients);

        Set<String> ing = ingredients.stream()
                .map((i) -> Arrays.asList(i.getItems()))
                .filter((l) -> !l.isEmpty())
                .map((l) -> l.get(0))
                .map((i) -> i.getItem().getRegistryName().toString())
                .collect(Collectors.toSet());

        return ing.size() == 1;
    }
}
