package com.sweetrpg.crafttracker.common.util;

import net.minecraft.resources.ResourceLocation;

public class Resources {

    public static final ResourceLocation SMALL_WIDGETS = getGui("small_widgets");

    public static ResourceLocation getGui(String textureFileName) {
        return Util.getResource("textures/gui/" + textureFileName + ".png");
    }
}
