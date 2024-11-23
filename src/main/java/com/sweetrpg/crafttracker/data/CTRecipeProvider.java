package com.sweetrpg.crafttracker.data;

import com.google.gson.JsonObject;
import com.sweetrpg.crafttracker.CraftTracker;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;

import java.nio.file.Path;
import java.util.function.Consumer;

public class CTRecipeProvider extends RecipeProvider {

    public CTRecipeProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    public String getName() {
        return "CraftTracker Recipes";
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        CraftTracker.LOGGER.debug("Build crafting recipes: {}", consumer);

        // treats
//        ShapelessRecipeBuilder.shapeless(ModItems.SUPER_TREAT.get(), 5)
//                .requires(ModItems.TRAINING_TREAT.get(), 5)
//                .requires(Items.GOLDEN_APPLE, 1)
//                .unlockedBy("has_golden_apple", has(Items.GOLDEN_APPLE))
//                .save(consumer);

    }

    @Override
    protected void saveAdvancement(HashCache cache, JsonObject advancementJson, Path pathIn) {
        // NOOP - We don't replace any of the advancement things yet...
    }
}
