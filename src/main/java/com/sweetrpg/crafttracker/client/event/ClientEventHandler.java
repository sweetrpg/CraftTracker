package com.sweetrpg.crafttracker.client.event;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.client.screen.QueueManagementScreen;
import com.sweetrpg.crafttracker.common.addon.jei.CTPlugin;
import com.sweetrpg.crafttracker.common.lib.CTRuntime;
import com.sweetrpg.crafttracker.common.lib.Constants;
import com.sweetrpg.crafttracker.common.manager.CraftingQueueManager;
import com.sweetrpg.crafttracker.common.manager.ShoppingListManager;
import com.sweetrpg.crafttracker.common.network.PacketHandler;
import com.sweetrpg.crafttracker.common.network.packet.data.AdvancementData;
import com.sweetrpg.crafttracker.common.registry.ModAdvancements;
import com.sweetrpg.crafttracker.common.registry.ModKeyBindings;
import com.sweetrpg.crafttracker.common.util.InventoryUtil;
import com.sweetrpg.crafttracker.common.util.KeyUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.CraftingScreen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Optional;

public class ClientEventHandler {

    public static void onKeyInput(final InputEvent.KeyInputEvent event) {
        CraftTracker.LOGGER.trace("#onKeyInput: {}", event);

        var screen = Minecraft.getInstance().screen;
        if(screen == null) {
            // in the game world

            if(KeyUtil.isKeyDown(event.getKey()) &&
                    ModKeyBindings.TOGGLE_CRAFT_QUEUE_MAPPING.matches(event.getKey(), event.getScanCode())) {
                CraftTracker.LOGGER.debug("#onKeyInput: TOGGLE_CRAFT_LIST_MAPPING");

                handleToggleCraftList();
            }
            else if(KeyUtil.isKeyDown(event.getKey()) &&
                    ModKeyBindings.TOGGLE_SHOPPING_LIST_MAPPING.matches(event.getKey(), event.getScanCode())) {
                CraftTracker.LOGGER.debug("#onKeyInput: TOGGLE_SHOPPING_LIST_MAPPING");

                handleToggleShoppingList();
            }
            else if(KeyUtil.isKeyDown(event.getKey()) &&
                    ModKeyBindings.OPEN_QUEUE_MANAGER_MAPPING.matches(event.getKey(), event.getScanCode())) {
                CraftTracker.LOGGER.debug("#onKeyInput: OPEN_QUEUE_MANAGER_MAPPING");

                QueueManagementScreen.open();
            }
            else if(KeyUtil.isKeyDown(event.getKey()) &&
                    ModKeyBindings.POPULATE_SHOPPING_LIST_MAPPING.matches(event.getKey(), event.getScanCode())) {
                CraftTracker.LOGGER.debug("#onKeyInput: POPULATE_SHOPPING_LIST_MAPPING");

                handlePopulateShoppingList();
            }
            else if(KeyUtil.isKeyDown(event.getKey()) &&
                    ModKeyBindings.CLEAR_SHOPPING_LIST_MAPPING.matches(event.getKey(), event.getScanCode())) {
                CraftTracker.LOGGER.debug("#onKeyInput: CLEAR_SHOPPING_LIST_MAPPING");

                handleClearShoppingList();
            }

            return;
        }

        if(screen instanceof CraftingScreen ||
                screen instanceof InventoryScreen) { // TODO: others?
            if(ModKeyBindings.ADD_TO_QUEUE_MAPPING.matches(event.getKey(), event.getScanCode())) {
                CraftTracker.LOGGER.debug("#onKeyInput: ADD_TO_QUEUE_MAPPING");

                handleAddToQueue();
            }
        }
    }

    private static void handleToggleCraftList() {
        CraftTracker.LOGGER.debug("#handleToggleCraftList");

        var player = Minecraft.getInstance().player;
        TranslationTextComponent msg;
        switch(CTRuntime.INSTANCE.queueOverlayRequestedState) {
            case SHOW:
                CTRuntime.INSTANCE.queueOverlayRequestedState = CTRuntime.OverlayState.HIDE;
                msg = new TranslationTextComponent(Constants.TRANSLATION_KEY_GUI_MSG_QUEUE_OVERLAY_MODE_HIDE);
                player.displayClientMessage(msg, true);
                break;

            case HIDE:
                CTRuntime.INSTANCE.queueOverlayRequestedState = CTRuntime.OverlayState.DYNAMIC;
                msg = new TranslationTextComponent(Constants.TRANSLATION_KEY_GUI_MSG_QUEUE_OVERLAY_MODE_DYNAMIC);
                player.displayClientMessage(msg, true);
                break;

            case DYNAMIC:
                CTRuntime.INSTANCE.queueOverlayRequestedState = CTRuntime.OverlayState.SHOW;
                msg = new TranslationTextComponent(Constants.TRANSLATION_KEY_GUI_MSG_QUEUE_OVERLAY_MODE_SHOW);
                player.displayClientMessage(msg, true);
                break;
        }
    }

