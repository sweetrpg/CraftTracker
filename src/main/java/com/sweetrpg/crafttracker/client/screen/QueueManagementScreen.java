package com.sweetrpg.crafttracker.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sweetrpg.crafttracker.common.lib.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;

public class QueueManagementScreen extends Screen {

    public final Player player;

    public QueueManagementScreen(Player player) {
        super(new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_QUEUEMGR_TITLE));
        this.player = player;
    }

    public static void open() {
        Minecraft mc = Minecraft.getInstance();
        mc.setScreen(new QueueManagementScreen(mc.player));
    }

    @Override
    public void init() {
        super.init();

        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);

        int topX = this.width / 2;
        int topY = this.height / 2;

        // TODO: hide queue overlay and shopping list




    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        //Background
        int topX = this.width / 2;
        int topY = this.height / 2;

        this.renderBackground(stack);


    }

    @Override
    public void removed() {
        super.removed();

        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);

        // TODO: restore queue overlay and shopping list
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

}
