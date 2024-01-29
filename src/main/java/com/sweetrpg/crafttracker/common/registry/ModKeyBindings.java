package com.sweetrpg.crafttracker.common.registry;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.settings.KeyConflictContext;

public class ModKeyBindings {

    public static final String KEY_CATEGORIES_CRAFT_TRACKER = "key.categories.crafttracker";
    public static final String KEY_ADD_TO_QUEUE = "key.addToQueue";
    public static final String KEY_TOGGLE_CRAFT_LIST = "key.toggleCraftList";
    public static final String KEY_TOGGLE_SHOPPING_LIST = "key.toggleShoppingList";

    public static final KeyMapping ADD_TO_QUEUE_MAPPING = new KeyMapping(KEY_ADD_TO_QUEUE, KeyConflictContext.GUI, InputConstants.Type.KEYSYM, InputConstants.KEY_Q, KEY_CATEGORIES_CRAFT_TRACKER);
    public static final KeyMapping TOGGLE_CRAFT_LIST_MAPPING = new KeyMapping(KEY_TOGGLE_CRAFT_LIST, KeyConflictContext.GUI, InputConstants.Type.KEYSYM, InputConstants.KEY_LSHIFT | InputConstants.KEY_L, KEY_CATEGORIES_CRAFT_TRACKER);
    public static final KeyMapping TOGGLE_SHOPPING_LIST_MAPPING = new KeyMapping(KEY_TOGGLE_SHOPPING_LIST, KeyConflictContext.GUI, InputConstants.Type.KEYSYM, InputConstants.KEY_LSHIFT | InputConstants.KEY_S, KEY_CATEGORIES_CRAFT_TRACKER);

    public static void init() {
        ClientRegistry.registerKeyBinding(ADD_TO_QUEUE_MAPPING);
        ClientRegistry.registerKeyBinding(TOGGLE_CRAFT_LIST_MAPPING);
        ClientRegistry.registerKeyBinding(TOGGLE_SHOPPING_LIST_MAPPING);
    }
}
