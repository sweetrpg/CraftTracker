package com.sweetrpg.crafttracker.data;

import com.google.gson.JsonObject;
import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.registry.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;

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

        ShapelessRecipeBuilder.shapeless(ModItems.SHOPPING_LIST.get())
                .group("shopping")
                .requires(Items.CRAFTING_TABLE)
                .requires(Items.PAPER)
                .unlockedBy("has_crafting_table", has(Items.CRAFTING_TABLE))
                .unlockedBy("has_paper", has(Items.PAPER))
                .save(consumer);
    }

    @Override
    protected void saveAdvancement(HashCache cache, JsonObject advancementJson, Path pathIn) {
        // NOOP - We don't replace any of the advancement things yet...
    }
}
