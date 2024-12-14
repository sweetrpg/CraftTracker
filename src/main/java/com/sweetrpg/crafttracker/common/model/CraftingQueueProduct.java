package com.sweetrpg.crafttracker.common.model;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import java.text.MessageFormat;
import java.util.List;

/**
 * A value object for holding crafting queue products.
 */
public class CraftingQueueProduct {

    ResourceLocation productId;
    int iterations;
    List<? extends Recipe<?>> recipes;
    int index;

    /**
     * Default constructor.
     *
     * @param productId The ID of the product
     * @param recipes The recipes associated with the product
     * @param iterations The number of times the product should be crafted
     */
    public CraftingQueueProduct(ResourceLocation productId, List<? extends Recipe<?>> recipes, int iterations) {
        this.productId = productId;
        this.iterations = iterations;
        this.recipes = recipes;
        this.index = 0;
    }

    public ResourceLocation getProductId() {
        return productId;
    }

    public void setProductId(ResourceLocation productId) {
        this.productId = productId;
    }

    public List<? extends Recipe<?>> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<? extends Recipe<?>> recipes) {
        this.recipes = recipes;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return MessageFormat.format("CraftingQueueProduct[ itemId={0}, recipes={1}, iterations={2}, index={3} ]",
                productId, recipes, iterations, index);
    }
}
