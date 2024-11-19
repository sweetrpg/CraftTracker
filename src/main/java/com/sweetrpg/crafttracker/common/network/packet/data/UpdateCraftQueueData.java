package com.sweetrpg.crafttracker.common.network.packet.data;

import net.minecraft.resources.ResourceLocation;
import org.antlr.v4.misc.OrderedHashMap;

import java.util.Map;

public class UpdateCraftQueueData {

    private Map<ResourceLocation, Integer> endProducts = new OrderedHashMap<>();

    public UpdateCraftQueueData(Map<ResourceLocation, Integer> endProducts) {
        this.endProducts = endProducts;
    }

    public Map<ResourceLocation, Integer> getEndProducts() {
        return endProducts;
    }
}
