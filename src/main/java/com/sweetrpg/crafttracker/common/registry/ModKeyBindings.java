package com.sweetrpg.crafttracker.common.registry;

import com.sweetrpg.crafttracker.common.lib.Constants;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public class ModKeyBindings {

    public static final KeyBinding ADD_TO_QUEUE_MAPPING = new KeyBinding(Constants.TRANSLATION_KEY_BINDINGS_ADDTOQUEUE_TITLE, KeyConflictContext.GUI, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_Q, Constants.KEY_BINDINGS_CATEGORY_TITLE);
    public static final KeyBinding TOGGLE_CRAFT_QUEUE_MAPPING = new KeyBinding(Constants.TRANSLATION_KEY_BINDINGS_TOGGLE_CRAFT_QUEUE_TITLE, KeyConflictContext.GUI, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_L, Constants.KEY_BINDINGS_CATEGORY_TITLE);
    public static final KeyBinding TOGGLE_SHOPPING_LIST_MAPPING = new KeyBinding(Constants.TRANSLATION_KEY_BINDINGS_TOGGLE_SHOPPING_LIST_TITLE, KeyConflictContext.GUI, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_S, Constants.KEY_BINDINGS_CATEGORY_TITLE);
    public static final KeyBinding OPEN_QUEUE_MANAGER_MAPPING = new KeyBinding(Constants.TRANSLATION_KEY_BINDINGS_OPEN_QMGR_TITLE, KeyConflictContext.GUI, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_M, Constants.KEY_BINDINGS_CATEGORY_TITLE);
    public static final KeyBinding POPULATE_SHOPPING_LIST_MAPPING = new KeyBinding(Constants.TRANSLATION_KEY_BINDINGS_POPULATE_SHOPPING_LIST_TITLE, KeyConflictContext.GUI, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_P, Constants.KEY_BINDINGS_CATEGORY_TITLE);
    public static final KeyBinding CLEAR_SHOPPING_LIST_MAPPING = new KeyBinding(Constants.TRANSLATION_KEY_BINDINGS_CLEAR_SHOPPING_LIST_TITLE, KeyConflictContext.GUI, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_K, Constants.KEY_BINDINGS_CATEGORY_TITLE);

    public static void init() {
        ClientRegistry.registerKeyBinding(ADD_TO_QUEUE_MAPPING);
        ClientRegistry.registerKeyBinding(TOGGLE_CRAFT_QUEUE_MAPPING);
        ClientRegistry.registerKeyBinding(TOGGLE_SHOPPING_LIST_MAPPING);
        ClientRegistry.registerKeyBinding(OPEN_QUEUE_MANAGER_MAPPING);
        ClientRegistry.registerKeyBinding(POPULATE_SHOPPING_LIST_MAPPING);
        ClientRegistry.registerKeyBinding(CLEAR_SHOPPING_LIST_MAPPING);
    }

}
