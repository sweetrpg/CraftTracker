package com.sweetrpg.crafttracker.common.registry;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.settings.KeyConflictContext;

public class ModKeyBindings {

    public static final String KEY_CATEGORIES_CRAFT_TRACKER = "key.categories.crafttracker";
    public static final String KEY_ADD_TO_QUEUE = "key.addToQueue";

    public static final KeyMapping ADD_TO_QUEUE_MAPPING = new KeyMapping(KEY_ADD_TO_QUEUE, KeyConflictContext.GUI, InputConstants.Type.KEYSYM, InputConstants.KEY_Q, KEY_CATEGORIES_CRAFT_TRACKER);

    public static void init() {
        ClientRegistry.registerKeyBinding(ADD_TO_QUEUE_MAPPING);
    }
}
