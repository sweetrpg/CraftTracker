package com.sweetrpg.crafttracker.client;

import com.sweetrpg.crafttracker.client.event.ClientEventHandler;
import com.sweetrpg.crafttracker.client.overlay.CraftQueueOverlay;
import com.sweetrpg.crafttracker.client.overlay.ShoppingListOverlay;
import com.sweetrpg.crafttracker.common.registry.ModKeyBindings;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientSetup {

    public static void setupScreenManagers(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.addListener(ClientEventHandler::onScreenInit);
    }

    public static void addKeyBindings(final FMLClientSetupEvent event) {

        MinecraftForge.EVENT_BUS.addListener(ClientEventHandler::onKeyInput);

        ModKeyBindings.init();
    }

    public static void initOverlays() {
        CraftQueueOverlay.init();
        ShoppingListOverlay.init();
    }
}