    private static void handleClearShoppingList() {
        CraftTracker.LOGGER.debug("#handleClearShoppingList");

        var player = Minecraft.getInstance().player;
        var sMgr = ShoppingListManager.INSTANCE;

        sMgr.clearItems(player);
    }

    private static void handlePopulateShoppingList() {
        CraftTracker.LOGGER.debug("#handlePopulateShoppingList");

        var player = Minecraft.getInstance().player;
        var sMgr = ShoppingListManager.INSTANCE;
        var qMgr = CraftingQueueManager.INSTANCE;

        sMgr.clearItems(player);

        var materials = qMgr.getRawMaterials();
        var fuel = qMgr.getFuel();

        materials.forEach(m -> {
            var haveQty = InventoryUtil.getQuantityOf(player, m.getItemId());
            var needed = m.getAmount() - haveQty;

            if(needed > 0)
                sMgr.addItem(player, m.getItemId(), needed);
        });
        fuel.forEach(f -> {
            var haveQty = InventoryUtil.getQuantityOf(player, f.getItemId());
            var needed = f.getAmount() - haveQty;

            if(needed > 0)
                sMgr.addItem(player, f.getItemId(), needed);
        });

        PacketHandler.sendToServer(new AdvancementData(ModAdvancements.Key.POPULATE_LIST));
    }

    private static void handleToggleShoppingList() {
        CraftTracker.LOGGER.debug("#handleToggleShoppingList");

        var player = Minecraft.getInstance().player;
        TranslationTextComponent msg;
        switch(CTRuntime.INSTANCE.shoppingOverlayRequestedState) {
            case SHOW:
                CTRuntime.INSTANCE.shoppingOverlayRequestedState = CTRuntime.OverlayState.HIDE;
                msg = new TranslationTextComponent(Constants.TRANSLATION_KEY_GUI_MSG_SLIST_OVERLAY_MODE_HIDE);
                player.displayClientMessage(msg, true);
                break;

            case HIDE:
                CTRuntime.INSTANCE.shoppingOverlayRequestedState = CTRuntime.OverlayState.DYNAMIC;
                msg = new TranslationTextComponent(Constants.TRANSLATION_KEY_GUI_MSG_SLIST_OVERLAY_MODE_DYNAMIC);
                player.displayClientMessage(msg, true);
                break;

            case DYNAMIC:
                CTRuntime.INSTANCE.shoppingOverlayRequestedState = CTRuntime.OverlayState.SHOW;
                msg = new TranslationTextComponent(Constants.TRANSLATION_KEY_GUI_MSG_SLIST_OVERLAY_MODE_SHOW);
                player.displayClientMessage(msg, true);
                break;
        }
    }

    private static void handleAddToQueue() {
        CraftTracker.LOGGER.debug("#handleAddToQueue");

        Optional.ofNullable(CTPlugin.jeiRuntime.getIngredientListOverlay().getIngredientUnderMouse())
                .stream()
                .filter(i -> i instanceof ItemStack)
                .map(i -> (ItemStack) i)
                .findFirst()
                .ifPresent(stack -> {
//                    CraftTracker.LOGGER.debug("#handleAddToQueue: type {}", ingredient.getType());
                    CraftTracker.LOGGER.debug("#handleAddToQueue: stack {}", stack);

                    Item item = stack.getItem();
                    ResourceLocation res = item.getRegistryName();
                    CraftTracker.LOGGER.debug("#handleAddToQueue: res {}", res);

                    var player = Minecraft.getInstance().player;
                    CraftingQueueManager.INSTANCE.addProduct(player, res, 1);

                    // send advancement packet
                    PacketHandler.sendToServer(new AdvancementData(ModAdvancements.Key.QUEUE_ITEM));
                });
    }

    @SubscribeEvent
    public void onInputEvent(final InputUpdateEvent event) {
        CraftTracker.LOGGER.trace("#onInputEvent: {}", event);

    }

    @SubscribeEvent
    public static void onScreenInit(final GuiScreenEvent.InitGuiEvent.Post event) {
        CraftTracker.LOGGER.trace("#onScreenInit: {}", event);

    }

    @SubscribeEvent
    public void onScreenDrawForeground(final GuiScreenEvent.DrawScreenEvent event) {
        CraftTracker.LOGGER.trace("#onScreenDrawForeground: {}", event);

        Screen screen = event.getGui();
        if(screen instanceof InventoryScreen || screen instanceof CreativeScreen) {
//            boolean creative = screen instanceof CreativeModeInventoryScreen;

            // TODO
        }
    }

}
