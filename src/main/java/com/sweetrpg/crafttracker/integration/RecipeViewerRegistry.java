package com.sweetrpg.crafttracker.integration;

import com.sweetrpg.crafttracker.CraftTracker;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds registered {@link RecipeViewerProvider}s.
 * Call {@link #showRecipesFor(ItemStack)} to dispatch to all registered providers.
 */
public class RecipeViewerRegistry {

    private static final List<RecipeViewerProvider> PROVIDERS = new ArrayList<>();

    public static void register(RecipeViewerProvider provider) {
        PROVIDERS.add(provider);
    }

    public static void showRecipesFor(ItemStack stack) {
        for (RecipeViewerProvider provider : PROVIDERS) {
            try {
                provider.showRecipesFor(stack);
            } catch (RuntimeException e) {
                CraftTracker.LOGGER.warn("RecipeViewerRegistry: {} failed to show recipes", provider.getName(), e);
            }
        }
    }
}
