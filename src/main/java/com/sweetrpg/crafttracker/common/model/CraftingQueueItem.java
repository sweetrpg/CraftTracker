package com.sweetrpg.crafttracker.common.model;

import net.minecraft.resources.ResourceLocation;

public class CraftingQueueItem {

    ResourceLocation itemId;
    int amount;
    boolean tag;

    public CraftingQueueItem(ResourceLocation itemId, int amount, boolean tag) {
        this.itemId = itemId;
        this.amount = amount;
        this.tag = tag;
    }

    public CraftingQueueItem increment(int increase) {
        this.amount += increase;
        return this;
    }

    public ResourceLocation getItemId() {
        return itemId;
    }

    public void setItemId(ResourceLocation itemId) {
        this.itemId = itemId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isTag() {
        return tag;
    }

    public void setTag(boolean tag) {
        this.tag = tag;
    }
}
