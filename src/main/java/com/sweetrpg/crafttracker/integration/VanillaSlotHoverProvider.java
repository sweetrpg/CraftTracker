package com.sweetrpg.crafttracker.integration;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

/**
 * Resolves the item under the mouse by reading
 * {@link AbstractContainerScreen#hoveredSlot}. Always registered.
 */
public class VanillaSlotHoverProvider implements HoverItemProvider {

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public String getName() {
        return "vanilla-slot";
    }

    @Override
    public Optional<ResourceLocation> getItemUnderMouse(Screen screen) {
        if (!(screen instanceof AbstractContainerScreen<?> containerScreen)) {
            return Optional.empty();
        }
        var slot = containerScreen.hoveredSlot;
        if (slot == null || !slot.hasItem()) {
            return Optional.empty();
        }
        return Optional.ofNullable(slot.getItem().getItem().getRegistryName());
    }
}
