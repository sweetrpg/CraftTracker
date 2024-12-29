package com.sweetrpg.crafttracker.common;

import com.sweetrpg.crafttracker.common.util.Util;
import net.minecraft.resources.ResourceLocation;

public class Constants {

    public static final String MOD_ID = "crafttracker";
    public static final String MOD_NAME = "Craft Tracker";

    public static final String VANILLA_ID = "minecraft";
    public static final String VANILLA_NAME = "Minecraft";

    public static final String JEI_PLUGIN_ID = "jei_plugin";

    // GUIs
    public static final int BACKGROUND_COLOR = 0x015f5f5f;
    public static final int BORDER_COLOR = 0x011f1f1f;

    // Network
    public static final ResourceLocation CHANNEL_NAME = Util.getResource("channel");
    public static final String PROTOCOL_VERSION = Integer.toString(1);

    // Language
    public static final String LOCALE_EN_US = "en_us";
    public static final String LOCALE_EN_GB = "en_gb";
    public static final String LOCALE_DE_DE = "de_de";

    // Translation keys
    public static final String TRANSLATION_KEY_GUI_CRAFT_QUEUE_TITLE = "crafttracker.screen.craft_queue.title";
    public static final String TRANSLATION_KEY_GUI_CRAFT_QUEUE_EMPTY = "crafttracker.screen.craft_queue.empty_message";
    public static final String TRANSLATION_KEY_GUI_CRAFT_QUEUE_HELP = "crafttracker.screen.craft_queue.help_message";
    public static final String TRANSLATION_KEY_GUI_CRAFT_QUEUE_SECTION_PRODUCTS = "crafttracker.screen.craft_queue.section.products";
    public static final String TRANSLATION_KEY_GUI_CRAFT_QUEUE_SECTION_INTERMEDIATES = "crafttracker.screen.craft_queue.section.intermediates";
    public static final String TRANSLATION_KEY_GUI_CRAFT_QUEUE_SECTION_MATERIALS = "crafttracker.screen.craft_queue.section.materials";
    public static final String TRANSLATION_KEY_GUI_CRAFT_QUEUE_SECTION_FUEL = "crafttracker.screen.craft_queue.section.fuel";
    public static final String TRANSLATION_KEY_GUI_HAVE = "crafttracker.screen.have";
    public static final String TRANSLATION_KEY_GUI_NO_RECIPES = "crafttracker.screen.no_recipes";
    public static final String TRANSLATION_KEY_GUI_SHOPPING_LIST_TITLE = "crafttracker.screen.shopping_list.title";
    public static final String TRANSLATION_KEY_GUI_SHOPPING_LIST_EMPTY = "crafttracker.screen.shopping_list.empty_message";
    public static final String TRANSLATION_KEY_GUI_SHOPPING_LIST_HELP = "crafttracker.screen.shopping_list.help_message";
    public static final String TRANSLATION_KEY_GUI_QUEUE_MGR_TITLE = "crafttracker.screen.queue_mgr.title";
    public static final String TRANSLATION_KEY_GUI_QUEUE_MGR_CLEAR_BUTTON = "crafttracker.screen.queue_mgr.button.clear";
    public static final String TRANSLATION_KEY_GUI_QUEUE_MGR_CLEAR_BUTTON_TOOLTIP = "crafttracker.screen.queue_mgr.button.clear.tooltip";
    public static final String TRANSLATION_KEY_GUI_QUEUE_MGR_DEC_BUTTON_TOOLTIP = "crafttracker.screen.queue_mgr.button.dec.tooltip";
    public static final String TRANSLATION_KEY_GUI_QUEUE_MGR_INC_BUTTON_TOOLTIP = "crafttracker.screen.queue_mgr.button.inc.tooltip";
    public static final String TRANSLATION_KEY_GUI_QUEUE_MGR_DEL_BUTTON_TOOLTIP = "crafttracker.screen.queue_mgr.button.del.tooltip";
    public static final String TRANSLATION_KEY_GUI_MSG_QUEUE_OVERLAY_MODE_HIDE = "crafttracker.msg.queue_overlay_mode.hide";
    public static final String TRANSLATION_KEY_GUI_MSG_QUEUE_OVERLAY_MODE_SHOW = "crafttracker.msg.queue_overlay_mode.show";
    public static final String TRANSLATION_KEY_GUI_MSG_QUEUE_OVERLAY_MODE_DYNAMIC = "crafttracker.msg.queue_overlay_mode.dynamic";
    public static final String TRANSLATION_KEY_GUI_MSG_SLIST_OVERLAY_MODE_HIDE = "crafttracker.msg.shopping_list_overlay_mode.hide";
    public static final String TRANSLATION_KEY_GUI_MSG_SLIST_OVERLAY_MODE_SHOW = "crafttracker.msg.shopping_list_overlay_mode.show";
    public static final String TRANSLATION_KEY_GUI_MSG_SLIST_OVERLAY_MODE_DYNAMIC = "crafttracker.msg.shopping_list_overlay_mode.dynamic";

