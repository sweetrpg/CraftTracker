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
//        var ids = mgr.getRecipeIds()
//                .filter(rl -> rl.equals(itemId))
//                .collect(Collectors.toUnmodifiableList());
        var recipes = mgr.getRecipes().stream()
                .filter(r -> r.getResultItem().getItem().getRegistryName().equals(itemId))
//                .map(rl -> mgr.byKey(rl).get())
                .collect(Collectors.toUnmodifiableList());

//        var rm = CTPlugin.jeiRuntime.getRecipeManager();
//
//        var categories = rm.createRecipeCategoryLookup().get()
//                .collect(Collectors.toUnmodifiableList());
//        CraftTracker.LOGGER.debug("RecipeUtil#getRecipesFor: categories {}", categories);
//
//        var item = ForgeRegistries.ITEMS.getValue(itemId);
//
//        var recipes = rm.createRecipeCategoryLookup().get()
////                .peek(c -> CraftTracker.LOGGER.debug("category: {}", c))
//                .map(c -> c.getRecipeType())
////                .peek(t -> CraftTracker.LOGGER.debug("type: {}", t))
//                .flatMap(t -> rm.createRecipeLookup(t).get())
////                .peek(r -> CraftTracker.LOGGER.debug("recipe: {}", r))
////                .filter(r -> !((r instanceof ShapelessRecipe) ||
////                        (r instanceof ShapedRecipe)))
//                .filter(r -> {
//                    return r instanceof Recipe;
//                })
//                .map(r -> Recipe.class.cast(r))
//////                .peek(r -> CraftTracker.LOGGER.debug("Recipe: {}", r.getId()))
////                .filter(r -> {
////                    CraftTracker.LOGGER.debug("RecipeUtil#getRecipesFor: {}", r);
////                    try {
////                        var method = r.getClass().getMethod("getId");
////                        if(method != null) {
////                            var id = method.invoke(r);
////                            return id.equals(itemId);
////                        }
////                    }
////                    catch (Exception e) {
////                    }
////                    return false;
////                })
//////                .peek(r -> CraftTracker.LOGGER.debug("{}: {}", itemId, r))
//                .collect(Collectors.toUnmodifiableList());

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
