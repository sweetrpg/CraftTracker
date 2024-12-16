package com.sweetrpg.crafttracker.client.overlay;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.config.ConfigHandler;
import com.sweetrpg.crafttracker.common.lib.CTRuntime;
import com.sweetrpg.crafttracker.common.lib.Constants;
import com.sweetrpg.crafttracker.common.manager.ShoppingListManager;
import com.sweetrpg.crafttracker.common.registry.ModKeyBindings;
import com.sweetrpg.crafttracker.common.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class ShoppingListOverlay {

    static int TITLE_COLOR = 0x99999999;
    static int HELP_COLOR = 0x77777777;
    static int TEXT_COLOR = 0xffffffff;
    static int MESSAGE_COLOR = 0x66666666;
    static int SECTION_X_OFFSET = 4;
    static int SECTION_TITLE_Y_OFFSET = 20;
    static int ITEM_NAME_X_OFFSET = 22;
    static int LINE_HEIGHT = 16;
    static int TEXT_HEIGHT = 12;
    static int MAX_STRING_LENGTH = 40;


    @SubscribeEvent
    public void onRenderGuiOverlay(RenderGuiOverlayEvent event) {
        CraftTracker.LOGGER.trace("ShoppingListOverlay#onRenderGuiOverlay");

        final var mc = Minecraft.getInstance();
        final var gui = (ForgeGui) mc.gui;
        final var poseStack = event.getPoseStack();
        final var width = mc.getWindow().getWidth();
        final var height = mc.getWindow().getHeight();

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
                if(ConfigHandler.CLIENT.shoppingListOverlayHideEmpty.get() &&
                        items.isEmpty()) {
                    return;
                }
                break;
        }

        var x = ConfigHandler.CLIENT.shoppingListOverlayX.get();
        if(x < 0) {
            x = width - (ConfigHandler.CLIENT.shoppingListOverlayWidth.get() + Math.abs(x));
        }
        var y = ConfigHandler.CLIENT.shoppingListOverlayY.get();
        if(x < 0) {
            y = width - (ConfigHandler.CLIENT.shoppingListOverlayHeight.get() + Math.abs(y));
        }
        var olWidth = Math.min((x + ConfigHandler.CLIENT.shoppingListOverlayWidth.get()), width - 10);
        var olHeight = Math.min((y + ConfigHandler.CLIENT.shoppingListOverlayHeight.get()), height - 10);
        var backgroundColor = Util.parseColor(ConfigHandler.CLIENT.shoppingListOverlayBackgroundColor.get(), 16, Constants.BACKGROUND_COLOR);
        var borderColor = Util.parseColor(ConfigHandler.CLIENT.shoppingListOverlayBorderColor.get(), 16, Constants.BORDER_COLOR);

        GuiComponent.fill(poseStack, x, y, olWidth, olHeight, borderColor);
        GuiComponent.fill(poseStack, x + 2, y + 2, olWidth - 2, olHeight - 2, backgroundColor);

        GuiComponent.drawCenteredString(poseStack, gui.getFont(),
                Component.translatable(Constants.TRANSLATION_KEY_GUI_SHOPPING_LIST_TITLE),
                (x + olWidth - 8) / 2, y + 6, TITLE_COLOR);

        // if products list is empty, display "empty" message
        if(items.isEmpty()) {
            GuiComponent.drawCenteredString(poseStack, gui.getFont(),
                    Component.translatable(Constants.TRANSLATION_KEY_GUI_SHOPPING_LIST_EMPTY),
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
                        .filter(inv -> ForgeRegistries.ITEMS.getKey(inv.getItem()).equals(m.getItemId()))
                        .map(ItemStack::getCount)
                        .findFirst()
                        .orElse(0);
            }

            if(playerHasQuantity >= m.getQuantity()) {
                // don't need to display this intermediate, since the user doesn't need to make it
                continue;
            }

            ItemRenderer itemRenderer = mc.getItemRenderer();
            itemRenderer.renderAndDecorateFakeItem(stack, x + SECTION_X_OFFSET, yPos);
            itemRenderer.renderGuiItemDecorations(mc.font, stack, x + SECTION_X_OFFSET, yPos);
//            var drawable = CTPlugin.jeiRuntime.getJeiHelpers().getGuiHelper()
//                    .createDrawableIngredient(VanillaTypes.ITEM_STACK, stack);
//            drawable.draw(poseStack, x + SECTION_X_OFFSET, yPos);

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
    }

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new ShoppingListOverlay());
    }
}
