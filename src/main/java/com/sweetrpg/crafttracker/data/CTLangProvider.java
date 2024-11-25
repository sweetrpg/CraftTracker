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
        add(Constants.TRANSLATION_KEY_GUI_CRAFTLIST_EMPTY, "The queue is empty.");
        add(Constants.TRANSLATION_KEY_GUI_CRAFTLIST_HELP, "To manage the queue:");
        add(Constants.TRANSLATION_KEY_GUI_CRAFTLIST_SECTION_PRODUCTS, "Products");
        add(Constants.TRANSLATION_KEY_GUI_CRAFTLIST_SECTION_INTERMEDIATES, "Intermediates");
        add(Constants.TRANSLATION_KEY_GUI_CRAFTLIST_SECTION_MATERIALS, "Materials");
        add(Constants.TRANSLATION_KEY_GUI_CRAFTLIST_SECTION_FUEL, "Fuel");
        add(Constants.TRANSLATION_KEY_GUI_CRAFTLIST_HAVE, "have %d");
        add(Constants.TRANSLATION_KEY_GUI_SHOPPINGLIST_TITLE, "Shopping List");
        add(Constants.TRANSLATION_KEY_GUI_QUEUEMGR_TITLE, "Queue Manager");
        add(Constants.TRANSLATION_KEY_GUI_QUEUEMGR_CLEAR_BUTTON, "Clear");
        add(Constants.TRANSLATION_KEY_GUI_QUEUEMGR_DEC_BUTTON_TOOLTIP, "Decrease the amount crafted");
        add(Constants.TRANSLATION_KEY_GUI_QUEUEMGR_INC_BUTTON_TOOLTIP, "Increase the amount crafted");
        add(Constants.TRANSLATION_KEY_GUI_QUEUEMGR_DEL_BUTTON_TOOLTIP, "Delete this item from the queue");
        add(Constants.TRANSLATION_KEY_BINDINGS_CATEGORY_TITLE, "Craft Tracker");
        add(Constants.TRANSLATION_KEY_BINDINGS_ADDTOQUEUE_TITLE, "Add to Queue");
        add(Constants.TRANSLATION_KEY_BINDINGS_TOGGLE_CRAFTLIST_TITLE, "Toggle Craft List");
        add(Constants.TRANSLATION_KEY_BINDINGS_TOGGLE_SHOPPINGLIST_TITLE, "Toggle Shopping List");
        add(Constants.TRANSLATION_KEY_BINDINGS_OPEN_QMGR_TITLE, "Open Queue Manager");
        add(Constants.TRANSLATION_KEY_GUI_MSG_QUEUE_OVERLAY_MODE_HIDE, "The craft list overlay will now be hidden.");
        add(Constants.TRANSLATION_KEY_GUI_MSG_QUEUE_OVERLAY_MODE_SHOW, "The craft list overlay will now be shown.");
        add(Constants.TRANSLATION_KEY_GUI_MSG_QUEUE_OVERLAY_MODE_DYNAMIC, "The craft list overlay mode is dynamic.");
    }

    private void processENGB() {
        CraftTracker.LOGGER.info("Adding translations for en_gb...");

        add(Constants.TRANSLATION_KEY_GUI_CRAFTLIST_TITLE, "Craft List");
        add(Constants.TRANSLATION_KEY_GUI_CRAFTLIST_EMPTY, "The queue is empty.");
        add(Constants.TRANSLATION_KEY_GUI_CRAFTLIST_HELP, "To manage the queue:");
        add(Constants.TRANSLATION_KEY_GUI_CRAFTLIST_SECTION_PRODUCTS, "Products");
        add(Constants.TRANSLATION_KEY_GUI_CRAFTLIST_SECTION_INTERMEDIATES, "Intermediates");
        add(Constants.TRANSLATION_KEY_GUI_CRAFTLIST_SECTION_MATERIALS, "Materials");
        add(Constants.TRANSLATION_KEY_GUI_CRAFTLIST_SECTION_FUEL, "Fuel");
        add(Constants.TRANSLATION_KEY_GUI_CRAFTLIST_HAVE, "have %d");
        add(Constants.TRANSLATION_KEY_GUI_SHOPPINGLIST_TITLE, "Shopping List");
        add(Constants.TRANSLATION_KEY_GUI_QUEUEMGR_TITLE, "Queue Manager");
        add(Constants.TRANSLATION_KEY_GUI_QUEUEMGR_CLEAR_BUTTON, "Clear");
        add(Constants.TRANSLATION_KEY_GUI_QUEUEMGR_DEC_BUTTON_TOOLTIP, "Decrease the amount crafted");
        add(Constants.TRANSLATION_KEY_GUI_QUEUEMGR_INC_BUTTON_TOOLTIP, "Increase the amount crafted");
        add(Constants.TRANSLATION_KEY_GUI_QUEUEMGR_DEL_BUTTON_TOOLTIP, "Delete this item from the queue");
        add(Constants.TRANSLATION_KEY_BINDINGS_CATEGORY_TITLE, "Craft Tracker");
        add(Constants.TRANSLATION_KEY_BINDINGS_ADDTOQUEUE_TITLE, "Add to Queue");
        add(Constants.TRANSLATION_KEY_BINDINGS_TOGGLE_CRAFTLIST_TITLE, "Toggle Craft List");
        add(Constants.TRANSLATION_KEY_BINDINGS_TOGGLE_SHOPPINGLIST_TITLE, "Toggle Shopping List");
        add(Constants.TRANSLATION_KEY_BINDINGS_OPEN_QMGR_TITLE, "Open Queue Manager");
        add(Constants.TRANSLATION_KEY_GUI_MSG_QUEUE_OVERLAY_MODE_HIDE, "The craft list overlay will now be hidden.");
        add(Constants.TRANSLATION_KEY_GUI_MSG_QUEUE_OVERLAY_MODE_SHOW, "The craft list overlay will now be shown.");
        add(Constants.TRANSLATION_KEY_GUI_MSG_QUEUE_OVERLAY_MODE_DYNAMIC, "The craft list overlay mode is dynamic.");
    }

    private void processDEDE() {
        CraftTracker.LOGGER.info("Adding translations for de_de...");

        add(Constants.TRANSLATION_KEY_GUI_CRAFTLIST_TITLE, "Bastelliste");
        add(Constants.TRANSLATION_KEY_GUI_CRAFTLIST_EMPTY, "Die Warteschlange ist leer.");
        add(Constants.TRANSLATION_KEY_GUI_CRAFTLIST_HELP, "Die Warteschlange verwalten:");
        add(Constants.TRANSLATION_KEY_GUI_CRAFTLIST_SECTION_PRODUCTS, "Produkte");
        add(Constants.TRANSLATION_KEY_GUI_CRAFTLIST_SECTION_INTERMEDIATES, "Zwischenprodukte");
        add(Constants.TRANSLATION_KEY_GUI_CRAFTLIST_SECTION_MATERIALS, "Materialien");
        add(Constants.TRANSLATION_KEY_GUI_CRAFTLIST_SECTION_FUEL, "Kraftstoff");
        add(Constants.TRANSLATION_KEY_GUI_CRAFTLIST_HAVE, "habe %d");
        add(Constants.TRANSLATION_KEY_GUI_SHOPPINGLIST_TITLE, "Einkaufsliste");
        add(Constants.TRANSLATION_KEY_GUI_QUEUEMGR_TITLE, "Warteschlangenmanager");
        add(Constants.TRANSLATION_KEY_GUI_QUEUEMGR_CLEAR_BUTTON, "Alle löschen");
        add(Constants.TRANSLATION_KEY_GUI_QUEUEMGR_DEC_BUTTON_TOOLTIP, "Reduzieren Sie die Menge an hergestellten");
        add(Constants.TRANSLATION_KEY_GUI_QUEUEMGR_INC_BUTTON_TOOLTIP, "Erhöhen Sie die Menge an hergestellten");
        add(Constants.TRANSLATION_KEY_GUI_QUEUEMGR_DEL_BUTTON_TOOLTIP, "Dieses Element aus der Warteschlange löschen");
        add(Constants.TRANSLATION_KEY_BINDINGS_CATEGORY_TITLE, "Handwerks-Tracker");
        add(Constants.TRANSLATION_KEY_BINDINGS_ADDTOQUEUE_TITLE, "Zur Warteschlange hinzufügen");
        add(Constants.TRANSLATION_KEY_BINDINGS_TOGGLE_CRAFTLIST_TITLE, "Handwerksliste umschalten");
        add(Constants.TRANSLATION_KEY_BINDINGS_TOGGLE_SHOPPINGLIST_TITLE, "Einkaufsliste umschalten");
        add(Constants.TRANSLATION_KEY_BINDINGS_OPEN_QMGR_TITLE, "Warteschlangenmanager öffnen");
        add(Constants.TRANSLATION_KEY_GUI_MSG_QUEUE_OVERLAY_MODE_HIDE, "Die Überlagerung der Handwerksliste wird nun ausgeblendet.");
        add(Constants.TRANSLATION_KEY_GUI_MSG_QUEUE_OVERLAY_MODE_SHOW, "Jetzt wird die Überlagerung mit der Handwerksliste angezeigt.");
        add(Constants.TRANSLATION_KEY_GUI_MSG_QUEUE_OVERLAY_MODE_DYNAMIC, "Der Überlagerungsmodus der Handwerksliste ist dynamisch.");
    }
}
