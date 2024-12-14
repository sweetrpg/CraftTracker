package com.sweetrpg.crafttracker.client.event;

import com.sweetrpg.crafttracker.common.manager.CraftingQueueManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

/**
 * Handlers for various events.
 */
public class CraftingEvents {

    /**
     * Relays the request to remove a product to the @{link CraftingQueueManager}
     *
     * @param itemId   the item to remove from the queue
     * @param quantity the amount of the item to remove from the queue
     */
    public static void removeProduct(ResourceLocation itemId, int quantity) {
        var player = Minecraft.getInstance().player;

        CraftingQueueManager.INSTANCE.removeProduct(player, itemId, quantity);
    }

    /**
     * Removes an item from the crafting queue if it was picked up by the player.
     *
     * @param itemId the item to remove from the queue
     * @param quantity the amount of the item to remove from the queue
     */
    public static void pickupItem(ResourceLocation itemId, int quantity) {
        var player = Minecraft.getInstance().player;

        CraftingQueueManager.INSTANCE.removeProduct(player, itemId, quantity);
    }

}
