package com.sweetrpg.crafttracker.client;

import com.sweetrpg.crafttracker.client.event.ClientEventHandler;
import com.sweetrpg.crafttracker.client.overlay.CraftQueueOverlay;
import com.sweetrpg.crafttracker.client.overlay.ShoppingListOverlay;
import com.sweetrpg.crafttracker.common.registry.ModKeyBindings;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static net.minecraftforge.client.gui.ForgeIngameGui.HOTBAR_ELEMENT;

public class ClientSetup {

    public static void setupScreenManagers(final FMLClientSetupEvent event) {
//        MenuScreens.register(ModContainerTypes.CAT_BOWL.get(), CatBowlScreen::new);
    }

//    public static void setupEntityRenderers(final EntityRenderersEvent.RegisterLayerDefinitions event) {
//    }

//    public static void setupTileEntityRenderers(final EntityRenderersEvent.RegisterRenderers event) {
//    }

    public static void addClientReloadListeners(final RegisterClientReloadListenersEvent event) {
//        event.registerReloadListener(CatTextureManager.INSTANCE);
    }

    public static void addKeyBindings(final FMLClientSetupEvent event) {

        MinecraftForge.EVENT_BUS.addListener(ClientEventHandler::onKeyInput);

        OverlayRegistry.registerOverlayAbove(HOTBAR_ELEMENT, "craft_queue", CraftQueueOverlay.CRAFT_QUEUE);
        OverlayRegistry.registerOverlayAbove(HOTBAR_ELEMENT, "shopping_list", ShoppingListOverlay.SHOPPING_LIST);

        ModKeyBindings.init();
    }
}
