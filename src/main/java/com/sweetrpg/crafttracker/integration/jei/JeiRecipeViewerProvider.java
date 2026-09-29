package com.sweetrpg.crafttracker.integration.jei;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.integration.RecipeViewerProvider;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.world.item.ItemStack;

/**
 * Shows recipes in JEI. Only this class and {@link CTPlugin} may import JEI API types.
 */
public class JeiRecipeViewerProvider implements RecipeViewerProvider {

    private final IJeiRuntime jeiRuntime;

    public JeiRecipeViewerProvider(IJeiRuntime jeiRuntime) {
        this.jeiRuntime = jeiRuntime;
    }

    @Override
    public String getName() {
        return "jei";
    }

    @Override
    public void showRecipesFor(ItemStack stack) {
        if (stack.isEmpty()) {
            CraftTracker.LOGGER.debug("JeiRecipeViewerProvider#showRecipesFor: empty stack, skipping");
            return;
        }
        IFocus<ItemStack> focus = jeiRuntime.getJeiHelpers()
                .getFocusFactory()
                .createFocus(RecipeIngredientRole.OUTPUT, VanillaTypes.ITEM_STACK, stack);
        jeiRuntime.getRecipesGui().show(focus);
    }
}
