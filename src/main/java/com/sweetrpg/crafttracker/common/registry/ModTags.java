package com.sweetrpg.crafttracker.common.registry;

import com.sweetrpg.crafttracker.common.util.Util;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {

    private static TagKey<Item> modItemTag(String name) {
        return ItemTags.create(Util.getResource(name));
    }

    private static TagKey<Block> modBlockTag(String name) {
        return BlockTags.create(Util.getResource(name));
    }

//    private static TagKey<EntityType<?>> tag(String path) {
//        return EntityTypeTags.bind(Util.getResourcePath(path));
//    }
}
