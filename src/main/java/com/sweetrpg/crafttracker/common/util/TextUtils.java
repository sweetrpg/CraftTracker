package com.sweetrpg.crafttracker.common.util;

import com.sweetrpg.crafttracker.common.lib.Constants;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class TextUtils {
    private static final MutableComponent NO_EFFECTS = (new TranslatableComponent("effect.none")).withStyle(ChatFormatting.GRAY);

    /**
     * Syntactic sugar for custom translation keys. Always prefixed with the mod's ID in lang files (e.g. farmersdelight.your.key.here).
     */
    public static MutableComponent getTranslation(String type, String key, Object... args) {
        return new TranslatableComponent(type + "." + Constants.MOD_ID + "." + key, args);
    }

}
