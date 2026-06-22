package com.sweetrpg.crafttracker.client.event;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.client.overlay.CraftQueueOverlay;
import com.sweetrpg.crafttracker.client.screen.QueueManagementScreen;
import com.sweetrpg.crafttracker.common.Constants;
import com.sweetrpg.crafttracker.common.Runtime;
import com.sweetrpg.crafttracker.common.manager.CraftingQueueManager;
import com.sweetrpg.crafttracker.common.manager.ShoppingListManager;
import com.sweetrpg.crafttracker.common.network.PacketHandler;
import com.sweetrpg.crafttracker.common.network.packet.data.AdvancementData;
import com.sweetrpg.crafttracker.common.registry.ModAdvancements;
import com.sweetrpg.crafttracker.common.registry.ModKeyBindings;
import com.sweetrpg.crafttracker.common.util.InventoryUtil;
import com.sweetrpg.crafttracker.common.util.KeyUtil;
import com.sweetrpg.crafttracker.integration.HoverProviderRegistry;
import com.sweetrpg.crafttracker.integration.RecipeViewerRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEventHandler {

    private static boolean managersLoaded = false;

    public static void onClientLogin(final ClientPlayerNetworkEvent.LoggedInEvent event) {
        CraftTracker.LOGGER.debug("#onClientLogin");
        if (!managersLoaded) {
            managersLoaded = true;
            var player = event.getPlayer();
            if (player != null) {
                CraftingQueueManager.INSTANCE.load(player);
                ShoppingListManager.INSTANCE.load(player);
            }
        }
    }

    public static void onClientLogout(final ClientPlayerNetworkEvent.LoggedOutEvent event) {
        CraftTracker.LOGGER.debug("#onClientLogout");
        managersLoaded = false;
    }

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

        if (screen instanceof AbstractContainerScreen) {
            if(ModKeyBindings.ADD_TO_QUEUE_MAPPING.matches(event.getKey(), event.getScanCode())) {
                CraftTracker.LOGGER.debug("#onKeyInput: ADD_TO_QUEUE_MAPPING");

                handleAddToQueue();
            } else if (KeyUtil.isKeyDown(event.getKey()) &&
                    ModKeyBindings.SHOW_RECIPE_MAPPING.matches(event.getKey(), event.getScanCode())) {
                CraftTracker.LOGGER.debug("#onKeyInput: SHOW_RECIPE_MAPPING");

                handleShowRecipe();
            }
        }
    }

    private static void handleToggleCraftList() {
        CraftTracker.LOGGER.debug("#handleToggleCraftList");

        var player = Minecraft.getInstance().player;
        TranslatableComponent msg;
        switch(Runtime.INSTANCE.queueOverlayRequestedState) {
            case SHOW:
                Runtime.INSTANCE.queueOverlayRequestedState = Runtime.OverlayState.HIDE;
                msg = new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_MSG_QUEUE_OVERLAY_MODE_HIDE);
                player.displayClientMessage(msg, true);
                break;

            case HIDE:
                Runtime.INSTANCE.queueOverlayRequestedState = Runtime.OverlayState.DYNAMIC;
                msg = new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_MSG_QUEUE_OVERLAY_MODE_DYNAMIC);
                player.displayClientMessage(msg, true);
                break;

            case DYNAMIC:
                Runtime.INSTANCE.queueOverlayRequestedState = Runtime.OverlayState.SHOW;
                msg = new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_MSG_QUEUE_OVERLAY_MODE_SHOW);
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
        TranslatableComponent msg;
        switch(Runtime.INSTANCE.shoppingOverlayRequestedState) {
            case SHOW:
                Runtime.INSTANCE.shoppingOverlayRequestedState = Runtime.OverlayState.HIDE;
                msg = new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_MSG_SLIST_OVERLAY_MODE_HIDE);
                player.displayClientMessage(msg, true);
                break;

            case HIDE:
                Runtime.INSTANCE.shoppingOverlayRequestedState = Runtime.OverlayState.DYNAMIC;
                msg = new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_MSG_SLIST_OVERLAY_MODE_DYNAMIC);
                player.displayClientMessage(msg, true);
                break;

            case DYNAMIC:
                Runtime.INSTANCE.shoppingOverlayRequestedState = Runtime.OverlayState.SHOW;
                msg = new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_MSG_SLIST_OVERLAY_MODE_SHOW);
                player.displayClientMessage(msg, true);
                break;
        }
    }

    private static void handleAddToQueue() {
        CraftTracker.LOGGER.debug("#handleAddToQueue");

        var screen = Minecraft.getInstance().screen;
        if (screen == null) return;

        HoverProviderRegistry.resolve(screen).ifPresent(res -> {
            CraftTracker.LOGGER.debug("#handleAddToQueue: res {}", res);
            var player = Minecraft.getInstance().player;
            CraftingQueueManager.INSTANCE.addProduct(player, res, 1);
            PacketHandler.sendToServer(new AdvancementData(ModAdvancements.Key.QUEUE_ITEM));
        });
    }

    private static void handleShowRecipe() {
        CraftTracker.LOGGER.debug("#handleShowRecipe");

        var hovered = CraftQueueOverlay.hoveredItem;
        if (!hovered.isEmpty()) {
            RecipeViewerRegistry.showRecipesFor(hovered);
        }
    }

    @SubscribeEvent
    public void onInputEvent(final MovementInputUpdateEvent event) {
        CraftTracker.LOGGER.trace("#onInputEvent: {}", event);

    }

    @SubscribeEvent
    public static void onScreenInit(final ScreenEvent.InitScreenEvent.Post event) {
        CraftTracker.LOGGER.trace("#onScreenInit: {}", event);

    }

    @SubscribeEvent
    public void onScreenDrawForeground(final ScreenEvent.DrawScreenEvent event) {
        CraftTracker.LOGGER.trace("#onScreenDrawForeground: {}", event);

        Screen screen = event.getScreen();
        if(screen instanceof InventoryScreen || screen instanceof CreativeModeInventoryScreen) {
//            boolean creative = screen instanceof CreativeModeInventoryScreen;

            // TODO
        }
    }

}
