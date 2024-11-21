package com.sweetrpg.crafttracker.client.overlay;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.addon.jei.CTPlugin;
import com.sweetrpg.crafttracker.common.config.ConfigHandler;
import com.sweetrpg.crafttracker.common.lib.Constants;
import com.sweetrpg.crafttracker.common.manager.CraftingQueueManager;
import mezz.jei.api.constants.VanillaTypes;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.registries.ForgeRegistries;

public class CraftQueueOverlay {

    static int TITLE_COLOR = 0x99999999;
    static int SECTION_COLOR = 0xcccccccc;
    static int TEXT_COLOR = 0xffffffff;
    static int MESSAGE_COLOR = 0x66666666;
    static int SECTION_X_OFFSET = 4;
    static int SECTION_TITLE_Y_OFFSET = 20;
    static int ITEM_NAME_X_OFFSET = 22;
    static int LINE_HEIGHT = 16;
    static int TEXT_HEIGHT = 12;
    static int MAX_STRING_LENGTH = 40;

    public static final IIngameOverlay CRAFT_QUEUE = (gui, poseStack, partialTicks, width, height) -> {
        CraftTracker.LOGGER.trace("CRAFT_QUEUE");

        if(ConfigHandler.CLIENT.CRAFT_QUEUE_OVERLAY_HIDE_EMPTY.get() /* TODO: || user wants it to display */) {
//            return;
        }

        var x = ConfigHandler.CLIENT.CRAFT_QUEUE_OVERLAY_X.get();
        var y = ConfigHandler.CLIENT.CRAFT_QUEUE_OVERLAY_Y.get();
        var olWidth = Math.min((ConfigHandler.CLIENT.CRAFT_QUEUE_OVERLAY_X.get() + ConfigHandler.CLIENT.CRAFT_QUEUE_OVERLAY_WIDTH.get()), width - 10);
        var olHeight = Math.min((ConfigHandler.CLIENT.CRAFT_QUEUE_OVERLAY_Y.get() + ConfigHandler.CLIENT.CRAFT_QUEUE_OVERLAY_HEIGHT.get()), height - 10);
        var backgroundColor = 0x5f5f5f5f; // TODO: get from config
        var borderColor = 0x1f1f1f1f; // TODO: get from config

        GuiComponent.fill(poseStack, x, y, olWidth, olHeight, borderColor);
        GuiComponent.fill(poseStack, x + 2, y + 2, olWidth - 2, olHeight - 2, backgroundColor);

        GuiComponent.drawCenteredString(poseStack, gui.getFont(),
                new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_CRAFTLIST_TITLE),
                (x + olWidth - 8) / 2, y + 6, TITLE_COLOR);

        var mgr = CraftingQueueManager.INSTANCE;
        var products = mgr.getEndProducts();

