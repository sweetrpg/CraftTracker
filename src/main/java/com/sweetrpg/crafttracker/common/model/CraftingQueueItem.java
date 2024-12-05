package com.sweetrpg.crafttracker.common.model;

import net.minecraft.resources.ResourceLocation;

/**
 *
 */
public class CraftingQueueItem {

    ResourceLocation itemId;
    int amount;
    boolean tag;

    /**
     * @param itemId
     * @param amount
     * @param tag
     */
    public CraftingQueueItem(ResourceLocation itemId, int amount, boolean tag) {
        this.itemId = itemId;
        this.amount = amount;
        this.tag = tag;
    }

    /**
     *
     * @param increase
     * @return
     */
    public CraftingQueueItem increment(int increase) {
        this.amount += increase;
        return this;
    }

    /**
     *
     * @param tag
     * @return
     */
    public CraftingQueueItem tag(boolean tag) {
        this.tag = tag;
        return this;
    }

    public ResourceLocation getItemId() {
        return itemId;
    }

    public CraftingQueueItem setItemId(ResourceLocation itemId) {
        this.itemId = itemId;
        return this;
    }

    public int getAmount() {
        return amount;
    }

    public CraftingQueueItem setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public boolean isTag() {
        return tag;
    }

    public void setTag(boolean tag) {
        this.tag = tag;
    }
}
