package com.sweetrpg.crafttracker.common.model;

import net.minecraft.resources.ResourceLocation;

/**
 * A value object for holding intermediate, material, and fuel information from the crafting queue.
 */
public class CraftingQueueItem {

    ResourceLocation itemId;
    int amount;
    boolean tag;

    /**
     * Default constructor.
     *
     * @param itemId The ID of the item
     * @param amount The quantity of the item
     * @param tag Indicates if the item is pulled from a tag or not
     */
    public CraftingQueueItem(ResourceLocation itemId, int amount, boolean tag) {
        this.itemId = itemId;
        this.amount = amount;
        this.tag = tag;
    }

    /**
     * Convenience method to increase the item amount. Provided for call chaining.
     *
     * @param increase The amount to increase
     * @return This object
     */
    public CraftingQueueItem increment(int increase) {
        this.amount += increase;
        return this;
    }

    /**
     * Convenience method to set the tag value. Provided for call chaining.
     *
     * @param tag Whether the item is a tag or not
     * @return This object
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
