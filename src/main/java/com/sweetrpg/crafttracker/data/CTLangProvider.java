package com.sweetrpg.crafttracker.data;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.Constants;
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

        add(Constants.TRANSLATION_KEY_GUI_CRAFT_QUEUE_TITLE, "Craft Queue");
        add(Constants.TRANSLATION_KEY_GUI_CRAFT_QUEUE_EMPTY, "The queue is empty.");
        add(Constants.TRANSLATION_KEY_GUI_CRAFT_QUEUE_HELP, "To manage the queue:");
        add(Constants.TRANSLATION_KEY_GUI_CRAFT_QUEUE_SECTION_PRODUCTS, "To make:");
        add(Constants.TRANSLATION_KEY_GUI_CRAFT_QUEUE_SECTION_INTERMEDIATES, "You first need to make:");
        add(Constants.TRANSLATION_KEY_GUI_CRAFT_QUEUE_SECTION_MATERIALS, "With these materials:");
        add(Constants.TRANSLATION_KEY_GUI_CRAFT_QUEUE_SECTION_FUEL, "And this fuel:");
        add(Constants.TRANSLATION_KEY_GUI_HAVE, "have %d");
        add(Constants.TRANSLATION_KEY_GUI_NO_RECIPES, "No recipes for %s at index %d");
        add(Constants.TRANSLATION_KEY_GUI_SHOPPING_LIST_TITLE, "Shopping List");
        add(Constants.TRANSLATION_KEY_GUI_SHOPPING_LIST_EMPTY, "The shopping list is empty.");
        add(Constants.TRANSLATION_KEY_GUI_SHOPPING_LIST_HELP, "To clear the list:");
        add(Constants.TRANSLATION_KEY_GUI_QUEUE_MGR_TITLE, "Queue Manager");
        add(Constants.TRANSLATION_KEY_GUI_QUEUE_MGR_CLEAR_BUTTON, "Clear");
        add(Constants.TRANSLATION_KEY_GUI_QUEUE_MGR_DEC_BUTTON_TOOLTIP, "Decrease the amount crafted");
        add(Constants.TRANSLATION_KEY_GUI_QUEUE_MGR_INC_BUTTON_TOOLTIP, "Increase the amount crafted");
        add(Constants.TRANSLATION_KEY_GUI_QUEUE_MGR_DEL_BUTTON_TOOLTIP, "Delete this item from the queue");
        add(Constants.TRANSLATION_KEY_GUI_QUEUE_MGR_PICKER_TITLE, "Add Item");
        add(Constants.TRANSLATION_KEY_GUI_QUEUE_MGR_PICKER_SEARCH, "Search:");
        add(Constants.KEY_BINDINGS_CATEGORY_TITLE, "Craft Tracker");
        add(Constants.TRANSLATION_KEY_BINDINGS_ADDTOQUEUE_TITLE, "Add to Queue");
        add(Constants.TRANSLATION_KEY_BINDINGS_TOGGLE_CRAFT_QUEUE_TITLE, "Toggle Craft Queue");
        add(Constants.TRANSLATION_KEY_BINDINGS_TOGGLE_SHOPPING_LIST_TITLE, "Toggle Shopping List");
        add(Constants.TRANSLATION_KEY_BINDINGS_OPEN_QMGR_TITLE, "Open Queue Manager");
        add(Constants.TRANSLATION_KEY_BINDINGS_POPULATE_SHOPPING_LIST_TITLE, "Populate Shopping List");
        add(Constants.TRANSLATION_KEY_BINDINGS_CLEAR_SHOPPING_LIST_TITLE, "Clear Shopping List");
        add(Constants.TRANSLATION_KEY_GUI_MSG_QUEUE_OVERLAY_MODE_HIDE, "The craft queue overlay will now be hidden.");
        add(Constants.TRANSLATION_KEY_GUI_MSG_QUEUE_OVERLAY_MODE_SHOW, "The craft queue overlay will now be shown.");
        add(Constants.TRANSLATION_KEY_GUI_MSG_QUEUE_OVERLAY_MODE_DYNAMIC, "The craft queue overlay mode is dynamic.");
        add(Constants.TRANSLATION_KEY_GUI_MSG_SLIST_OVERLAY_MODE_HIDE, "The shopping list overlay will now be hidden.");
        add(Constants.TRANSLATION_KEY_GUI_MSG_SLIST_OVERLAY_MODE_SHOW, "The shopping list overlay will now be shown.");
        add(Constants.TRANSLATION_KEY_GUI_MSG_SLIST_OVERLAY_MODE_DYNAMIC, "The shopping list overlay mode is dynamic.");

        add(Constants.TRANSLATION_KEY_CONFIG_CLIENT_CRAFT_QUEUE_X, "The X position on the screen for the craft queue overlay");
        add(Constants.TRANSLATION_KEY_CONFIG_CLIENT_CRAFT_QUEUE_Y, "The Y position on the screen for the craft queue overlay");
        add(Constants.TRANSLATION_KEY_CONFIG_CLIENT_CRAFT_QUEUE_WIDTH, "The width of the craft queue overlay");
        add(Constants.TRANSLATION_KEY_CONFIG_CLIENT_CRAFT_QUEUE_HEIGHT, "The height of the craft queue overlay");
        add(Constants.TRANSLATION_KEY_CONFIG_CLIENT_SHOPPING_LIST_X, "The X position on the screen for the shopping list overlay");
        add(Constants.TRANSLATION_KEY_CONFIG_CLIENT_SHOPPING_LIST_Y, "The Y position on the screen for the shopping list overlay");
        add(Constants.TRANSLATION_KEY_CONFIG_CLIENT_SHOPPING_LIST_WIDTH, "The width of the shopping list overlay");
        add(Constants.TRANSLATION_KEY_CONFIG_CLIENT_SHOPPING_LIST_HEIGHT, "The height of the shopping list overlay");
        add(Constants.TRANSLATION_KEY_CONFIG_CLIENT_CRAFT_QUEUE_HIDE_EMPTY, "Should the craft queue overlay be hidden when it is empty?");
        add(Constants.TRANSLATION_KEY_CONFIG_CLIENT_SHOPPING_LIST_HIDE_EMPTY, "Should the shopping list overlay be hidden when it is empty?");

        add(Constants.TRANSLATION_KEY_ADVANCEMENT_ROOT_TITLE, "Tracking Crafts");
        add(Constants.TRANSLATION_KEY_ADVANCEMENT_ROOT_DESCRIPTION, "Keep track of all the things you need to craft");
        add(Constants.TRANSLATION_KEY_ADVANCEMENT_QUEUE_ITEM_TITLE, "Remember This");
        add(Constants.TRANSLATION_KEY_ADVANCEMENT_QUEUE_ITEM_DESCRIPTION, "Add an item to the crafting queue");
        add(Constants.TRANSLATION_KEY_ADVANCEMENT_CRAFT_ITEM_TITLE, "Workin'");
        add(Constants.TRANSLATION_KEY_ADVANCEMENT_CRAFT_ITEM_DESCRIPTION, "Craft a queued item");
        add(Constants.TRANSLATION_KEY_ADVANCEMENT_POPULATE_SHOPPING_LIST_TITLE, "Honey-do List");
        add(Constants.TRANSLATION_KEY_ADVANCEMENT_POPULATE_SHOPPING_LIST_DESCRIPTION, "Populate the shopping list");
        add(Constants.TRANSLATION_KEY_ADVANCEMENT_ACQUIRE_LIST_ITEM_TITLE, "Just Keep Shopping");
        add(Constants.TRANSLATION_KEY_ADVANCEMENT_ACQUIRE_LIST_ITEM_DESCRIPTION, "Acquire an item that is in the shopping list");
        add(Constants.TRANSLATION_KEY_ADVANCEMENT_CLEAR_QUEUE_ITEM_TITLE, "Like A Boss");
        add(Constants.TRANSLATION_KEY_ADVANCEMENT_CLEAR_QUEUE_ITEM_DESCRIPTION, "Clear the crafting queue entirely");
    }

    private void processENGB() {
        CraftTracker.LOGGER.info("Adding translations for en_gb...");

        add(Constants.TRANSLATION_KEY_GUI_CRAFT_QUEUE_TITLE, "Craft Queue");
        add(Constants.TRANSLATION_KEY_GUI_CRAFT_QUEUE_EMPTY, "The queue is empty.");
        add(Constants.TRANSLATION_KEY_GUI_CRAFT_QUEUE_HELP, "To manage the queue:");
        add(Constants.TRANSLATION_KEY_GUI_CRAFT_QUEUE_SECTION_PRODUCTS, "To make:");
        add(Constants.TRANSLATION_KEY_GUI_CRAFT_QUEUE_SECTION_INTERMEDIATES, "You first need to make:");
        add(Constants.TRANSLATION_KEY_GUI_CRAFT_QUEUE_SECTION_MATERIALS, "With these materials:");
        add(Constants.TRANSLATION_KEY_GUI_CRAFT_QUEUE_SECTION_FUEL, "And this fuel:");
        add(Constants.TRANSLATION_KEY_GUI_HAVE, "have %d");
        add(Constants.TRANSLATION_KEY_GUI_NO_RECIPES, "No recipes for %s at index %d");
        add(Constants.TRANSLATION_KEY_GUI_SHOPPING_LIST_TITLE, "Shopping List");
        add(Constants.TRANSLATION_KEY_GUI_SHOPPING_LIST_EMPTY, "The shopping list is empty.");
        add(Constants.TRANSLATION_KEY_GUI_SHOPPING_LIST_HELP, "To clear the list:");
        add(Constants.TRANSLATION_KEY_GUI_QUEUE_MGR_TITLE, "Queue Manager");
        add(Constants.TRANSLATION_KEY_GUI_QUEUE_MGR_CLEAR_BUTTON, "Clear");
        add(Constants.TRANSLATION_KEY_GUI_QUEUE_MGR_DEC_BUTTON_TOOLTIP, "Decrease the amount crafted");
        add(Constants.TRANSLATION_KEY_GUI_QUEUE_MGR_INC_BUTTON_TOOLTIP, "Increase the amount crafted");
        add(Constants.TRANSLATION_KEY_GUI_QUEUE_MGR_DEL_BUTTON_TOOLTIP, "Delete this item from the queue");
        add(Constants.TRANSLATION_KEY_GUI_QUEUE_MGR_PICKER_TITLE, "Add Item");
        add(Constants.TRANSLATION_KEY_GUI_QUEUE_MGR_PICKER_SEARCH, "Search:");
        add(Constants.KEY_BINDINGS_CATEGORY_TITLE, "Craft Tracker");
        add(Constants.TRANSLATION_KEY_BINDINGS_ADDTOQUEUE_TITLE, "Add to Queue");
        add(Constants.TRANSLATION_KEY_BINDINGS_TOGGLE_CRAFT_QUEUE_TITLE, "Toggle Craft Queue");
        add(Constants.TRANSLATION_KEY_BINDINGS_TOGGLE_SHOPPING_LIST_TITLE, "Toggle Shopping List");
        add(Constants.TRANSLATION_KEY_BINDINGS_OPEN_QMGR_TITLE, "Open Queue Manager");
        add(Constants.TRANSLATION_KEY_BINDINGS_POPULATE_SHOPPING_LIST_TITLE, "Populate Shopping List");
        add(Constants.TRANSLATION_KEY_BINDINGS_CLEAR_SHOPPING_LIST_TITLE, "Clear Shopping List");
        add(Constants.TRANSLATION_KEY_GUI_MSG_QUEUE_OVERLAY_MODE_HIDE, "The craft queue overlay will now be hidden.");
        add(Constants.TRANSLATION_KEY_GUI_MSG_QUEUE_OVERLAY_MODE_SHOW, "The craft queue overlay will now be shown.");
        add(Constants.TRANSLATION_KEY_GUI_MSG_QUEUE_OVERLAY_MODE_DYNAMIC, "The craft queue overlay mode is dynamic.");
        add(Constants.TRANSLATION_KEY_GUI_MSG_SLIST_OVERLAY_MODE_HIDE, "The shopping list overlay will now be hidden.");
        add(Constants.TRANSLATION_KEY_GUI_MSG_SLIST_OVERLAY_MODE_SHOW, "The shopping list overlay will now be shown.");
        add(Constants.TRANSLATION_KEY_GUI_MSG_SLIST_OVERLAY_MODE_DYNAMIC, "The shopping list overlay mode is dynamic.");

        add(Constants.TRANSLATION_KEY_CONFIG_CLIENT_CRAFT_QUEUE_X, "The X position on the screen for the craft queue overlay");
        add(Constants.TRANSLATION_KEY_CONFIG_CLIENT_CRAFT_QUEUE_Y, "The Y position on the screen for the craft queue overlay");
        add(Constants.TRANSLATION_KEY_CONFIG_CLIENT_CRAFT_QUEUE_WIDTH, "The width of the craft queue overlay");
        add(Constants.TRANSLATION_KEY_CONFIG_CLIENT_CRAFT_QUEUE_HEIGHT, "The height of the craft queue overlay");
        add(Constants.TRANSLATION_KEY_CONFIG_CLIENT_SHOPPING_LIST_X, "The X position on the screen for the shopping list overlay");
        add(Constants.TRANSLATION_KEY_CONFIG_CLIENT_SHOPPING_LIST_Y, "The Y position on the screen for the shopping list overlay");
        add(Constants.TRANSLATION_KEY_CONFIG_CLIENT_SHOPPING_LIST_WIDTH, "The width of the shopping list overlay");
        add(Constants.TRANSLATION_KEY_CONFIG_CLIENT_SHOPPING_LIST_HEIGHT, "The height of the shopping list overlay");
        add(Constants.TRANSLATION_KEY_CONFIG_CLIENT_CRAFT_QUEUE_HIDE_EMPTY, "Should the craft queue overlay be hidden when it is empty?");
        add(Constants.TRANSLATION_KEY_CONFIG_CLIENT_SHOPPING_LIST_HIDE_EMPTY, "Should the shopping list overlay be hidden when it is empty?");

        add(Constants.TRANSLATION_KEY_ADVANCEMENT_ROOT_TITLE, "Tracking Crafts");
        add(Constants.TRANSLATION_KEY_ADVANCEMENT_ROOT_DESCRIPTION, "Keep track of all the things you need to craft");
        add(Constants.TRANSLATION_KEY_ADVANCEMENT_QUEUE_ITEM_TITLE, "Remember This");
        add(Constants.TRANSLATION_KEY_ADVANCEMENT_QUEUE_ITEM_DESCRIPTION, "Add an item to the crafting queue");
        add(Constants.TRANSLATION_KEY_ADVANCEMENT_CRAFT_ITEM_TITLE, "Workin'");
        add(Constants.TRANSLATION_KEY_ADVANCEMENT_CRAFT_ITEM_DESCRIPTION, "Craft a queued item");
        add(Constants.TRANSLATION_KEY_ADVANCEMENT_POPULATE_SHOPPING_LIST_TITLE, "Honey-do List");
        add(Constants.TRANSLATION_KEY_ADVANCEMENT_POPULATE_SHOPPING_LIST_DESCRIPTION, "Populate the shopping list");
        add(Constants.TRANSLATION_KEY_ADVANCEMENT_ACQUIRE_LIST_ITEM_TITLE, "Just Keep Shopping");
        add(Constants.TRANSLATION_KEY_ADVANCEMENT_ACQUIRE_LIST_ITEM_DESCRIPTION, "Acquire an item that is in the shopping list");
        add(Constants.TRANSLATION_KEY_ADVANCEMENT_CLEAR_QUEUE_ITEM_TITLE, "Like A Boss");
        add(Constants.TRANSLATION_KEY_ADVANCEMENT_CLEAR_QUEUE_ITEM_DESCRIPTION, "Clear the crafting queue entirely");
    }

    private void processDEDE() {
        CraftTracker.LOGGER.info("Adding translations for de_de...");

        add(Constants.TRANSLATION_KEY_GUI_CRAFT_QUEUE_TITLE, "Herstellungswarteschlange");
        add(Constants.TRANSLATION_KEY_GUI_CRAFT_QUEUE_EMPTY, "Die Warteschlange ist leer.");
        add(Constants.TRANSLATION_KEY_GUI_CRAFT_QUEUE_HELP, "Die Warteschlange verwalten:");
        add(Constants.TRANSLATION_KEY_GUI_CRAFT_QUEUE_SECTION_PRODUCTS, "Um zu machen:");
        add(Constants.TRANSLATION_KEY_GUI_CRAFT_QUEUE_SECTION_INTERMEDIATES, "Sie müssen zunächst Folgendes tun:");
        add(Constants.TRANSLATION_KEY_GUI_CRAFT_QUEUE_SECTION_MATERIALS, "Mit diesen Materialien:");
        add(Constants.TRANSLATION_KEY_GUI_CRAFT_QUEUE_SECTION_FUEL, "Und dieser Treibstoff:");
        add(Constants.TRANSLATION_KEY_GUI_HAVE, "habe %d");
        add(Constants.TRANSLATION_KEY_GUI_NO_RECIPES, "Keine Rezepte für %s bei Index %d");
        add(Constants.TRANSLATION_KEY_GUI_SHOPPING_LIST_TITLE, "Einkaufsliste");
        add(Constants.TRANSLATION_KEY_GUI_SHOPPING_LIST_EMPTY, "The shopping list is empty.");
        add(Constants.TRANSLATION_KEY_GUI_SHOPPING_LIST_HELP, "To clear the list:");
        add(Constants.TRANSLATION_KEY_GUI_QUEUE_MGR_TITLE, "Warteschlangenmanager");
        add(Constants.TRANSLATION_KEY_GUI_QUEUE_MGR_CLEAR_BUTTON, "Alle löschen");
        add(Constants.TRANSLATION_KEY_GUI_QUEUE_MGR_DEC_BUTTON_TOOLTIP, "Reduzieren Sie die Menge an hergestellten");
        add(Constants.TRANSLATION_KEY_GUI_QUEUE_MGR_INC_BUTTON_TOOLTIP, "Erhöhen Sie die Menge an hergestellten");
        add(Constants.TRANSLATION_KEY_GUI_QUEUE_MGR_DEL_BUTTON_TOOLTIP, "Dieses Element aus der Warteschlange löschen");
        add(Constants.TRANSLATION_KEY_GUI_QUEUE_MGR_PICKER_TITLE, "Gegenstand hinzufügen");
        add(Constants.TRANSLATION_KEY_GUI_QUEUE_MGR_PICKER_SEARCH, "Suchen:");
        add(Constants.KEY_BINDINGS_CATEGORY_TITLE, "Handwerks-Tracker");
        add(Constants.TRANSLATION_KEY_BINDINGS_ADDTOQUEUE_TITLE, "Zur Warteschlange hinzufügen");
        add(Constants.TRANSLATION_KEY_BINDINGS_TOGGLE_CRAFT_QUEUE_TITLE, "Herstellungswarteschlange umschalten");
        add(Constants.TRANSLATION_KEY_BINDINGS_TOGGLE_SHOPPING_LIST_TITLE, "Einkaufsliste umschalten");
        add(Constants.TRANSLATION_KEY_BINDINGS_OPEN_QMGR_TITLE, "Warteschlangenmanager öffnen");
        add(Constants.TRANSLATION_KEY_BINDINGS_POPULATE_SHOPPING_LIST_TITLE, "Einkaufsliste füllen");
        add(Constants.TRANSLATION_KEY_BINDINGS_CLEAR_SHOPPING_LIST_TITLE, "Einkaufsliste löschen");
        add(Constants.TRANSLATION_KEY_GUI_MSG_QUEUE_OVERLAY_MODE_HIDE, "Die Überlagerung der Handwerksliste wird nun ausgeblendet.");
        add(Constants.TRANSLATION_KEY_GUI_MSG_QUEUE_OVERLAY_MODE_SHOW, "Jetzt wird die Überlagerung mit der Handwerksliste angezeigt.");
        add(Constants.TRANSLATION_KEY_GUI_MSG_QUEUE_OVERLAY_MODE_DYNAMIC, "Der Überlagerungsmodus der Handwerksliste ist dynamisch.");
        add(Constants.TRANSLATION_KEY_GUI_MSG_SLIST_OVERLAY_MODE_HIDE, "Die Einkaufslisten-Overlay wird nun ausgeblendet.");
        add(Constants.TRANSLATION_KEY_GUI_MSG_SLIST_OVERLAY_MODE_SHOW, "Nun wird das Overlay mit der Einkaufsliste angezeigt.");
        add(Constants.TRANSLATION_KEY_GUI_MSG_SLIST_OVERLAY_MODE_DYNAMIC, "Der Einkaufslisten-Overlay-Modus ist dynamisch.");

        add(Constants.TRANSLATION_KEY_CONFIG_CLIENT_CRAFT_QUEUE_X, "Die X-Position auf dem Bildschirm für die Überlagerung der Handwerkswarteschlange");
        add(Constants.TRANSLATION_KEY_CONFIG_CLIENT_CRAFT_QUEUE_Y, "Die Y-Position auf dem Bildschirm für die Handwerkswarteschlangenüberlagerung");
        add(Constants.TRANSLATION_KEY_CONFIG_CLIENT_CRAFT_QUEUE_WIDTH, "Die Breite der Craft-Warteschlangenüberlagerung");
        add(Constants.TRANSLATION_KEY_CONFIG_CLIENT_CRAFT_QUEUE_HEIGHT, "Die Höhe der Craft-Warteschlangenüberlagerung");
        add(Constants.TRANSLATION_KEY_CONFIG_CLIENT_SHOPPING_LIST_X, "Die X-Position auf dem Bildschirm für die Einkaufslisten-Überlagerung");
        add(Constants.TRANSLATION_KEY_CONFIG_CLIENT_SHOPPING_LIST_Y, "Die Y-Position auf dem Bildschirm für die Einkaufslisten-Überlagerung");
        add(Constants.TRANSLATION_KEY_CONFIG_CLIENT_SHOPPING_LIST_WIDTH, "Die Breite des Einkaufslisten-Overlays");
        add(Constants.TRANSLATION_KEY_CONFIG_CLIENT_SHOPPING_LIST_HEIGHT, "Die Höhe des Einkaufslisten-Overlays");
        add(Constants.TRANSLATION_KEY_CONFIG_CLIENT_CRAFT_QUEUE_HIDE_EMPTY, "Soll das Overlay der Herstellungswarteschlange ausgeblendet werden, wenn es leer ist?");
        add(Constants.TRANSLATION_KEY_CONFIG_CLIENT_SHOPPING_LIST_HIDE_EMPTY, "Soll das Einkaufslisten-Overlay ausgeblendet werden, wenn es leer ist?");

        add(Constants.TRANSLATION_KEY_ADVANCEMENT_ROOT_TITLE, "Tracking-Handwerk");
        add(Constants.TRANSLATION_KEY_ADVANCEMENT_ROOT_DESCRIPTION, "Behalten Sie den Überblick über alle Dinge, die Sie zum Herstellen benötigen");
        add(Constants.TRANSLATION_KEY_ADVANCEMENT_QUEUE_ITEM_TITLE, "Denken Sie daran");
        add(Constants.TRANSLATION_KEY_ADVANCEMENT_QUEUE_ITEM_DESCRIPTION, "Einen Gegenstand zur Herstellungswarteschlange hinzufügen");
        add(Constants.TRANSLATION_KEY_ADVANCEMENT_CRAFT_ITEM_TITLE, "Arbeiten");
        add(Constants.TRANSLATION_KEY_ADVANCEMENT_CRAFT_ITEM_DESCRIPTION, "Einen in der Warteschlange befindlichen Gegenstand herstellen");
        add(Constants.TRANSLATION_KEY_ADVANCEMENT_POPULATE_SHOPPING_LIST_TITLE, "To-do-Liste");
        add(Constants.TRANSLATION_KEY_ADVANCEMENT_POPULATE_SHOPPING_LIST_DESCRIPTION, "Die Einkaufsliste füllen");
        add(Constants.TRANSLATION_KEY_ADVANCEMENT_ACQUIRE_LIST_ITEM_TITLE, "Einfach weiter einkaufen");
        add(Constants.TRANSLATION_KEY_ADVANCEMENT_ACQUIRE_LIST_ITEM_DESCRIPTION, "Einen Artikel erwerben, der auf der Einkaufsliste steht");
        add(Constants.TRANSLATION_KEY_ADVANCEMENT_CLEAR_QUEUE_ITEM_TITLE, "Wie ein Boss");
        add(Constants.TRANSLATION_KEY_ADVANCEMENT_CLEAR_QUEUE_ITEM_DESCRIPTION, "Leeren Sie die Herstellungswarteschlange vollständig.");
    }
}
