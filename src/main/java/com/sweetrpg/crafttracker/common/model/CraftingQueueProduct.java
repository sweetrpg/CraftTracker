package com.sweetrpg.crafttracker.common.model;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import java.text.MessageFormat;
import java.util.List;

public class CraftingQueueProduct {

    ResourceLocation itemId;
    List<? extends Recipe<?>> recipes;
    int quantity;
    int index;

    public CraftingQueueProduct(ResourceLocation itemId, List<? extends Recipe<?>> recipes, int quantity) {
        this.itemId = itemId;
        this.recipes = recipes;
        this.quantity = quantity;
        this.index = 0;
    }

    public ResourceLocation getItemId() {
        return itemId;
    }

    public void setItemId(ResourceLocation itemId) {
        this.itemId = itemId;
    }

    public List<? extends Recipe<?>> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<? extends Recipe<?>> recipes) {
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

    @Override
    public String toString() {
        return MessageFormat.format("""
                        CraftingQueueProduct[
                          itemId={0}
                          recipes={1}
                          quantity={2}
                          index={3}
                        ]
                        """,
                itemId, recipes, quantity, index);
    }
}
