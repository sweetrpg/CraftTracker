package com.sweetrpg.crafttracker.common.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;

/**
 * Keyboard handling utility functions
 */
public class KeyUtil {

    /**
     * Check if the specified key is currently pressed.
     *
     * @param key The key code to check
     * @return A boolean value indicating if the key is down or not
     */
    public static boolean isKeyDown(int key) {
        return InputConstants.isKeyDown(Minecraft.getInstance()
                .getWindow()
                .getWindow(), key);
    }

}
