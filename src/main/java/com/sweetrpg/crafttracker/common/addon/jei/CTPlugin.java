package com.sweetrpg.crafttracker.common.addon.jei;

import com.sweetrpg.crafttracker.CraftTracker;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.ModIds;
import mezz.jei.api.registration.IAdvancedRegistration;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class CTPlugin implements IModPlugin {

    public static IJeiRuntime jeiRuntime;

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(ModIds.JEI_ID, "crafttracker");
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        CraftTracker.LOGGER.debug("CTPlugin#registerItemSubtypes: {}", registration);

//        registration.registerSubtypeInterpreter(ModBlocks.CAT_TREE.get().asItem(), (stack, ctx) -> {
//            IColorMaterial colorMaterial = CatTreeUtil.getColorMaterial(stack);
//
//            String colorKey = colorMaterial != null ? colorMaterial.getRegistryName().toString()
//                    : "CraftTracker:casing_missing";
//
//            return colorKey;
//        });
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        CraftTracker.LOGGER.debug("CTPlugin#registerRecipes: {}", registration);

        //        registration.addRecipes(CatTreeRecipeMaker.createCatTreeRecipes(), RecipeTypes.CRAFTING.getUid());
//        registration.addRecipes(PetDoorRecipeMaker.createPetDoorRecipes(), RecipeTypes.CRAFTING.getUid());
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        CraftTracker.LOGGER.debug("CTPlugin#registerGuiHandlers: {}", registration);

    }

    @Override
    public void registerAdvanced(IAdvancedRegistration registration) {
        CraftTracker.LOGGER.debug("CTPlugin#registerAdvanced: {}", registration);

        registration.getJeiHelpers().getGuiHelper().createCraftingGridHelper(0);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        CraftTracker.LOGGER.debug("CTPlugin#onRuntimeAvailable: {}", jeiRuntime);

        CTPlugin.jeiRuntime = jeiRuntime;
    }
}
