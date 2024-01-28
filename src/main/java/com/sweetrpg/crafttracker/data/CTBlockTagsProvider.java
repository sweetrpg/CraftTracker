package com.sweetrpg.crafttracker.data;

import com.sweetrpg.crafttracker.common.lib.Constants;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class CTBlockTagsProvider extends BlockTagsProvider {

    public CTBlockTagsProvider(DataGenerator generatorIn, ExistingFileHelper existingFileHelper) {
        super(generatorIn, Constants.MOD_ID, existingFileHelper);
    }

    @Override
    public String getName() {
        return "CraftTracker Block Tags";
    }

    @Override
    protected void addTags() {
        this.registerModTags();
        this.registerMinecraftTags();
        this.registerForgeTags();

        this.registerBlockMineables();
    }

    protected void registerBlockMineables() {

    }

    protected void registerMinecraftTags() {
//        tag(net.minecraft.tags.BlockTags.SMALL_FLOWERS)
//                .add(ModBlocks.WILD_CATNIP.get());
//        tag(net.minecraft.tags.BlockTags.CROPS)
//                .add(ModBlocks.CATNIP_CROP.get());
    }

    protected void registerForgeTags() {

    }

    protected void registerModTags() {
//        tag(ModTags.WILD_CROPS)
//                .add(ModBlocks.WILD_CATNIP.get());
    }

}
