package com.sweetrpg.crafttracker.common.storage;

import com.google.common.collect.Maps;
import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.lib.Constants;
import com.sweetrpg.crafttracker.common.util.NBTUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.Map;
import java.util.UUID;

public class CraftingQueueStorage extends SavedData {

    private Map<UUID, CraftingQueueData> queueDataMap = Maps.newConcurrentMap();

    public CraftingQueueStorage() {}

    public static CraftingQueueStorage get(Level world) {
        CraftTracker.LOGGER.debug("#get: {}", world);

        if (!(world instanceof ServerLevel)) {
            throw new RuntimeException("Tried to access crafting queue data from the client. This should not happen...");
        }

        ServerLevel overworld = world.getServer().getLevel(Level.OVERWORLD);

        DimensionDataStorage storage = overworld.getDataStorage();
        return storage.computeIfAbsent(CraftingQueueStorage::load, CraftingQueueStorage::new, Constants.STORAGE_CRAFTING_QUEUE);
    }

    public CraftingQueueData getData(UUID uuid) {
        CraftTracker.LOGGER.debug("#getData: {}", uuid);

        return queueDataMap.computeIfAbsent(uuid, (k) -> {
            CraftingQueueData data = new CraftingQueueData(this, uuid);
            this.setDirty();
            return data;
        });
    }

    public void removeData(UUID uuid) {
        CraftTracker.LOGGER.debug("#removeData: {}", uuid);

        queueDataMap.remove(uuid);
        this.setDirty();
    }

    public static CraftingQueueStorage load(CompoundTag nbt) {
        CraftTracker.LOGGER.debug("#load: {}", nbt);

        CraftingQueueStorage store = new CraftingQueueStorage();
        store.queueDataMap.clear();

        ListTag list = nbt.getList("queueData", Tag.TAG_COMPOUND);

        for (int i = 0; i < list.size(); ++i) {
            CompoundTag queueDataCompound = list.getCompound(i);

            UUID uuid = NBTUtil.getUniqueId(queueDataCompound, "uuid");

            CraftingQueueData queueData = new CraftingQueueData(store, uuid);
            queueData.read(queueDataCompound);

            if (uuid == null) {
                CraftTracker.LOGGER.info("Failed to load cat location data. Please report to mod author...");
                CraftTracker.LOGGER.info(queueData);
                continue;
            }

            store.queueDataMap.put(uuid, queueData);
        }

        return store;
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        CraftTracker.LOGGER.debug("#save: {}", compound);

        ListTag list = new ListTag();

        for (Map.Entry<UUID, CraftingQueueData> entry : this.queueDataMap.entrySet()) {
            CompoundTag queueDataCompound = new CompoundTag();

            CraftingQueueData queueData = entry.getValue();
            NBTUtil.putUniqueId(queueDataCompound, "uuid", entry.getKey());
            queueData.write(queueDataCompound);

            list.add(queueDataCompound);
        }

        compound.put("queueData", list);

        return compound;
    }
}
