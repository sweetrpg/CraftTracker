package com.sweetrpg.crafttracker.common.addon.jei;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.lib.Constants;
import com.sweetrpg.crafttracker.common.manager.CraftingQueueManager;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

@JeiPlugin
public class CTPlugin implements IModPlugin {

    public static IJeiRuntime jeiRuntime;

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Constants.MOD_ID, Constants.JEI_PLUGIN_ID);
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

//        registration.getJeiHelpers().getGuiHelper().createCraftingGridHelper(0);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        CraftTracker.LOGGER.debug("CTPlugin#onRuntimeAvailable: {}", jeiRuntime);

        CTPlugin.jeiRuntime = jeiRuntime;

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            Player player = Minecraft.getInstance().player;
            CraftingQueueManager.INSTANCE.load(player);
        });
    }
}