    // Key bindings
    public static final String KEY_BINDINGS_CATEGORY_TITLE = "key.categories.crafttracker";
    public static final String TRANSLATION_KEY_BINDINGS_ADDTOQUEUE_TITLE = "key.crafttracker.addToQueue";
    public static final String TRANSLATION_KEY_BINDINGS_TOGGLE_CRAFT_QUEUE_TITLE = "key.crafttracker.toggleCraftQueue";
    public static final String TRANSLATION_KEY_BINDINGS_TOGGLE_SHOPPING_LIST_TITLE = "key.crafttracker.toggleShoppingList";
    public static final String TRANSLATION_KEY_BINDINGS_OPEN_QMGR_TITLE = "key.crafttracker.openQueueManager";
    public static final String TRANSLATION_KEY_BINDINGS_POPULATE_SHOPPING_LIST_TITLE = "key.crafttracker.populateShoppingList";
    public static final String TRANSLATION_KEY_BINDINGS_CLEAR_SHOPPING_LIST_TITLE = "key.crafttracker.clearShoppingList";

    // Config
    public static final String TRANSLATION_KEY_CONFIG_CLIENT_CALC_DEPTH = "crafttracker.config.client.calculation_depth";
    public static final String TRANSLATION_KEY_CONFIG_CLIENT_CRAFT_QUEUE_X = "crafttracker.config.client.craft_queue_x";
    public static final String TRANSLATION_KEY_CONFIG_CLIENT_CRAFT_QUEUE_Y = "crafttracker.config.client.craft_queue_y";
    public static final String TRANSLATION_KEY_CONFIG_CLIENT_CRAFT_QUEUE_WIDTH = "crafttracker.config.client.craft_queue_width";
    public static final String TRANSLATION_KEY_CONFIG_CLIENT_CRAFT_QUEUE_HEIGHT = "crafttracker.config.client.craft_queue_height";
    public static final String TRANSLATION_KEY_CONFIG_CLIENT_CRAFT_QUEUE_BACKGROUND_COLOR = "crafttracker.config.client.craft_queue_background_color";
    public static final String TRANSLATION_KEY_CONFIG_CLIENT_CRAFT_QUEUE_BORDER_COLOR = "crafttracker.config.client.craft_queue_border_color";
    public static final String TRANSLATION_KEY_CONFIG_CLIENT_SHOPPING_LIST_X = "crafttracker.config.client.shopping_list_x";
    public static final String TRANSLATION_KEY_CONFIG_CLIENT_SHOPPING_LIST_Y = "crafttracker.config.client.shopping_list_y";
    public static final String TRANSLATION_KEY_CONFIG_CLIENT_SHOPPING_LIST_WIDTH = "crafttracker.config.client.shopping_list_width";
    public static final String TRANSLATION_KEY_CONFIG_CLIENT_SHOPPING_LIST_HEIGHT = "crafttracker.config.client.shopping_list_height";
    public static final String TRANSLATION_KEY_CONFIG_CLIENT_SHOPPING_LIST_BACKGROUND_COLOR = "crafttracker.config.client.shopping_list_background_color";
    public static final String TRANSLATION_KEY_CONFIG_CLIENT_SHOPPING_LIST_BORDER_COLOR = "crafttracker.config.client.shopping_list_border_color";
    public static final String TRANSLATION_KEY_CONFIG_CLIENT_CRAFT_QUEUE_HIDE_EMPTY = "crafttracker.config.client.craft_queue_hide_empty";
    public static final String TRANSLATION_KEY_CONFIG_CLIENT_SHOPPING_LIST_HIDE_EMPTY = "crafttracker.config.client.shopping_list_hide_empty";

    // Advancements
    public static final String TRANSLATION_KEY_ADVANCEMENT_ROOT_TITLE = "advancements.crafttracker.main.root.title";
    public static final String TRANSLATION_KEY_ADVANCEMENT_ROOT_DESCRIPTION = "advancements.crafttracker.main.root.description";
    public static final String TRANSLATION_KEY_ADVANCEMENT_QUEUE_ITEM_TITLE = "advancements.crafttracker.main.queue_item.title";
    public static final String TRANSLATION_KEY_ADVANCEMENT_QUEUE_ITEM_DESCRIPTION = "advancements.crafttracker.main.queue_item.description";
    public static final String TRANSLATION_KEY_ADVANCEMENT_CRAFT_ITEM_TITLE = "advancements.crafttracker.main.craft_item.title";
    public static final String TRANSLATION_KEY_ADVANCEMENT_CRAFT_ITEM_DESCRIPTION = "advancements.crafttracker.main.craft_item.description";
    public static final String TRANSLATION_KEY_ADVANCEMENT_POPULATE_SHOPPING_LIST_TITLE = "advancements.crafttracker.main.populate_list.title";
    public static final String TRANSLATION_KEY_ADVANCEMENT_POPULATE_SHOPPING_LIST_DESCRIPTION = "advancements.crafttracker.main.populate_list.description";
    public static final String TRANSLATION_KEY_ADVANCEMENT_ACQUIRE_LIST_ITEM_TITLE = "advancements.crafttracker.main.acquire_item.title";
    public static final String TRANSLATION_KEY_ADVANCEMENT_ACQUIRE_LIST_ITEM_DESCRIPTION = "advancements.crafttracker.main.acquire_item.description";
    public static final String TRANSLATION_KEY_ADVANCEMENT_CLEAR_QUEUE_ITEM_TITLE = "advancements.crafttracker.main.clear_queue.title";
    public static final String TRANSLATION_KEY_ADVANCEMENT_CLEAR_QUEUE_ITEM_DESCRIPTION = "advancements.crafttracker.main.clear_queue.description";

}
