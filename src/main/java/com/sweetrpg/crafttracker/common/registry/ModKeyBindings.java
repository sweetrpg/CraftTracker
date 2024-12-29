package com.sweetrpg.crafttracker.common.registry;

import com.mojang.blaze3d.platform.InputConstants;
import com.sweetrpg.crafttracker.common.Constants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID,
        value = {Dist.CLIENT},
        bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModKeyBindings {

    public static final KeyMapping ADD_TO_QUEUE_MAPPING = new KeyMapping(Constants.TRANSLATION_KEY_BINDINGS_ADDTOQUEUE_TITLE, KeyConflictContext.GUI, InputConstants.Type.KEYSYM, InputConstants.KEY_Q, Constants.KEY_BINDINGS_CATEGORY_TITLE);
    public static final KeyMapping TOGGLE_CRAFT_QUEUE_MAPPING = new KeyMapping(Constants.TRANSLATION_KEY_BINDINGS_TOGGLE_CRAFT_QUEUE_TITLE, KeyConflictContext.GUI, InputConstants.Type.KEYSYM, InputConstants.KEY_RSHIFT | InputConstants.KEY_L, Constants.KEY_BINDINGS_CATEGORY_TITLE);
    public static final KeyMapping TOGGLE_SHOPPING_LIST_MAPPING = new KeyMapping(Constants.TRANSLATION_KEY_BINDINGS_TOGGLE_SHOPPING_LIST_TITLE, KeyConflictContext.GUI, InputConstants.Type.KEYSYM, InputConstants.KEY_RSHIFT | InputConstants.KEY_S, Constants.KEY_BINDINGS_CATEGORY_TITLE);
    public static final KeyMapping OPEN_QUEUE_MANAGER_MAPPING = new KeyMapping(Constants.TRANSLATION_KEY_BINDINGS_OPEN_QMGR_TITLE, KeyConflictContext.GUI, InputConstants.Type.KEYSYM, InputConstants.KEY_RSHIFT | InputConstants.KEY_M, Constants.KEY_BINDINGS_CATEGORY_TITLE);
    public static final KeyMapping POPULATE_SHOPPING_LIST_MAPPING = new KeyMapping(Constants.TRANSLATION_KEY_BINDINGS_POPULATE_SHOPPING_LIST_TITLE, KeyConflictContext.GUI, InputConstants.Type.KEYSYM, InputConstants.KEY_RSHIFT | InputConstants.KEY_P, Constants.KEY_BINDINGS_CATEGORY_TITLE);
    public static final KeyMapping CLEAR_SHOPPING_LIST_MAPPING = new KeyMapping(Constants.TRANSLATION_KEY_BINDINGS_CLEAR_SHOPPING_LIST_TITLE, KeyConflictContext.GUI, InputConstants.Type.KEYSYM, InputConstants.KEY_RSHIFT | InputConstants.KEY_K, Constants.KEY_BINDINGS_CATEGORY_TITLE);

    @SubscribeEvent
    public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
        event.register(ADD_TO_QUEUE_MAPPING);
        event.register(TOGGLE_CRAFT_QUEUE_MAPPING);
        event.register(TOGGLE_SHOPPING_LIST_MAPPING);
        event.register(OPEN_QUEUE_MANAGER_MAPPING);
        event.register(POPULATE_SHOPPING_LIST_MAPPING);
        event.register(CLEAR_SHOPPING_LIST_MAPPING);
    }

    public static void init() {}
}
