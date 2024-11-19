package com.sweetrpg.crafttracker.common.storage;

import com.google.common.collect.Maps;
import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.lib.Constants;
import com.sweetrpg.crafttracker.common.util.NBTUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

/**
 * This class handles storage of all crafting queue information for a player.
 *
 * This includes:
 *   * Position and visibility of the craft list window
 *   * Position and visibility of the shopping list window
 *   * The list of end products in the queue by item ID and quantity
 */
public class CraftingQueueStorage extends SavedData {

    private @Nullable UUID ownerId;
    private Vec3 craftingQueuePosition;
    private boolean craftingQueueVisible;
    private Vec3 shoppingListPosition;
    private boolean shoppingListVisible;
    private Map<ResourceLocation, CraftingQueueData> queueData = Maps.newConcurrentMap();

    public CraftingQueueStorage() {}

    public static CraftingQueueStorage get(Level world) {
        CraftTracker.LOGGER.debug("CraftingQueueStorage#get: {}", world);

        if (!(world instanceof ServerLevel)) {
            throw new RuntimeException("Tried to access crafting queue data from the client. This should not happen...");
        }

        ServerLevel overworld = world.getServer().getLevel(Level.OVERWORLD);

        DimensionDataStorage storage = overworld.getDataStorage();
        return storage.computeIfAbsent(CraftingQueueStorage::load, CraftingQueueStorage::new, Constants.STORAGE_CRAFTING_QUEUE);
    }

    public void putData(ResourceLocation itemId, int quantity) {
        CraftTracker.LOGGER.debug("CraftingQueueStorage#putData: {}, quantity: {}", itemId, quantity);

        this.queueData.compute(itemId, (k, data) -> {
            if(data == null) {
                return new CraftingQueueData(this, itemId, quantity);
            }

            return new CraftingQueueData(this, itemId, data.getQuantity() + quantity);
        });

        this.setDirty();
    }

    public void removeData(ResourceLocation itemId) {
        CraftTracker.LOGGER.debug("CraftingQueueStorage#removeData: {}", itemId);

        this.queueData.remove(itemId);

        this.setDirty();
    }

    public static CraftingQueueStorage load(CompoundTag nbt) {
        CraftTracker.LOGGER.debug("CraftingQueueStorage#load: {}", nbt);

        CraftingQueueStorage store = new CraftingQueueStorage();
        store.queueData.clear();

        store.ownerId = NBTUtil.getUniqueId(nbt, Keys.OWNER_ID);
        store.craftingQueuePosition = NBTUtil.getVector3d(nbt);
        store.craftingQueueVisible = nbt.getBoolean(Keys.CRAFTING_QUEUE_VISIBLE);
        store.shoppingListPosition = NBTUtil.getVector3d(nbt);
        store.shoppingListVisible = nbt.getBoolean(Keys.SHOPPING_LIST_VISIBLE);

        ListTag list = nbt.getList(Keys.QUEUE_DATA, Tag.TAG_COMPOUND);

        for (int i = 0; i < list.size(); ++i) {
            CompoundTag queueDataCompound = list.getCompound(i);

            CraftingQueueData queueData = new CraftingQueueData(store);
            queueData.read(queueDataCompound);

            store.queueData.put(queueData.getItemId(), queueData);
        }

        return store;
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        CraftTracker.LOGGER.debug("CraftingQueueStorage#save: {}", compound);

        NBTUtil.putUniqueId(compound, Keys.OWNER_ID, this.ownerId);
        NBTUtil.putVector3d(compound, this.craftingQueuePosition);
        compound.putBoolean(Keys.CRAFTING_QUEUE_VISIBLE, this.craftingQueueVisible);
        NBTUtil.putVector3d(compound, this.shoppingListPosition);
        compound.putBoolean(Keys.SHOPPING_LIST_VISIBLE, this.shoppingListVisible);

        ListTag list = new ListTag();

        for (Map.Entry<ResourceLocation, CraftingQueueData> entry : this.queueData.entrySet()) {
            CompoundTag queueDataCompound = new CompoundTag();

            CraftingQueueData queueData = entry.getValue();
            queueData.write(queueDataCompound);

            list.add(queueDataCompound);
        }

        compound.put(Keys.QUEUE_DATA, list);

        return compound;
    }

    static class Keys {
        static String OWNER_ID = "owner_id";
        static String CRAFTING_QUEUE_VISIBLE = "crafting_queue_visible";
        static String SHOPPING_LIST_VISIBLE = "shopping_list_visible";
        static String QUEUE_DATA = "queue_data";
    }
}
