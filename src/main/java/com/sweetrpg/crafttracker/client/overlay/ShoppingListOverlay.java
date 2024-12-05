package com.sweetrpg.crafttracker.client.overlay;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.addon.jei.CTPlugin;
import com.sweetrpg.crafttracker.common.config.ConfigHandler;
import com.sweetrpg.crafttracker.common.lib.CTRuntime;
import com.sweetrpg.crafttracker.common.lib.Constants;
import com.sweetrpg.crafttracker.common.manager.ShoppingListManager;
import com.sweetrpg.crafttracker.common.registry.ModKeyBindings;
import mezz.jei.api.constants.VanillaTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.registries.ForgeRegistries;

public class ShoppingListOverlay {

    static int TITLE_COLOR = 0x99999999;
    static int HELP_COLOR = 0x77777777;
    static int SECTION_COLOR = 0xcccccccc;
    static int TEXT_COLOR = 0xffffffff;
    static int MESSAGE_COLOR = 0x66666666;
    static int SECTION_X_OFFSET = 4;
    static int SECTION_TITLE_Y_OFFSET = 20;
    static int ITEM_NAME_X_OFFSET = 22;
    static int LINE_HEIGHT = 16;
    static int TEXT_HEIGHT = 12;
    static int MAX_STRING_LENGTH = 40;

    public static final IIngameOverlay SHOPPING_LIST = (gui, poseStack, partialTicks, width, height) -> {
        CraftTracker.LOGGER.trace("SHOPPING_LIST");

        var mgr = ShoppingListManager.INSTANCE;
        var items = mgr.getItems();

        switch(CTRuntime.INSTANCE.shoppingOverlayRequestedState) {
            case SHOW:
                //
                break;

            case HIDE:
            case SUPPRESS:
                return;

            case DYNAMIC:
                if(ConfigHandler.CLIENT.SHOPPING_LIST_OVERLAY_HIDE_EMPTY.get() &&
                        items.isEmpty()) {
                    return;
                }
                break;
        }

        var x = ConfigHandler.CLIENT.SHOPPING_LIST_OVERLAY_X.get();
        if(x < 0) {
            x = width - (ConfigHandler.CLIENT.SHOPPING_LIST_OVERLAY_WIDTH.get() + Math.abs(x));
        }
        var y = ConfigHandler.CLIENT.SHOPPING_LIST_OVERLAY_Y.get();
        if(x < 0) {
            y = width - (ConfigHandler.CLIENT.SHOPPING_LIST_OVERLAY_HEIGHT.get() + Math.abs(y));
        }
        var olWidth = Math.min((x + ConfigHandler.CLIENT.SHOPPING_LIST_OVERLAY_WIDTH.get()), width - 10);
        var olHeight = Math.min((y + ConfigHandler.CLIENT.SHOPPING_LIST_OVERLAY_HEIGHT.get()), height - 10);
        var backgroundColor = 0x5f5f5f5f; // TODO: get from config?
        var borderColor = 0x1f1f1f1f; // TODO: get from config?

        GuiComponent.fill(poseStack, x, y, olWidth, olHeight, borderColor);
        GuiComponent.fill(poseStack, x + 2, y + 2, olWidth - 2, olHeight - 2, backgroundColor);

        GuiComponent.drawCenteredString(poseStack, gui.getFont(),
                new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_SHOPPING_LIST_TITLE),
                (x + olWidth - 8) / 2, y + 6, TITLE_COLOR);

        // if products list is empty, display "empty" message
        if(items.isEmpty()) {
            GuiComponent.drawCenteredString(poseStack, gui.getFont(),
                    new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_SHOPPING_LIST_EMPTY),
                    (x + olWidth - 8) / 2, (y + olHeight - 6) / 2, MESSAGE_COLOR);
            return;
        }

        var helpText = String.format("%s [%s]",
                I18n.get(Constants.TRANSLATION_KEY_GUI_SHOPPING_LIST_HELP),
                ModKeyBindings.CLEAR_SHOPPING_LIST_MAPPING.getTranslatedKeyMessage().getString());
        GuiComponent.drawCenteredString(poseStack, gui.getFont(), helpText,
                (x + olWidth - 8) / 2, olHeight - TEXT_HEIGHT, HELP_COLOR);

        int yPos = y + SECTION_TITLE_Y_OFFSET;
        CraftTracker.LOGGER.trace("yPos (initial): {}", yPos);

        var inventory = Minecraft.getInstance().player.getInventory();

        // items
        var sortedItems = mgr.getItems().stream().sorted((i1, i2) -> {
            var item1 = ForgeRegistries.ITEMS.getValue(i1.getItemId());
            var item2 = ForgeRegistries.ITEMS.getValue(i2.getItemId());
            return item1.getDescription().getString().compareTo(item2.getDescription().getString());
        }).toList();
        for(int i = 0; i < sortedItems.size(); i++) {
            var m = sortedItems.get(i);

            var item = ForgeRegistries.ITEMS.getValue(m.getItemId());
            var stack = item.getDefaultInstance();
            stack.setCount(m.getQuantity());

            int playerHasQuantity = 0;
            if(inventory.contains(stack)) {
                playerHasQuantity = inventory.items.stream()
                        .filter(inv -> inv.getItem().getRegistryName().equals(m.getItemId()))
                        .map(inv -> inv.getCount())
                        .findFirst()
                        .orElse(0);
            }

            if(playerHasQuantity >= m.getQuantity()) {
                // don't need to display this intermediate, since the user doesn't need to make it
                continue;
            }

            var drawable = CTPlugin.jeiRuntime.getJeiHelpers().getGuiHelper()
                    .createDrawableIngredient(VanillaTypes.ITEM_STACK, stack);
            drawable.draw(poseStack, x + SECTION_X_OFFSET, yPos);

            final int lambdaYpos = yPos;
            if(playerHasQuantity > 0) {
                var countText = I18n.get(Constants.TRANSLATION_KEY_GUI_HAVE, playerHasQuantity);
                var text = String.format("%s [%s]",
                        item.getDescription().getString(MAX_STRING_LENGTH - countText.length() - 3),
                        countText);
                CraftTracker.LOGGER.trace("text: {}", text);
                GuiComponent.drawString(poseStack, gui.getFont(), text, x + ITEM_NAME_X_OFFSET, lambdaYpos + 4, TEXT_COLOR);
            }
            else {
                var text = item.getDescription().getString(MAX_STRING_LENGTH);
                GuiComponent.drawString(poseStack, gui.getFont(), text, x + ITEM_NAME_X_OFFSET, yPos + 4, TEXT_COLOR);
            }

            yPos += LINE_HEIGHT + 2;
            CraftTracker.LOGGER.trace("yPos (materials item {}): {}", i, yPos);
        }
    };

}
