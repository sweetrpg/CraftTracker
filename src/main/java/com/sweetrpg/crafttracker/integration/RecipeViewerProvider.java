package com.sweetrpg.crafttracker.integration;

import net.minecraft.world.item.ItemStack;

/**
 * Opens recipes for a given item in an installed item viewer.
 * Implementations are registered in {@link RecipeViewerRegistry}.
 * Only JEI-specific code may import JEI API types; all other code uses this interface.
 */
public interface RecipeViewerProvider {

    String getName();

    /**
     * Shows recipes for {@code stack} in the item viewer. No-op if the viewer is unavailable.
     */
    void showRecipesFor(ItemStack stack);
}
