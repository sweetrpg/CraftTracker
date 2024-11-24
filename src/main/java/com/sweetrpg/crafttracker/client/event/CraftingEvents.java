package com.sweetrpg.crafttracker.client.event;

import com.sweetrpg.crafttracker.common.manager.CraftingQueueManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public class CraftingEvents {

    public static void removeProduct(ResourceLocation itemId, int quantity) {
        var player = Minecraft.getInstance().player;

        CraftingQueueManager.INSTANCE.removeProduct(player, itemId, quantity);
    }
}
