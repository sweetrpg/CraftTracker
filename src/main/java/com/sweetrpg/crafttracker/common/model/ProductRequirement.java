package com.sweetrpg.crafttracker.common.model;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class ProductRequirement {
    public ResourceLocation productId;
    public ResourceLocation recipeId;
    public Map<ResourceLocation, Integer> intermediateRecipes;
    public Map<ResourceLocation, Integer> materials;

    public ProductRequirement(ResourceLocation productId, ResourceLocation recipeId, Map<ResourceLocation, Integer> intermediateRecipes, Map<ResourceLocation, Integer> materials) {
        this.productId = productId;
        this.recipeId = recipeId;
        this.intermediateRecipes = intermediateRecipes;
        this.materials = materials;
    }

}
