package com.sweetrpg.crafttracker.common.model;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class CraftingQueueFuel {

    Map<ResourceLocation, Integer> quantities;
    int index;

    public CraftingQueueFuel(Map<ResourceLocation, Integer> quantities) {
        this.quantities = quantities;
        this.index = -1;
    }

    public Map<ResourceLocation, Integer> getQuantities() {
        return quantities;
    }

    public void setQuantities(Map<ResourceLocation, Integer> quantities) {
        this.quantities = quantities;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = Math.min(Math.max(index, this.quantities.size() - 1), -1);
    }
}
