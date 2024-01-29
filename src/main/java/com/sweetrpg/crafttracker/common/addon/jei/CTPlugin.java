package com.sweetrpg.crafttracker.common.addon.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.ModIds;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class CTPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(ModIds.JEI_ID, "crafttracker");
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
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
//        registration.addRecipes(CatTreeRecipeMaker.createCatTreeRecipes(), RecipeTypes.CRAFTING.getUid());
//        registration.addRecipes(PetDoorRecipeMaker.createPetDoorRecipes(), RecipeTypes.CRAFTING.getUid());
    }
}
