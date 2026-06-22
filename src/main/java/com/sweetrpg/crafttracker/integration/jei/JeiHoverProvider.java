package com.sweetrpg.crafttracker.integration.jei;

import com.sweetrpg.crafttracker.integration.HoverItemProvider;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * Resolves the item under the mouse via the JEI ingredient-list overlay.
 * Only registered when JEI is loaded; only this class and {@link CTPlugin}
 * may import JEI API types.
 */
public class JeiHoverProvider implements HoverItemProvider {

    private final IJeiRuntime jeiRuntime;

    public JeiHoverProvider(IJeiRuntime jeiRuntime) {
        this.jeiRuntime = jeiRuntime;
    }

    @Override
    public int getPriority() {
        return 100;
    }

    @Override
    public String getName() {
        return "jei";
    }

    @Override
    public Optional<ResourceLocation> getItemUnderMouse(Screen screen) {
        return jeiRuntime.getIngredientListOverlay().getIngredientUnderMouse()
                .filter(i -> i.getIngredient() instanceof ItemStack)
                .map(i -> ((ItemStack) i.getIngredient()).getItem().getRegistryName());
    }
}
