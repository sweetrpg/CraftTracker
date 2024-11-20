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

    static int TEXT_COLOR = 0xffffffff;
    static int SECTION_X_OFFSET = 4;
    static int SECTION_TITLE_Y_OFFSET = 18;
    static int ITEM_NAME_X_OFFSET = 22;
    static int LINE_HEIGHT = 8;
    static int MAX_STRING_LENGTH = 16;


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
                (x + olWidth - 8) / 2, y + 6, TEXT_COLOR);

        var mgr = CraftingQueueManager.INSTANCE;
        var products = mgr.getEndProducts();

        // if products list is empty, display "empty" message
        if(products.isEmpty()) { // TODO
            GuiComponent.drawCenteredString(poseStack, gui.getFont(),
                    new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_CRAFTLIST_EMPTY),
                    (x + olWidth - 8) / 2, (y + olHeight - 6) / 2, TEXT_COLOR);
            return;
        }

        // end products

        // title
        GuiComponent.drawString(poseStack, gui.getFont(),
                new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_CRAFTLIST_SECTION_PRODUCTS),
                x + SECTION_X_OFFSET, y + SECTION_TITLE_Y_OFFSET, TEXT_COLOR);

        int yPos = y + (LINE_HEIGHT * 2) + 2;

        // items
        for(var p : products) {
            var index = products.indexOf(p);

            var item = ForgeRegistries.ITEMS.getValue(p.getItemId());
//            var image = item.getDefaultInstance().getTooltipImage();

            yPos += (index * LINE_HEIGHT);

            var stack = item.getDefaultInstance();
            stack.setCount(p.getQuantity());
            var drawable = CTPlugin.jeiRuntime.getJeiHelpers().getGuiHelper()
                    .createDrawableIngredient(VanillaTypes.ITEM_STACK, stack);
            drawable.draw(poseStack, x + SECTION_X_OFFSET, yPos);
//            GuiComponent.blit(poseStack, 2, index * 16, 0, 16, 16, image);
            GuiComponent.drawString(poseStack, gui.getFont(), item.getDescription().getString(MAX_STRING_LENGTH), x + ITEM_NAME_X_OFFSET, yPos + 2, TEXT_COLOR);
        }

        // intermediates
        // TODO: title
        // TODO: items

        // raw materials
        // TODO: title
        // TODO: items

        // fuel
        // TODO: title
        // TODO: items
    };

}
