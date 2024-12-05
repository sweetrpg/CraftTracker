package com.sweetrpg.crafttracker.common.storage;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.model.CraftingQueueProduct;
import com.sweetrpg.crafttracker.common.util.NBTUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class handles storage of all crafting queue information for a player.
 * <p>
 * This includes:
 * * Position and visibility of the craft list window
 * * Position and visibility of the shopping list window
 * * The list of end products in the queue by item ID and quantity
 */
public class CraftingQueueStorage extends SavedData {

    private @Nullable UUID ownerId;
    private Vec3 craftingQueuePosition;
    private boolean craftingQueueVisible;
    private Map<ResourceLocation, CraftingQueueProduct> products = new HashMap<>();

    /**
     *
     */
    public CraftingQueueStorage() {
    }

    /**
     * @param products
     */
    public void putData(Map<ResourceLocation, CraftingQueueProduct> products) {
        CraftTracker.LOGGER.debug("CraftingQueueStorage#putData: {}", products);

        this.products = products;

        this.setDirty();
    }

    /**
     *
     * @param nbt
     * @return
     */
    public static Map<ResourceLocation, CraftingQueueProduct> load(CompoundTag nbt) {
        CraftTracker.LOGGER.debug("CraftingQueueStorage#load: {}", nbt);

        CraftingQueueStorage store = new CraftingQueueStorage();
        store.products.clear();

        store.ownerId = NBTUtil.getUniqueId(nbt, Keys.OWNER_ID);
        store.craftingQueuePosition = NBTUtil.getVector3d(nbt);
        store.craftingQueueVisible = nbt.getBoolean(Keys.CRAFTING_QUEUE_VISIBLE);

        ListTag list = nbt.getList(Keys.QUEUE_DATA, Tag.TAG_COMPOUND);

        for(int i = 0; i < list.size(); ++i) {
            CompoundTag productData = list.getCompound(i);

            var itemId = NBTUtil.getResourceLocation(productData, Keys.ITEM_ID);
            var quantity = productData.getInt(Keys.QUANTITY);
            var index = productData.getInt(Keys.INDEX);

            var product = new CraftingQueueProduct(itemId, Arrays.asList(), quantity);
            product.setIndex(index);
            store.products.put(itemId, product);
        }

        return store.products;
    }

    /**
     *
     * @param compound
     * @return
     */
    @Override
    public CompoundTag save(CompoundTag compound) {
        CraftTracker.LOGGER.debug("CraftingQueueStorage#save: {}", compound);

        NBTUtil.putUniqueId(compound, Keys.OWNER_ID, this.ownerId);
        NBTUtil.putVector3d(compound, this.craftingQueuePosition);
        compound.putBoolean(Keys.CRAFTING_QUEUE_VISIBLE, this.craftingQueueVisible);

        ListTag list = new ListTag();

        this.products.forEach((k, v) -> {
            CompoundTag productData = new CompoundTag();

            NBTUtil.putResourceLocation(productData, Keys.ITEM_ID, v.getProductId());
            productData.putInt(Keys.QUANTITY, v.getIterations());
            productData.putInt(Keys.INDEX, v.getIndex());

            list.add(productData);
        });

        compound.put(Keys.QUEUE_DATA, list);

        return compound;
    }

    static class Keys {
        static String OWNER_ID = "owner_id";
        static String CRAFTING_QUEUE_VISIBLE = "crafting_queue_visible";
        static String QUEUE_DATA = "queue_data";
        static String ITEM_ID = "item_id";
        static String QUANTITY = "quantity";
        static String INDEX = "index";
    }
}
