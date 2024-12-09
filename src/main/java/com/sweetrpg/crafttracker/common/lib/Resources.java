package com.sweetrpg.crafttracker.common.lib;

import com.sweetrpg.crafttracker.common.util.Util;
import net.minecraft.resources.ResourceLocation;

public class Resources {

    public static final ResourceLocation SMALL_WIDGETS = getGui("small_widgets");

    public static ResourceLocation getGui(String textureFileName) {
        return Util.getResource("textures/gui/" + textureFileName + ".png");
    }
}
