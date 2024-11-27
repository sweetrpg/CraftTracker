package com.sweetrpg.crafttracker.common.model;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import java.util.List;
import java.util.Map;

public class CraftingQueueIntermediate {

    Map<ResourceLocation, List<? extends Recipe<?>>> recipes;
    int quantity;
    int index;

    public CraftingQueueIntermediate(Map<ResourceLocation, List<? extends Recipe<?>>> recipes, int quantity) {
        this.recipes = recipes;
        this.quantity = quantity;
        this.index = 0;
    }

    public Map<ResourceLocation, List<? extends Recipe<?>>> getRecipes() {
        return recipes;
    }

    public void setRecipes(Map<ResourceLocation, List<? extends Recipe<?>>> recipes) {
        this.recipes = recipes;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
