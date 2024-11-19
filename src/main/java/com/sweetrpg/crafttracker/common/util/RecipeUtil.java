package com.sweetrpg.crafttracker.common.util;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.addon.jei.CTPlugin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;

import java.util.List;
import java.util.stream.Collectors;

public class RecipeUtil {

    public static List<Recipe> getRecipesFor(ResourceLocation itemId) {
        var rm = CTPlugin.jeiRuntime.getRecipeManager();

        var recipes = rm.createRecipeCategoryLookup().get()
                .peek(c -> CraftTracker.LOGGER.debug("category: {}", c))
                .map(c -> c.getRecipeType())
                .peek(t -> CraftTracker.LOGGER.debug("type: {}", t))
                .flatMap(t -> rm.createRecipeLookup(t).get())
                .peek(r -> CraftTracker.LOGGER.debug("recipe: {}", r))
                .filter(r -> r instanceof Recipe)
                .map(r -> Recipe.class.cast(r))
                .peek(r -> CraftTracker.LOGGER.debug("Recipe: {}", r.getId()))
                .filter(r -> r.getId().equals(itemId))
                .peek(r -> CraftTracker.LOGGER.debug("{}: {}", itemId, r))
                .collect(Collectors.toUnmodifiableList());

        return recipes;
    }
}
