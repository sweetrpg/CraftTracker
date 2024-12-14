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
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new CraftQueueOverlay());
//        OverlayRegistry.registerOverlayAbove(HOTBAR_ELEMENT, "craft_queue", CraftQueueOverlay.CRAFT_QUEUE);
//        OverlayRegistry.registerOverlayAbove(HOTBAR_ELEMENT, "shopping_list", ShoppingListOverlay.SHOPPING_LIST);
    }

    @SubscribeEvent
    public void onRenderGuiOverlay(RenderGuiOverlayEvent event) {
        CraftTracker.LOGGER.trace("CraftQueueOverlay#onRenderGuiOverlay");

        final var gui = (ForgeGui) Minecraft.getInstance().gui;
        final var graphics = event.getGuiGraphics();
        final var width = Minecraft.getInstance().getWindow().getWidth();
        final var height = Minecraft.getInstance().getWindow().getHeight();

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
                if(ConfigHandler.CLIENT.craftQueueOverlayHideEmpty.get() &&
                        products.isEmpty()) {
                    return;
                }
                break;
        }

        var x = ConfigHandler.CLIENT.craftQueueOverlayX.get();
        var y = ConfigHandler.CLIENT.craftQueueOverlayY.get();
        var olWidth = Math.min((ConfigHandler.CLIENT.craftQueueOverlayX.get() + ConfigHandler.CLIENT.craftQueueOverlayWidth.get()), width - 10);
        var olHeight = Math.min((ConfigHandler.CLIENT.craftQueueOverlayY.get() + ConfigHandler.CLIENT.craftQueueOverlayHeight.get()), height - 10);
        var backgroundColor = 0x015f5f5f; // TODO: get from config
        var borderColor = 0x021f1f1f; // TODO: get from config

        graphics.fill(x, y, olWidth, olHeight, borderColor);
        graphics.fill(x + 2, y + 2, olWidth - 2, olHeight - 2, backgroundColor);

        graphics.drawCenteredString(gui.getFont(),
                Component.translatable(Constants.TRANSLATION_KEY_GUI_CRAFT_QUEUE_TITLE),
                (x + olWidth - 8) / 2, y + 6, TITLE_COLOR);

        // if products list is empty, display "empty" message
        if(products.isEmpty()) {
            graphics.drawCenteredString(gui.getFont(),
                    Component.translatable(Constants.TRANSLATION_KEY_GUI_CRAFT_QUEUE_EMPTY),
                    (x + olWidth - 8) / 2, (y + olHeight - 6) / 2, MESSAGE_COLOR);
            return;
        }

        var helpText = String.format("%s [%s]",
                I18n.get(Constants.TRANSLATION_KEY_GUI_CRAFT_QUEUE_HELP),
                ModKeyBindings.OPEN_QUEUE_MANAGER_MAPPING.getTranslatedKeyMessage().getString());
        graphics.drawCenteredString(gui.getFont(), helpText,
                (x + olWidth - 8) / 2, olHeight - TEXT_HEIGHT, HELP_COLOR);

        int yPos = y + SECTION_TITLE_Y_OFFSET;
        CraftTracker.LOGGER.trace("yPos (initial): {}", yPos);

        // SECTION: end products

        // title
        graphics.drawString(gui.getFont(),
                Component.translatable(Constants.TRANSLATION_KEY_GUI_CRAFT_QUEUE_SECTION_PRODUCTS),
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
            Recipe<?> selectedRecipe = p.getRecipes().get(p.getIndex());
            var amountProduced = selectedRecipe.getResultItem(Minecraft.getInstance().level.registryAccess()).getCount() * p.getIterations();
            stack.setCount(amountProduced);
            var drawable = CTPlugin.jeiRuntime.getJeiHelpers().getGuiHelper()
                    .createDrawableIngredient(VanillaTypes.ITEM_STACK, stack);
            drawable.draw(graphics, x + SECTION_X_OFFSET, yPos);
            var text = String.format("%s (x%d)", item.getDescription().getString(MAX_STRING_LENGTH), p.getIterations());
            graphics.drawString(gui.getFont(), text, x + ITEM_NAME_X_OFFSET, yPos + 4, TEXT_COLOR);

            yPos += LINE_HEIGHT + 2;
            CraftTracker.LOGGER.trace("yPos (product item {}): {}", i, yPos);
        }

        Player player = Minecraft.getInstance().player;

        // SECTION: intermediates

        if(!mgr.getIntermediates().isEmpty()) {
            yPos += (int) (TEXT_HEIGHT * 1.5);
            CraftTracker.LOGGER.trace("yPos (before intermediates title): {}", yPos);

            // title
            graphics.drawString(gui.getFont(),
                    Component.translatable(Constants.TRANSLATION_KEY_GUI_CRAFT_QUEUE_SECTION_INTERMEDIATES),
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
                stack.setCount(inter.getAmount());

                int playerHasQuantity = InventoryUtil.getQuantityOf(player, inter.getItemId());
                if(playerHasQuantity >= inter.getAmount()) {
                    // don't need to display this intermediate, since the user doesn't need to make it
                    continue;
                }

                var drawable = CTPlugin.jeiRuntime.getJeiHelpers().getGuiHelper()
                        .createDrawableIngredient(VanillaTypes.ITEM_STACK, stack);
                drawable.draw(graphics, x + SECTION_X_OFFSET, yPos);

                final int lambdaYpos = yPos;
                if(playerHasQuantity > 0) {
                    var countText = I18n.get(Constants.TRANSLATION_KEY_GUI_HAVE, playerHasQuantity);
                    var text = String.format("%s%s [%s]",
                            item.getDescription().getString(MAX_STRING_LENGTH - countText.length() - 3),
                            inter.isTag() ? "*" : "",
                            countText);
                    CraftTracker.LOGGER.trace("text: {}", text);
                    graphics.drawString(gui.getFont(), text, x + ITEM_NAME_X_OFFSET, lambdaYpos + 4, TEXT_COLOR);
                }
                else {
                    var text = item.getDescription().getString(MAX_STRING_LENGTH) +
                            (inter.isTag() ? "*" : "");
                    graphics.drawString(gui.getFont(), text, x + ITEM_NAME_X_OFFSET, yPos + 4, TEXT_COLOR);
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
            graphics.drawString(gui.getFont(),
                    Component.translatable(Constants.TRANSLATION_KEY_GUI_CRAFT_QUEUE_SECTION_MATERIALS),
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
                stack.setCount(m.getAmount());

                int playerHasQuantity = InventoryUtil.getQuantityOf(player, m.getItemId());
                if(playerHasQuantity >= m.getAmount()) {
                    // don't need to display this intermediate, since the user doesn't need to make it
                    continue;
                }

                var drawable = CTPlugin.jeiRuntime.getJeiHelpers().getGuiHelper()
                        .createDrawableIngredient(VanillaTypes.ITEM_STACK, stack);
                drawable.draw(graphics, x + SECTION_X_OFFSET, yPos);

                final int lambdaYpos = yPos;
                if(playerHasQuantity > 0) {
                    var countText = I18n.get(Constants.TRANSLATION_KEY_GUI_HAVE, playerHasQuantity);
                    var text = String.format("%s%s [%s]",
                            item.getDescription().getString(MAX_STRING_LENGTH - countText.length() - 3),
                            m.isTag() ? "*" : "",
                            countText);
                    CraftTracker.LOGGER.trace("text: {}", text);
                    graphics.drawString(gui.getFont(), text, x + ITEM_NAME_X_OFFSET, lambdaYpos + 4, TEXT_COLOR);
                }
                else {
                    var text = item.getDescription().getString(MAX_STRING_LENGTH) +
                            (m.isTag() ? "*" : "");
                    graphics.drawString(gui.getFont(), text, x + ITEM_NAME_X_OFFSET, yPos + 4, TEXT_COLOR);
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
            graphics.drawString(gui.getFont(),
                    Component.translatable(Constants.TRANSLATION_KEY_GUI_CRAFT_QUEUE_SECTION_FUEL),
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
                stack.setCount(f.getAmount());

                int playerHasQuantity = InventoryUtil.getQuantityOf(player, f.getItemId());
                if(playerHasQuantity >= f.getAmount()) {
                    // don't need to display this intermediate, since the user doesn't need to make it
                    continue;
                }

                var drawable = CTPlugin.jeiRuntime.getJeiHelpers().getGuiHelper()
                        .createDrawableIngredient(VanillaTypes.ITEM_STACK, stack);
                drawable.draw(graphics, x + SECTION_X_OFFSET, yPos);

                final int lambdaYpos = yPos;
                if(playerHasQuantity > 0) {
                    var countText = I18n.get(Constants.TRANSLATION_KEY_GUI_HAVE, playerHasQuantity);
                    var text = String.format("%s%s [%s]",
                            item.getDescription().getString(MAX_STRING_LENGTH - countText.length() - 3),
                            f.isTag() ? "*" : "",
                            countText);
                    CraftTracker.LOGGER.trace("text: {}", text);
                    graphics.drawString(gui.getFont(), text, x + ITEM_NAME_X_OFFSET, lambdaYpos + 4, TEXT_COLOR);
                }
                else {
                    var text = item.getDescription().getString(MAX_STRING_LENGTH) +
                            (f.isTag() ? "*" : "");
                    graphics.drawString(gui.getFont(), text, x + ITEM_NAME_X_OFFSET, yPos + 4, TEXT_COLOR);
                }

                yPos += LINE_HEIGHT + 2;
                CraftTracker.LOGGER.trace("yPos (materials item {}): {}", i, yPos);
            }
        }
    }

}
