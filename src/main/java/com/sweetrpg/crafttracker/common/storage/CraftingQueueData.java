package com.sweetrpg.crafttracker.common.storage;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.util.NBTUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CraftingQueueData {

    private final CraftingQueueStorage storage;
    private final UUID uuid;
    private @Nullable UUID ownerId;
    private Vec3 position;

    private Map<ResourceLocation, Integer> endProducts;

    protected CraftingQueueData(CraftingQueueStorage storage, UUID uuid) {
        this.storage = storage;
        this.uuid = uuid;
        this.endProducts = new HashMap<>();
    }

    public void addItem(ResourceLocation itemId, int quantity) {
        CraftTracker.LOGGER.debug("CraftingQueueData#addItem: {}, quantity {}", itemId, quantity);

        this.endProducts.compute(itemId, (k, v) -> (v == null ? 0 : v) + quantity);

        this.storage.setDirty();
    }

    public void removeItem(ResourceLocation itemId, int quantity) {
        CraftTracker.LOGGER.debug("CraftingQueueData#removeItem: {}, quantity {}", itemId, quantity);

        this.endProducts.computeIfPresent(itemId, (k, v) -> {
            if(v - quantity < 1) {
                return null;
            }

            return v - quantity;
        });

        this.storage.setDirty();
    }

    public void read(CompoundTag compound) {
        CraftTracker.LOGGER.debug("CraftingQueueData#read");

        this.ownerId = NBTUtil.getUniqueId(compound, "ownerId");
        this.position = NBTUtil.getVector3d(compound);
        this.endProducts.clear();

        if(compound.contains("products", Tag.TAG_COMPOUND)) {
            ListTag products = compound.getList("products", Tag.TAG_COMPOUND);
            for(Tag p : products) {
                if(p instanceof CompoundTag product) {
                    String itemId = product.getString("item_id");
                    ResourceLocation res = ResourceLocation.tryParse(itemId);
                    int quantity = product.getInt("quantity");
                    this.endProducts.put(res, quantity);
                }
            }
        }
    }

    public CompoundTag write(CompoundTag compound) {
        CraftTracker.LOGGER.debug("CraftingQueueData#write");

        NBTUtil.putUniqueId(compound, "ownerId", this.ownerId);
        NBTUtil.putVector3d(compound, this.position);

        ListTag list = new ListTag();
        for(Map.Entry<ResourceLocation, Integer> product : this.endProducts.entrySet()) {
            CompoundTag tag = new CompoundTag();

            NBTUtil.putResourceLocation(tag, "item_id", product.getKey());
            tag.putInt("item_id", product.getValue());

            list.add(tag);
        }
        compound.put("products", list);

        return compound;
    }
}
