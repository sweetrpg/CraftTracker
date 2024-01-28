package com.sweetrpg.crafttracker.client;

import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientSetup {

    public static void setupScreenManagers(final FMLClientSetupEvent event) {
//        MenuScreens.register(ModContainerTypes.CAT_BOWL.get(), CatBowlScreen::new);
    }

    public static void setupEntityRenderers(final EntityRenderersEvent.RegisterLayerDefinitions event) {
    }

    public static void setupTileEntityRenderers(final EntityRenderersEvent.RegisterRenderers event) {
    }

    public static void addClientReloadListeners(final RegisterClientReloadListenersEvent event) {
//        event.registerReloadListener(CatTextureManager.INSTANCE);
    }
}