        // if products list is empty, display "empty" message
        if(products.isEmpty()) { // TODO
            GuiComponent.drawCenteredString(poseStack, gui.getFont(),
                    new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_CRAFTLIST_EMPTY),
                    (x + olWidth - 8) / 2, (y + olHeight - 6) / 2, MESSAGE_COLOR);
            return;
        }

        int yPos = y + SECTION_TITLE_Y_OFFSET;
        CraftTracker.LOGGER.debug("yPos (initial): {}", yPos);

        // SECTION: end products

        // title
        GuiComponent.drawString(poseStack, gui.getFont(),
                new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_CRAFTLIST_SECTION_PRODUCTS),
                x + SECTION_X_OFFSET, yPos, SECTION_COLOR);
        yPos += TEXT_HEIGHT + 2;
        CraftTracker.LOGGER.debug("yPos (after product title): {}", yPos);

        // items
        for(int i = 0; i < products.size(); i++) {
            var p = products.get(i);
//            var index = products.indexOf(p);

            var item = ForgeRegistries.ITEMS.getValue(p.getItemId());
            var stack = item.getDefaultInstance();
            // TODO: count overlay on icon is amount produced by recipe
            //  stack.setCount(stack.getCount());
            var drawable = CTPlugin.jeiRuntime.getJeiHelpers().getGuiHelper()
                    .createDrawableIngredient(VanillaTypes.ITEM_STACK, stack);
            drawable.draw(poseStack, x + SECTION_X_OFFSET, yPos);
            var text = String.format("%s (x%d)", item.getDescription().getString(MAX_STRING_LENGTH), p.getQuantity());
            GuiComponent.drawString(poseStack, gui.getFont(), text, x + ITEM_NAME_X_OFFSET, yPos + 4, TEXT_COLOR);

            yPos += LINE_HEIGHT + 2;
            CraftTracker.LOGGER.debug("yPos (product item {}): {}", i, yPos);
        }

        // SECTION: intermediates
        if(!mgr.getIntermediates().isEmpty()) {
            yPos += (TEXT_HEIGHT * 2);
            CraftTracker.LOGGER.debug("yPos (before intermediates title): {}", yPos);

            // title
            GuiComponent.drawString(poseStack, gui.getFont(),
                    new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_CRAFTLIST_SECTION_INTERMEDIATES),
                    x + SECTION_X_OFFSET, yPos, SECTION_COLOR);
            yPos += TEXT_HEIGHT + 2;
            CraftTracker.LOGGER.debug("yPos (after intermediates title): {}", yPos);

            // items
            for(int i = 0; i < mgr.getIntermediates().size(); i++) {
                var inter = mgr.getIntermediates().get(i);
//                var index = mgr.getIntermediates().indexOf(i);

                var item = ForgeRegistries.ITEMS.getValue(inter.getItemId());
                var stack = item.getDefaultInstance();
                stack.setCount(inter.getQuantity());
                var drawable = CTPlugin.jeiRuntime.getJeiHelpers().getGuiHelper()
                        .createDrawableIngredient(VanillaTypes.ITEM_STACK, stack);
                drawable.draw(poseStack, x + SECTION_X_OFFSET, yPos);
                GuiComponent.drawString(poseStack, gui.getFont(), item.getDescription().getString(MAX_STRING_LENGTH), x + ITEM_NAME_X_OFFSET, yPos + 4, TEXT_COLOR);

                yPos += LINE_HEIGHT + 2;
                CraftTracker.LOGGER.debug("yPos (intermediates item {}): {}", i, yPos);
            }
        }

        // SECTION: raw materials
        if(!mgr.getRawMaterials().isEmpty()) {
            yPos += (TEXT_HEIGHT * 2);
            CraftTracker.LOGGER.debug("yPos (before materials title): {}", yPos);

            // title
            GuiComponent.drawString(poseStack, gui.getFont(),
                    new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_CRAFTLIST_SECTION_MATERIALS),
                    x + SECTION_X_OFFSET, yPos, SECTION_COLOR);
            yPos += TEXT_HEIGHT + 2;
            CraftTracker.LOGGER.debug("yPos (after materials title): {}", yPos);

            // items
            for(int i = 0; i < mgr.getRawMaterials().size(); i++) {
                var m = mgr.getRawMaterials().get(i);
//                var index = mgr.getRawMaterials().indexOf(m);

                var item = ForgeRegistries.ITEMS.getValue(m.getItemId());
                var stack = item.getDefaultInstance();
                stack.setCount(m.getQuantity());
                var drawable = CTPlugin.jeiRuntime.getJeiHelpers().getGuiHelper()
                        .createDrawableIngredient(VanillaTypes.ITEM_STACK, stack);
                drawable.draw(poseStack, x + SECTION_X_OFFSET, yPos);
                GuiComponent.drawString(poseStack, gui.getFont(), item.getDescription().getString(MAX_STRING_LENGTH), x + ITEM_NAME_X_OFFSET, yPos + 4, TEXT_COLOR);

                yPos += LINE_HEIGHT + 2;
                CraftTracker.LOGGER.debug("yPos (materials item {}): {}", i, yPos);
            }
        }

        // SECTION: fuel

        if(!mgr.getFuel().isEmpty()) {
            yPos += (TEXT_HEIGHT * 2);
            CraftTracker.LOGGER.debug("yPos (before fuel title): {}", yPos);

            // title
            GuiComponent.drawString(poseStack, gui.getFont(),
                    new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_CRAFTLIST_SECTION_FUEL),
                    x + SECTION_X_OFFSET, yPos, SECTION_COLOR);
            yPos += TEXT_HEIGHT + 2;
            CraftTracker.LOGGER.debug("yPos: {}", yPos);

            // items
            for(var f : mgr.getFuel()) {

            }
        }
    };

}
