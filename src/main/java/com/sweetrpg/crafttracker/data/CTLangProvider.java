package com.sweetrpg.crafttracker.data;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.lib.Constants;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class CTLangProvider extends LanguageProvider {
    private final String locale;

    public CTLangProvider(DataGenerator gen, String locale) {
        super(gen, Constants.MOD_ID, locale);
        this.locale = locale;
    }

    @Override
    public String getName() {
        return "CraftTracker Language Provider";
    }

    @Override
    protected void addTranslations() {
        switch(this.locale) {
            case Constants.LOCALE_EN_US -> processENUS();
            case Constants.LOCALE_EN_GB -> processENGB();
            case Constants.LOCALE_DE_DE -> processDEDE();
        }
    }

    private void processENUS() {
        CraftTracker.LOGGER.info("Adding translations for en_us...");

        add(Constants.TRANSLATION_KEY_GUI_CRAFTLIST_TITLE, "Craft List");
        add(Constants.TRANSLATION_KEY_GUI_SHOPPINGLIST_TITLE, "Shopping List");
        add(Constants.TRANSLATION_KEY_BINDINGS_CATEGORY_TITLE, "Craft Tracker");
        add(Constants.TRANSLATION_KEY_BINDINGS_ADDTOQUEUE_TITLE, "Add to Queue");
        add(Constants.TRANSLATION_KEY_BINDINGS_TOGGLE_CRAFTLIST_TITLE, "Toggle Craft List");
        add(Constants.TRANSLATION_KEY_BINDINGS_TOGGLE_SHOPPINGLIST_TITLE, "Toggle Shopping List");
    }

    private void processENGB() {
        CraftTracker.LOGGER.info("Adding translations for en_gb...");

        add(Constants.TRANSLATION_KEY_GUI_CRAFTLIST_TITLE, "Craft List");
        add(Constants.TRANSLATION_KEY_GUI_SHOPPINGLIST_TITLE, "Shopping List");
        add(Constants.TRANSLATION_KEY_BINDINGS_CATEGORY_TITLE, "Craft Tracker");
        add(Constants.TRANSLATION_KEY_BINDINGS_ADDTOQUEUE_TITLE, "Add to Queue");
        add(Constants.TRANSLATION_KEY_BINDINGS_TOGGLE_CRAFTLIST_TITLE, "Toggle Craft List");
        add(Constants.TRANSLATION_KEY_BINDINGS_TOGGLE_SHOPPINGLIST_TITLE, "Toggle Shopping List");
    }

    private void processDEDE() {
        CraftTracker.LOGGER.info("Adding translations for de_de...");

        add(Constants.TRANSLATION_KEY_GUI_CRAFTLIST_TITLE, "Bastelliste");
        add(Constants.TRANSLATION_KEY_GUI_SHOPPINGLIST_TITLE, "Einkaufsliste");
        add(Constants.TRANSLATION_KEY_BINDINGS_CATEGORY_TITLE, "Handwerks-Tracker");
        add(Constants.TRANSLATION_KEY_BINDINGS_ADDTOQUEUE_TITLE, "Zur Warteschlange hinzufügen");
        add(Constants.TRANSLATION_KEY_BINDINGS_TOGGLE_CRAFTLIST_TITLE, "Handwerksliste umschalten");
        add(Constants.TRANSLATION_KEY_BINDINGS_TOGGLE_SHOPPINGLIST_TITLE, "Einkaufsliste umschalten");
    }
}
