package com.sweetrpg.crafttracker.common.lib;

import com.sweetrpg.crafttracker.common.util.Util;
import net.minecraft.resources.ResourceLocation;

public class Constants {

    public static final String MOD_ID = "crafttracker";
    public static final String MOD_NAME = "Craft Tracker";

    public static final String VANILLA_ID = "minecraft";
    public static final String VANILLA_NAME = "Minecraft";

    // Network
    public static final ResourceLocation CHANNEL_NAME = Util.getResource("channel");
    public static final String PROTOCOL_VERSION = Integer.toString(1);

    // Language
    public static final String LOCALE_EN_US = "en_us";
    public static final String LOCALE_EN_GB = "en_gb";
    public static final String LOCALE_DE_DE = "de_de";

    // Translation keys
    public static final String TRANSLATION_KEY_GUI_CRAFTLIST_TITLE = "crafttracker.screen.craft_list.title";
    public static final String TRANSLATION_KEY_GUI_SHOPPINGLIST_TITLE = "crafttracker.screen.shopping_list.title";
    public static final String TRANSLATION_KEY_BINDINGS_CATEGORY_TITLE = "key.categories.crafttracker";
    public static final String TRANSLATION_KEY_BINDINGS_ADDTOQUEUE_TITLE = "key.addToQueue";
    public static final String TRANSLATION_KEY_BINDINGS_TOGGLE_CRAFTLIST_TITLE = "key.toggleCraftList";
    public static final String TRANSLATION_KEY_BINDINGS_TOGGLE_SHOPPINGLIST_TITLE = "key.toggleShoppingList";

    // Advancements
    public static final String TRANSLATION_KEY_ADVANCEMENT_MAKE_LIST_TITLE = "advancements.crafttracker.main.make_list.title";
    public static final String TRANSLATION_KEY_ADVANCEMENT_MAKE_LIST_DESCRIPTION = "advancements.crafttracker.main.make_list.description";

}
