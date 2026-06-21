package com.sweetrpg.crafttracker.integration.jei;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.Constants;
import com.sweetrpg.crafttracker.integration.HoverProviderRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class CTPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Constants.MOD_ID, Constants.JEI_PLUGIN_ID);
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        CraftTracker.LOGGER.debug("CTPlugin#registerItemSubtypes: {}", registration);

    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        CraftTracker.LOGGER.debug("CTPlugin#registerRecipes: {}", registration);

    }

    @Override
    public void registerFluidSubtypes(ISubtypeRegistration registration) {
        CraftTracker.LOGGER.debug("CTPlugin#registerFluidSubtypes: {}", registration);
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        CraftTracker.LOGGER.debug("CTPlugin#registerIngredients: {}", registration);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        CraftTracker.LOGGER.debug("CTPlugin#registerCategories: {}", registration);
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        CraftTracker.LOGGER.debug("CTPlugin#registerVanillaCategoryExtensions: {}", registration);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        CraftTracker.LOGGER.debug("CTPlugin#registerRecipeTransferHandlers: {}", registration);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        CraftTracker.LOGGER.debug("CTPlugin#registerRecipeCatalysts: {}", registration);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        CraftTracker.LOGGER.debug("CTPlugin#registerGuiHandlers: {}", registration);

    }

    @Override
    public void registerAdvanced(IAdvancedRegistration registration) {
        CraftTracker.LOGGER.debug("CTPlugin#registerAdvanced: {}", registration);

    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        CraftTracker.LOGGER.debug("CTPlugin#onRuntimeAvailable: {}", jeiRuntime);
        HoverProviderRegistry.register(new JeiHoverProvider(jeiRuntime));
    }
}
