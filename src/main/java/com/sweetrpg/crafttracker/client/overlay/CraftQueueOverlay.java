package com.sweetrpg.crafttracker.client.overlay;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.addon.jei.CTPlugin;
import com.sweetrpg.crafttracker.common.config.ConfigHandler;
import com.sweetrpg.crafttracker.common.lib.CTRuntime;
import com.sweetrpg.crafttracker.common.lib.Constants;
import com.sweetrpg.crafttracker.common.manager.CraftingQueueManager;
import com.sweetrpg.crafttracker.common.registry.ModKeyBindings;
import com.sweetrpg.crafttracker.common.util.InventoryUtil;
import mezz.jei.api.constants.VanillaTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.registries.ForgeRegistries;

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

    public static final IIngameOverlay CRAFT_QUEUE = (gui, poseStack, partialTicks, width, height) -> {
        CraftTracker.LOGGER.trace("CRAFT_QUEUE");

        var mgr = CraftingQueueManager.INSTANCE;
        var products = mgr.getEndProducts().stream().sorted((i1, i2) -> {
            var item1 = ForgeRegistries.ITEMS.getValue(i1.getProductId());
            if(item1 == null) return 0;
            var item2 = ForgeRegistries.ITEMS.getValue(i2.getProductId());
            if(item2 == null) return 0;
            return item1.getDescription().getString().compareTo(item2.getDescription().getString());
        }).toList();

        switch(CTRuntime.INSTANCE.queueOverlayRequestedState) {
            case SHOW:
                //
                break;

            case HIDE:
            case SUPPRESS:
                return;

            case DYNAMIC:
                if(ConfigHandler.CLIENT.CRAFT_QUEUE_OVERLAY_HIDE_EMPTY.get() &&
                        products.isEmpty()) {
                    return;
                }
                break;
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
                new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_CRAFT_QUEUE_TITLE),
                (x + olWidth - 8) / 2, y + 6, TITLE_COLOR);

        // if products list is empty, display "empty" message
        if(products.isEmpty()) {
            GuiComponent.drawCenteredString(poseStack, gui.getFont(),
                    new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_CRAFT_QUEUE_EMPTY),
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

        // SECTION: end products

        // title
        GuiComponent.drawString(poseStack, gui.getFont(),
                new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_CRAFT_QUEUE_SECTION_PRODUCTS),
                x + SECTION_X_OFFSET, yPos, SECTION_COLOR);
        yPos += TEXT_HEIGHT + 2;
        CraftTracker.LOGGER.trace("yPos (after product title): {}", yPos);

        // items
        for(int i = 0; i < products.size(); i++) {
            var p = products.get(i);

            var item = ForgeRegistries.ITEMS.getValue(p.getProductId());
            if(item == null) {
                continue;
            }
            var stack = item.getDefaultInstance();
            var selectedRecipe = p.getRecipes().get(p.getIndex());
            var amountProduced = selectedRecipe.getResultItem().getCount() * p.getIterations();
            stack.setCount(amountProduced);
            var drawable = CTPlugin.jeiRuntime.getJeiHelpers().getGuiHelper()
                    .createDrawableIngredient(VanillaTypes.ITEM_STACK, stack);
            drawable.draw(poseStack, x + SECTION_X_OFFSET, yPos);
            var text = String.format("%s (x%d)", item.getDescription().getString(MAX_STRING_LENGTH), p.getIterations());
            GuiComponent.drawString(poseStack, gui.getFont(), text, x + ITEM_NAME_X_OFFSET, yPos + 4, TEXT_COLOR);

            yPos += LINE_HEIGHT + 2;
            CraftTracker.LOGGER.trace("yPos (product item {}): {}", i, yPos);
        }

        Player player = Minecraft.getInstance().player;
        var inventory = player.getInventory();

        // SECTION: intermediates

        if(!mgr.getIntermediates().isEmpty()) {
            yPos += (int) (TEXT_HEIGHT * 1.5);
            CraftTracker.LOGGER.trace("yPos (before intermediates title): {}", yPos);

            // title
            GuiComponent.drawString(poseStack, gui.getFont(),
                    new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_CRAFT_QUEUE_SECTION_INTERMEDIATES),
                    x + SECTION_X_OFFSET, yPos, SECTION_COLOR);
            yPos += TEXT_HEIGHT + 2;
            CraftTracker.LOGGER.trace("yPos (after intermediates title): {}", yPos);

            // items
            var sortedIntermediates = mgr.getIntermediates().stream().sorted((i1, i2) -> {
                var item1 = ForgeRegistries.ITEMS.getValue(i1.getItemId());
                if(item1 == null) return 0;
                var item2 = ForgeRegistries.ITEMS.getValue(i2.getItemId());
                if(item2 == null) return 0;
                return item1.getDescription().getString().compareTo(item2.getDescription().getString());
            }).toList();
            for(int i = 0; i < sortedIntermediates.size(); i++) {
                var inter = sortedIntermediates.get(i);

                var item = ForgeRegistries.ITEMS.getValue(inter.getItemId());
                var stack = item.getDefaultInstance();
                stack.setCount(inter.getQuantity());

                int playerHasQuantity = InventoryUtil.getQuantityOf(player, inter.getItemId());
                if(playerHasQuantity >= inter.getQuantity()) {
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
                CraftTracker.LOGGER.trace("yPos (intermediates item {}): {}", i, yPos);
            }
        }

        // SECTION: raw materials

        if(!mgr.getRawMaterials().isEmpty()) {
            yPos += (int) (TEXT_HEIGHT * 1.5);
            CraftTracker.LOGGER.trace("yPos (before materials title): {}", yPos);

            // title
            GuiComponent.drawString(poseStack, gui.getFont(),
                    new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_CRAFT_QUEUE_SECTION_MATERIALS),
                    x + SECTION_X_OFFSET, yPos, SECTION_COLOR);
            yPos += TEXT_HEIGHT + 2;
            CraftTracker.LOGGER.trace("yPos (after materials title): {}", yPos);

            // items
            var sortedMaterials = mgr.getRawMaterials().stream().sorted((i1, i2) -> {
                var item1 = ForgeRegistries.ITEMS.getValue(i1.getItemId());
                var item2 = ForgeRegistries.ITEMS.getValue(i2.getItemId());
                return item1.getDescription().getString().compareTo(item2.getDescription().getString());
            }).toList();
            for(int i = 0; i < sortedMaterials.size(); i++) {
                var m = sortedMaterials.get(i);

                var item = ForgeRegistries.ITEMS.getValue(m.getItemId());
                var stack = item.getDefaultInstance();
                stack.setCount(m.getQuantity());

                int playerHasQuantity = InventoryUtil.getQuantityOf(player, m.getItemId());
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
        }

        // SECTION: fuel

        if(!mgr.getFuel().isEmpty()) {
            yPos += (int) (TEXT_HEIGHT * 1.5);
            CraftTracker.LOGGER.trace("yPos (before fuel title): {}", yPos);

            // title
            GuiComponent.drawString(poseStack, gui.getFont(),
                    new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_CRAFT_QUEUE_SECTION_FUEL),
                    x + SECTION_X_OFFSET, yPos, SECTION_COLOR);
            yPos += TEXT_HEIGHT + 2;
            CraftTracker.LOGGER.trace("yPos: {}", yPos);

            // items
            var sortedFuels = mgr.getFuel().stream().sorted((i1, i2) -> {
                var item1 = ForgeRegistries.ITEMS.getValue(i1.getItemId());
                var item2 = ForgeRegistries.ITEMS.getValue(i2.getItemId());
                return item1.getDescription().getString().compareTo(item2.getDescription().getString());
            }).toList();
            for(int i = 0; i < sortedFuels.size(); i++) {
                var f = sortedFuels.get(i);

                var item = ForgeRegistries.ITEMS.getValue(f.getItemId());
                var stack = item.getDefaultInstance();
                stack.setCount(f.getQuantity());

                int playerHasQuantity = InventoryUtil.getQuantityOf(player, f.getItemId());
                if(playerHasQuantity >= f.getQuantity()) {
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
        }
    };

}
