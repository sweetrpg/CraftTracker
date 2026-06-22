package com.sweetrpg.crafttracker.integration;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Holds registered {@link HoverItemProvider}s in descending priority order.
 * Query with {@link #resolve(Screen)} to get the first non-empty result.
 */
public class HoverProviderRegistry {

    private static final List<HoverItemProvider> PROVIDERS = new ArrayList<>();

    public static void register(HoverItemProvider provider) {
        PROVIDERS.add(provider);
        PROVIDERS.sort(Comparator.comparingInt(HoverItemProvider::getPriority).reversed());
    }

    public static Optional<ResourceLocation> resolve(Screen screen) {
        for (HoverItemProvider provider : PROVIDERS) {
            Optional<ResourceLocation> result = provider.getItemUnderMouse(screen);
            if (result.isPresent()) {
                return result;
            }
        }
        return Optional.empty();
    }
}
