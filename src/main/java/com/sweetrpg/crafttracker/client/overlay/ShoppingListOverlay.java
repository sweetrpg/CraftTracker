package com.sweetrpg.crafttracker.client.overlay;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.Constants;
import com.sweetrpg.crafttracker.common.Runtime;
import com.sweetrpg.crafttracker.common.config.ConfigHandler;
import com.sweetrpg.crafttracker.common.manager.ShoppingListManager;
import com.sweetrpg.crafttracker.common.registry.ModKeyBindings;
import com.sweetrpg.crafttracker.common.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

@OnlyIn(Dist.CLIENT)
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

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new ShoppingListOverlay());
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onRender(RenderGameOverlayEvent.Post event) {
        CraftTracker.LOGGER.trace("SHOPPING_LIST");

        Minecraft mc = Minecraft.getInstance();
        if(mc.gui == null) {
            return;
        }

        ShoppingListManager mgr = ShoppingListManager.INSTANCE;
        List<ShoppingListManager.ListItem> items = mgr.getItems();
        MatrixStack poseStack = event.getMatrixStack();

        switch(Runtime.INSTANCE.shoppingOverlayRequestedState) {
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

        int width = mc.getWindow().getWidth();
        int height = mc.getWindow().getHeight();
        int x = ConfigHandler.CLIENT.shoppingListOverlayX.get();
        if(x < 0) {
            x = width - (ConfigHandler.CLIENT.shoppingListOverlayWidth.get() + Math.abs(x));
        }
        int y = ConfigHandler.CLIENT.shoppingListOverlayY.get();
        if(x < 0) {
            y = width - (ConfigHandler.CLIENT.shoppingListOverlayHeight.get() + Math.abs(y));
        }
        int olWidth = Math.min((x + ConfigHandler.CLIENT.shoppingListOverlayWidth.get()), width - 10);
        int olHeight = Math.min((y + ConfigHandler.CLIENT.shoppingListOverlayHeight.get()), height - 10);
        int backgroundColor = Util.parseColor(ConfigHandler.CLIENT.shoppingListOverlayBackgroundColor.get(), 16, Constants.BACKGROUND_COLOR);
        int borderColor = Util.parseColor(ConfigHandler.CLIENT.shoppingListOverlayBorderColor.get(), 16, Constants.BORDER_COLOR);

        mc.gui.fill(poseStack, x, y, olWidth, olHeight, borderColor);
        mc.gui.fill(poseStack, x + 2, y + 2, olWidth - 2, olHeight - 2, backgroundColor);

        mc.gui.drawCenteredString(poseStack, mc.gui.getFont(),
                new TranslationTextComponent(Constants.TRANSLATION_KEY_GUI_SHOPPING_LIST_TITLE),
                (x + olWidth - 8) / 2, y + 6, TITLE_COLOR);

        // if products list is empty, display "empty" message
        if(items.isEmpty()) {
            mc.gui.drawCenteredString(poseStack, mc.gui.getFont(),
                    new TranslationTextComponent(Constants.TRANSLATION_KEY_GUI_SHOPPING_LIST_EMPTY),
                    (x + olWidth - 8) / 2, (y + olHeight - 6) / 2, MESSAGE_COLOR);
            return;
        }

        String helpText = String.format("%s [%s]",
                I18n.get(Constants.TRANSLATION_KEY_GUI_SHOPPING_LIST_HELP),
                ModKeyBindings.CLEAR_SHOPPING_LIST_MAPPING.getTranslatedKeyMessage().getString());
        mc.gui.drawCenteredString(poseStack, mc.gui.getFont(), helpText,
                (x + olWidth - 8) / 2, olHeight - TEXT_HEIGHT, HELP_COLOR);

        int yPos = y + SECTION_TITLE_Y_OFFSET;
        CraftTracker.LOGGER.trace("yPos (initial): {}", yPos);

        PlayerInventory inventory = mc.player.inventory;

        // items
        List<ShoppingListManager.ListItem> sortedItems = mgr.getItems().stream().sorted((i1, i2) -> {
            Item item1 = ForgeRegistries.ITEMS.getValue(i1.getItemId());
            Item item2 = ForgeRegistries.ITEMS.getValue(i2.getItemId());
            return item1.getDescription().getString().compareTo(item2.getDescription().getString());
        }).toList();
        for(int i = 0; i < sortedItems.size(); i++) {
            ShoppingListManager.ListItem m = sortedItems.get(i);

            Item item = ForgeRegistries.ITEMS.getValue(m.getItemId());
            ItemStack stack = item.getDefaultInstance();
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

            ItemRenderer itemRenderer = mc.getItemRenderer();
            itemRenderer.renderAndDecorateFakeItem(stack, x + SECTION_X_OFFSET, yPos);
            itemRenderer.renderGuiItemDecorations(mc.font, stack, x + SECTION_X_OFFSET, yPos);

            final int lambdaYpos = yPos;
            if(playerHasQuantity > 0) {
                String countText = I18n.get(Constants.TRANSLATION_KEY_GUI_HAVE, playerHasQuantity);
                String text = String.format("%s [%s]",
                        item.getDescription().getString(MAX_STRING_LENGTH - countText.length() - 3),
                        countText);
                CraftTracker.LOGGER.trace("text: {}", text);
                mc.gui.drawString(poseStack, mc.gui.getFont(), text, x + ITEM_NAME_X_OFFSET, lambdaYpos + 4, TEXT_COLOR);
            }
            else {
                String text = item.getDescription().getString(MAX_STRING_LENGTH);
                mc.gui.drawString(poseStack, mc.gui.getFont(), text, x + ITEM_NAME_X_OFFSET, yPos + 4, TEXT_COLOR);
            }

            yPos += LINE_HEIGHT + 2;
            CraftTracker.LOGGER.trace("yPos (materials item {}): {}", i, yPos);
        }
    };

}
