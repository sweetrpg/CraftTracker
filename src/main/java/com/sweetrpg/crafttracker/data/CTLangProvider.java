package com.sweetrpg.crafttracker.data;

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
        add(Constants.TRANSLATION_KEY_GUI_CRAFTLIST_TITLE, "Craft List");
    }

    private void processENGB() {
        add(Constants.TRANSLATION_KEY_GUI_CRAFTLIST_TITLE, "Craft List");

    }

    private void processDEDE() {
        add(Constants.TRANSLATION_KEY_GUI_CRAFTLIST_TITLE, "Craft List");
    }

}
