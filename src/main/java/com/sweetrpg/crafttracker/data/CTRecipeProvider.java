package com.sweetrpg.crafttracker.data;

import com.google.gson.JsonObject;
import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.registry.ModBlocks;
import com.sweetrpg.crafttracker.common.registry.ModItems;
import com.sweetrpg.crafttracker.common.registry.ModRecipeSerializers;
import com.sweetrpg.crafttracker.common.util.Util;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

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
