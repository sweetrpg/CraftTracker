package com.sweetrpg.crafttracker.client.overlay;

import com.sweetrpg.crafttracker.common.config.ConfigHandler;
import com.sweetrpg.crafttracker.common.lib.Constants;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.client.gui.IIngameOverlay;

public class CraftQueueOverlay {

    public static final IIngameOverlay CRAFT_QUEUE = (gui, poseStack, partialTicks, width, height) -> {
        if(ConfigHandler.CLIENT.CRAFT_QUEUE_OVERLAY_HIDE_EMPTY.get() /* TODO: || user wants it to display */) {
//            return;
        }

        var x = ConfigHandler.CLIENT.CRAFT_QUEUE_OVERLAY_X.get();
        var y = ConfigHandler.CLIENT.CRAFT_QUEUE_OVERLAY_Y.get();
        var olWidth = Math.min((ConfigHandler.CLIENT.CRAFT_QUEUE_OVERLAY_X.get() + ConfigHandler.CLIENT.CRAFT_QUEUE_OVERLAY_WIDTH.get()), width - 10);
        var olHeight = Math.min((ConfigHandler.CLIENT.CRAFT_QUEUE_OVERLAY_Y.get() + ConfigHandler.CLIENT.CRAFT_QUEUE_OVERLAY_HEIGHT.get()), height - 10);

        GuiComponent.fill(poseStack, x, y, olWidth, olHeight, 0x1f1f1f1f);
        GuiComponent.fill(poseStack, x + 2, y + 2, olWidth - 2, olHeight - 2, 0x5f5f5f5f);

        GuiComponent.drawCenteredString(poseStack, gui.getFont(),
                new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_CRAFTLIST_TITLE),
                (x + olWidth - 8) / 2, y + 6, 0xffffffff);
    };

}
