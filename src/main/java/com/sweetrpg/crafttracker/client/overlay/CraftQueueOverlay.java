package com.sweetrpg.crafttracker.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.Constants;
import com.sweetrpg.crafttracker.common.Runtime;
import com.sweetrpg.crafttracker.common.config.ConfigHandler;
import com.sweetrpg.crafttracker.common.manager.CraftingQueueManager;
import com.sweetrpg.crafttracker.common.model.CraftingQueueItem;
import com.sweetrpg.crafttracker.common.model.CraftingQueueProduct;
import com.sweetrpg.crafttracker.common.registry.ModKeyBindings;
import com.sweetrpg.crafttracker.common.util.InventoryUtil;
import com.sweetrpg.crafttracker.common.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class CraftQueueOverlay {

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

    @SubscribeEvent
    public void onRenderGuiOverlay(RenderGuiOverlayEvent event) {
        CraftTracker.LOGGER.trace("CraftQueueOverlay#onRenderGuiOverlay");

        final Minecraft mc = Minecraft.getInstance();
        final ForgeGui gui = (ForgeGui) mc.gui;
        final PoseStack poseStack = event.getPoseStack();
        final int width = mc.getWindow().getWidth();
        final int height = mc.getWindow().getHeight();

        CraftingQueueManager mgr = CraftingQueueManager.INSTANCE;
        List<CraftingQueueProduct> products = mgr.getEndProducts().stream().sorted((i1, i2) -> {
            Item item1 = ForgeRegistries.ITEMS.getValue(i1.getProductId());
            if(item1 == null) return 0;
            Item item2 = ForgeRegistries.ITEMS.getValue(i2.getProductId());
            if(item2 == null) return 0;
            return item1.getDescription().getString().compareTo(item2.getDescription().getString());
        }).toList();

        switch(Runtime.INSTANCE.queueOverlayRequestedState) {
            case SHOW:
                //
                break;

            case HIDE:
            case SUPPRESS:
                return;

            case DYNAMIC:
                if(ConfigHandler.CLIENT.craftQueueOverlayHideEmpty.get() &&
                        products.isEmpty()) {
                    return;
                }
                break;
        }

        int x = ConfigHandler.CLIENT.craftQueueOverlayX.get();
        if(x < 0) {
            x = width - (ConfigHandler.CLIENT.craftQueueOverlayX.get() + Math.abs(x));
        }
        int y = ConfigHandler.CLIENT.craftQueueOverlayY.get();
        if(x < 0) {
            y = width - (ConfigHandler.CLIENT.craftQueueOverlayY.get() + Math.abs(y));
        }
        int olWidth = Math.min((ConfigHandler.CLIENT.craftQueueOverlayX.get() + ConfigHandler.CLIENT.craftQueueOverlayWidth.get()), width - 10);
        int olHeight = Math.min((ConfigHandler.CLIENT.craftQueueOverlayY.get() + ConfigHandler.CLIENT.craftQueueOverlayHeight.get()), height - 10);
        int backgroundColor = Util.parseColor(ConfigHandler.CLIENT.craftQueueOverlayBackgroundColor.get(), 16, Constants.BACKGROUND_COLOR);
        int borderColor = Util.parseColor(ConfigHandler.CLIENT.craftQueueOverlayBorderColor.get(), 16, Constants.BORDER_COLOR);

        GuiComponent.fill(poseStack, x, y, olWidth, olHeight, borderColor);
        GuiComponent.fill(poseStack, x + 2, y + 2, olWidth - 2, olHeight - 2, backgroundColor);

        GuiComponent.drawCenteredString(poseStack, gui.getFont(),
                Component.translatable(Constants.TRANSLATION_KEY_GUI_CRAFT_QUEUE_TITLE),
                (x + olWidth - 8) / 2, y + 6, TITLE_COLOR);

        // if products list is empty, display "empty" message
        if(products.isEmpty()) {
            GuiComponent.drawCenteredString(poseStack, gui.getFont(),
                    Component.translatable(Constants.TRANSLATION_KEY_GUI_CRAFT_QUEUE_EMPTY),
                    (x + olWidth - 8) / 2, (y + olHeight - 6) / 2, MESSAGE_COLOR);
            return;
        }

        var helpText = String.format("%s [%s]",
                I18n.get(Constants.TRANSLATION_KEY_GUI_CRAFT_QUEUE_HELP),
                ModKeyBindings.OPEN_QUEUE_MANAGER_MAPPING.getTranslatedKeyMessage().getString());
        GuiComponent.drawCenteredString(poseStack, gui.getFont(), helpText,
                (x + olWidth - 8) / 2, olHeight - TEXT_HEIGHT, HELP_COLOR);

        int yPos = y + SECTION_TITLE_Y_OFFSET;
        CraftTracker.LOGGER.trace("yPos (initial): {}", yPos);

        // SECTION: products

        // title
        GuiComponent.drawString(poseStack, gui.getFont(),
                Component.translatable(Constants.TRANSLATION_KEY_GUI_CRAFT_QUEUE_SECTION_PRODUCTS),
                x + SECTION_X_OFFSET, yPos, SECTION_COLOR);
        yPos += TEXT_HEIGHT + 2;
        CraftTracker.LOGGER.trace("yPos (after product title): {}", yPos);

        // items
        for(int i = 0; i < products.size(); i++) {
            CraftingQueueProduct p = products.get(i);

            Item item = ForgeRegistries.ITEMS.getValue(p.getProductId());
            if(item == null) {
                continue;
            }
            ItemStack stack = item.getDefaultInstance();

            try {
                Recipe<?> selectedRecipe = getRecipeFor(p);
                int amountProduced = selectedRecipe.getResultItem().getCount() * p.getIterations();
                stack.setCount(amountProduced);

                ItemRenderer itemRenderer = mc.getItemRenderer();
                itemRenderer.renderAndDecorateFakeItem(stack, x + SECTION_X_OFFSET, yPos);
                itemRenderer.renderGuiItemDecorations(mc.font, stack, x + SECTION_X_OFFSET, yPos);

                String text = String.format("%s (x%d)", item.getDescription().getString(MAX_STRING_LENGTH), p.getIterations());
                GuiComponent.drawString(poseStack, gui.getFont(), text, x + ITEM_NAME_X_OFFSET, yPos + 4, TEXT_COLOR);
            }
            catch (RuntimeException e) {
                String text = I18n.get(Constants.TRANSLATION_KEY_GUI_NO_RECIPES, p.getProductId().toString(), p.getIndex());
                GuiComponent.drawString(poseStack, gui.getFont(), text, x + ITEM_NAME_X_OFFSET, yPos + 4, TEXT_COLOR);
            }

            yPos += LINE_HEIGHT + 2;
            CraftTracker.LOGGER.trace("yPos (product item {}): {}", i, yPos);
        }

        Player player = Minecraft.getInstance().player;

        // SECTION: intermediates

        if(!mgr.getIntermediates().isEmpty()) {
            yPos += (int) (TEXT_HEIGHT * 1.5);
            CraftTracker.LOGGER.trace("yPos (before intermediates title): {}", yPos);

            // title
            GuiComponent.drawString(poseStack, gui.getFont(),
                    Component.translatable(Constants.TRANSLATION_KEY_GUI_CRAFT_QUEUE_SECTION_INTERMEDIATES),
                    x + SECTION_X_OFFSET, yPos, SECTION_COLOR);
            yPos += TEXT_HEIGHT + 2;
            CraftTracker.LOGGER.trace("yPos (after intermediates title): {}", yPos);

            // items
            List<CraftingQueueItem> sortedIntermediates = mgr.getIntermediates().stream().sorted((i1, i2) -> {
                Item item1 = ForgeRegistries.ITEMS.getValue(i1.getItemId());
                if(item1 == null) return 0;
                Item item2 = ForgeRegistries.ITEMS.getValue(i2.getItemId());
                if(item2 == null) return 0;
                return item1.getDescription().getString().compareTo(item2.getDescription().getString());
            }).toList();
            for(int i = 0; i < sortedIntermediates.size(); i++) {
                CraftingQueueItem inter = sortedIntermediates.get(i);

                Item item = ForgeRegistries.ITEMS.getValue(inter.getItemId());
                ItemStack stack = item.getDefaultInstance();
                stack.setCount(inter.getAmount());

                int playerHasQuantity = InventoryUtil.getQuantityOf(player, inter.getItemId());
                if(playerHasQuantity >= inter.getAmount()) {
                    // don't need to display this intermediate, since the user doesn't need to make it
                    continue;
                }

                ItemRenderer itemRenderer = mc.getItemRenderer();
                itemRenderer.renderAndDecorateFakeItem(stack, x + SECTION_X_OFFSET, yPos);
                itemRenderer.renderGuiItemDecorations(mc.font, stack, x + SECTION_X_OFFSET, yPos);

                final int lambdaYpos = yPos;
                if(playerHasQuantity > 0) {
                    String countText = I18n.get(Constants.TRANSLATION_KEY_GUI_HAVE, playerHasQuantity);
                    String text = String.format("%s%s [%s]",
                            item.getDescription().getString(MAX_STRING_LENGTH - countText.length() - 3),
                            inter.isTag() ? "*" : "",
                            countText);
                    CraftTracker.LOGGER.trace("text: {}", text);
                    GuiComponent.drawString(poseStack, gui.getFont(), text, x + ITEM_NAME_X_OFFSET, lambdaYpos + 4, TEXT_COLOR);
                }
                else {
                    String text = item.getDescription().getString(MAX_STRING_LENGTH) +
                            (inter.isTag() ? "*" : "");
                    GuiComponent.drawString(poseStack, gui.getFont(), text, x + ITEM_NAME_X_OFFSET, yPos + 4, TEXT_COLOR);
                }

                yPos += LINE_HEIGHT + 2;
                CraftTracker.LOGGER.trace("yPos (intermediates item {}): {}", i, yPos);
            }
        }

        // SECTION: raw materials

        if(!mgr.getRawMaterials().isEmpty()) {
            yPos += (int) (TEXT_HEIGHT * 1.5);
            CraftTracker.LOGGER.trace("yPos (before materials title): {}", yPos);

            // title
            GuiComponent.drawString(poseStack, gui.getFont(),
                    Component.translatable(Constants.TRANSLATION_KEY_GUI_CRAFT_QUEUE_SECTION_MATERIALS),
                    x + SECTION_X_OFFSET, yPos, SECTION_COLOR);
            yPos += TEXT_HEIGHT + 2;
            CraftTracker.LOGGER.trace("yPos (after materials title): {}", yPos);

            // items
            List<CraftingQueueItem> sortedMaterials = mgr.getRawMaterials().stream().sorted((i1, i2) -> {
                Item item1 = ForgeRegistries.ITEMS.getValue(i1.getItemId());
                Item item2 = ForgeRegistries.ITEMS.getValue(i2.getItemId());
                return item1.getDescription().getString().compareTo(item2.getDescription().getString());
            }).toList();
            for(int i = 0; i < sortedMaterials.size(); i++) {
                CraftingQueueItem m = sortedMaterials.get(i);

                Item item = ForgeRegistries.ITEMS.getValue(m.getItemId());
                ItemStack stack = item.getDefaultInstance();
                stack.setCount(m.getAmount());

                int playerHasQuantity = InventoryUtil.getQuantityOf(player, m.getItemId());
                if(playerHasQuantity >= m.getAmount()) {
                    // don't need to display this intermediate, since the user doesn't need to make it
                    continue;
                }

                ItemRenderer itemRenderer = mc.getItemRenderer();
                itemRenderer.renderAndDecorateFakeItem(stack, x + SECTION_X_OFFSET, yPos);
                itemRenderer.renderGuiItemDecorations(mc.font, stack, x + SECTION_X_OFFSET, yPos);

                final int lambdaYpos = yPos;
                if(playerHasQuantity > 0) {
                    String countText = I18n.get(Constants.TRANSLATION_KEY_GUI_HAVE, playerHasQuantity);
                    String text = String.format("%s%s [%s]",
                            item.getDescription().getString(MAX_STRING_LENGTH - countText.length() - 3),
                            m.isTag() ? "*" : "",
                            countText);
                    CraftTracker.LOGGER.trace("text: {}", text);
                    GuiComponent.drawString(poseStack, gui.getFont(), text, x + ITEM_NAME_X_OFFSET, lambdaYpos + 4, TEXT_COLOR);
                }
                else {
                    String text = item.getDescription().getString(MAX_STRING_LENGTH) +
                            (m.isTag() ? "*" : "");
                    GuiComponent.drawString(poseStack, gui.getFont(), text, x + ITEM_NAME_X_OFFSET, yPos + 4, TEXT_COLOR);
                }

                yPos += LINE_HEIGHT + 2;
                CraftTracker.LOGGER.trace("yPos (materials item {}): {}", i, yPos);
            }
        }

        // SECTION: fuel

        if(!mgr.getFuel().isEmpty()) {
            yPos += (int) (TEXT_HEIGHT * 1.5);
            CraftTracker.LOGGER.trace("yPos (before fuel title): {}", yPos);

            // title
            GuiComponent.drawString(poseStack, gui.getFont(),
                    Component.translatable(Constants.TRANSLATION_KEY_GUI_CRAFT_QUEUE_SECTION_FUEL),
                    x + SECTION_X_OFFSET, yPos, SECTION_COLOR);
            yPos += TEXT_HEIGHT + 2;
            CraftTracker.LOGGER.trace("yPos: {}", yPos);

            // items
            List<CraftingQueueItem> sortedFuels = mgr.getFuel().stream().sorted((i1, i2) -> {
                Item item1 = ForgeRegistries.ITEMS.getValue(i1.getItemId());
                Item item2 = ForgeRegistries.ITEMS.getValue(i2.getItemId());
                return item1.getDescription().getString().compareTo(item2.getDescription().getString());
            }).toList();
            for(int i = 0; i < sortedFuels.size(); i++) {
                CraftingQueueItem f = sortedFuels.get(i);

                Item item = ForgeRegistries.ITEMS.getValue(f.getItemId());
                ItemStack stack = item.getDefaultInstance();
                stack.setCount(f.getAmount());

                int playerHasQuantity = InventoryUtil.getQuantityOf(player, f.getItemId());
                if(playerHasQuantity >= f.getAmount()) {
                    // don't need to display this intermediate, since the user doesn't need to make it
                    continue;
                }

                ItemRenderer itemRenderer = mc.getItemRenderer();
                itemRenderer.renderAndDecorateFakeItem(stack, x + SECTION_X_OFFSET, yPos);
                itemRenderer.renderGuiItemDecorations(mc.font, stack, x + SECTION_X_OFFSET, yPos);

                final int lambdaYpos = yPos;
                if(playerHasQuantity > 0) {
                    String countText = I18n.get(Constants.TRANSLATION_KEY_GUI_HAVE, playerHasQuantity);
                    String text = String.format("%s%s [%s]",
                            item.getDescription().getString(MAX_STRING_LENGTH - countText.length() - 3),
                            f.isTag() ? "*" : "",
                            countText);
                    CraftTracker.LOGGER.trace("text: {}", text);
                    GuiComponent.drawString(poseStack, gui.getFont(), text, x + ITEM_NAME_X_OFFSET, lambdaYpos + 4, TEXT_COLOR);
                }
                else {
                    String text = item.getDescription().getString(MAX_STRING_LENGTH) +
                            (f.isTag() ? "*" : "");
                    GuiComponent.drawString(poseStack, gui.getFont(), text, x + ITEM_NAME_X_OFFSET, yPos + 4, TEXT_COLOR);
                }

                yPos += LINE_HEIGHT + 2;
                CraftTracker.LOGGER.trace("yPos (materials item {}): {}", i, yPos);
            }
        }
    }

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new CraftQueueOverlay());
    }

    private static Recipe<?> getRecipeFor(CraftingQueueProduct product) {
        try {
            return product.getRecipes().get(product.getIndex());
        }
        catch (RuntimeException e) {
            return product.getRecipes().get(0);
        }
    }

}
