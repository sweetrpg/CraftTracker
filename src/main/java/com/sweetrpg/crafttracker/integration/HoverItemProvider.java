package com.sweetrpg.crafttracker.integration;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

/**
 * Resolves the item under the mouse for the current screen.
 * Implementations are registered in {@link HoverProviderRegistry} in priority order.
 */
public interface HoverItemProvider {

    /**
     * Higher value = queried first.
     */
    int getPriority();

    String getName();

    Optional<ResourceLocation> getItemUnderMouse(Screen screen);
}
