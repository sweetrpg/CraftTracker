package com.sweetrpg.crafttracker.client.event;

import com.sweetrpg.crafttracker.CraftTracker;
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
import com.sweetrpg.crafttracker.integration.jei.CTPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

public class ClientEventHandler {

    public static void onKeyInput(final InputEvent.Key event) {
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
        Component msg;
        switch(Runtime.INSTANCE.queueOverlayRequestedState) {
            case SHOW:
                Runtime.INSTANCE.queueOverlayRequestedState = Runtime.OverlayState.HIDE;
                msg = Component.translatable(Constants.TRANSLATION_KEY_GUI_MSG_QUEUE_OVERLAY_MODE_HIDE);
                player.displayClientMessage(msg, true);
                break;

            case HIDE:
                Runtime.INSTANCE.queueOverlayRequestedState = Runtime.OverlayState.DYNAMIC;
                msg = Component.translatable(Constants.TRANSLATION_KEY_GUI_MSG_QUEUE_OVERLAY_MODE_DYNAMIC);
                player.displayClientMessage(msg, true);
                break;

            case DYNAMIC:
                Runtime.INSTANCE.queueOverlayRequestedState = Runtime.OverlayState.SHOW;
                msg = Component.translatable(Constants.TRANSLATION_KEY_GUI_MSG_QUEUE_OVERLAY_MODE_SHOW);
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

        PacketHandler.send(PacketDistributor.SERVER.noArg(), new AdvancementData(ModAdvancements.Key.POPULATE_LIST));
    }

    private static void handleToggleShoppingList() {
        CraftTracker.LOGGER.debug("#handleToggleShoppingList");

        var player = Minecraft.getInstance().player;
        Component msg;
        switch(Runtime.INSTANCE.shoppingOverlayRequestedState) {
            case SHOW:
                Runtime.INSTANCE.shoppingOverlayRequestedState = Runtime.OverlayState.HIDE;
                msg = Component.translatable(Constants.TRANSLATION_KEY_GUI_MSG_SLIST_OVERLAY_MODE_HIDE);
                player.displayClientMessage(msg, true);
                break;

            case HIDE:
                Runtime.INSTANCE.shoppingOverlayRequestedState = Runtime.OverlayState.DYNAMIC;
                msg = Component.translatable(Constants.TRANSLATION_KEY_GUI_MSG_SLIST_OVERLAY_MODE_DYNAMIC);
                player.displayClientMessage(msg, true);
                break;

            case DYNAMIC:
                Runtime.INSTANCE.shoppingOverlayRequestedState = Runtime.OverlayState.SHOW;
                msg = Component.translatable(Constants.TRANSLATION_KEY_GUI_MSG_SLIST_OVERLAY_MODE_SHOW);
                player.displayClientMessage(msg, true);
                break;
        }
    }

    private static void handleAddToQueue() {
        CraftTracker.LOGGER.debug("#handleAddToQueue");

        CTPlugin.jeiRuntime.getIngredientListOverlay().getIngredientUnderMouse()
                .ifPresent(ingredient -> {
                    CraftTracker.LOGGER.debug("#handleAddToQueue: type {}", ingredient.getType());
                    CraftTracker.LOGGER.debug("#handleAddToQueue: ingredient {}", ingredient.getIngredient());

                    if(ingredient.getIngredient() instanceof ItemStack itemStack) {
                        ResourceLocation res = ForgeRegistries.ITEMS.getKey(itemStack.getItem());
                        CraftTracker.LOGGER.debug("#handleAddToQueue: res {}", res);

                        var player = Minecraft.getInstance().player;
                        CraftingQueueManager.INSTANCE.addProduct(player, res, 1);

                        // send advancement packet
                        PacketHandler.send(PacketDistributor.SERVER.noArg(), new AdvancementData(ModAdvancements.Key.QUEUE_ITEM));
                    }
                });
    }

    @SubscribeEvent
    public void onInputEvent(final MovementInputUpdateEvent event) {
        CraftTracker.LOGGER.trace("#onInputEvent: {}", event);

    }

    @SubscribeEvent
    public static void onScreenInit(final ScreenEvent.Init.Post event) {
        CraftTracker.LOGGER.trace("#onScreenInit: {}", event);

    }

    @SubscribeEvent
    public void onScreenDrawForeground(final ScreenEvent.Render event) {
        CraftTracker.LOGGER.trace("#onScreenDrawForeground: {}", event);

        Screen screen = event.getScreen();
        if(screen instanceof InventoryScreen || screen instanceof CreativeModeInventoryScreen) {
//            boolean creative = screen instanceof CreativeModeInventoryScreen;

            // TODO
        }
    }

}
