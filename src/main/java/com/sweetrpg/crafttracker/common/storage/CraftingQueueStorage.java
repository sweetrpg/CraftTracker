package com.sweetrpg.crafttracker.common.storage;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.model.CraftingQueueProduct;
import com.sweetrpg.crafttracker.common.util.NBTUtil;
import com.sweetrpg.crafttracker.common.util.Util;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

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
 * * The list of end products in the queue by item ID and product information ({@link CraftingQueueProduct})
 */
public class CraftingQueueStorage extends WorldSavedData {

    private @Nullable UUID ownerId;
    private Vector3d craftingQueuePosition;
    private boolean craftingQueueVisible;
    private Map<ResourceLocation, CraftingQueueProduct> products = new HashMap<>();

    /**
     * Default constructor.
     */
    public CraftingQueueStorage() {
        super(Util.getResourcePath("crafting_queue"));
    }

    /**
     * Sets the data to store.
     *
     * @param products A map of end products to write to storage
     */
    public void putData(Map<ResourceLocation, CraftingQueueProduct> products) {
        CraftTracker.LOGGER.debug("CraftingQueueStorage#putData: {}", products);

        this.products = products;

        this.setDirty();
    }

    /**
     * Given a starting NBT tag, loads the crafting queue information and attaches to that tag.
     *
     * @param nbt The root tag to add the crafting queue data to
     * @return A map of the crafting queue data
     */
    public void load(CompoundNBT nbt) {
        CraftTracker.LOGGER.debug("CraftingQueueStorage#load: {}", nbt);

//        CraftingQueueStorage store = new CraftingQueueStorage();
        this.products.clear();

        this.ownerId = NBTUtil.getUniqueId(nbt, Keys.OWNER_ID);
        this.craftingQueuePosition = NBTUtil.getVector3d(nbt);
        this.craftingQueueVisible = nbt.getBoolean(Keys.CRAFTING_QUEUE_VISIBLE);

        ListNBT list = nbt.getList(Keys.QUEUE_DATA, Constants.NBT.TAG_COMPOUND);

        for(int i = 0; i < list.size(); ++i) {
            CompoundNBT productData = list.getCompound(i);

            var itemId = NBTUtil.getResourceLocation(productData, Keys.ITEM_ID);
            var quantity = productData.getInt(Keys.QUANTITY);
            var index = productData.getInt(Keys.INDEX);

            var product = new CraftingQueueProduct(itemId, Arrays.asList(), quantity);
            product.setIndex(index);
            this.products.put(itemId, product);
        }

//        return store.products;
    }

    /**
     * Given a populated NBT tag, writes the attached tag data to storage.
     *
     * @param compound The root tag
     * @return The root tag
     */
    @Override
    public  CompoundNBT save( CompoundNBT compound) {
        CraftTracker.LOGGER.debug("CraftingQueueStorage#save: {}", compound);

        NBTUtil.putUniqueId(compound, Keys.OWNER_ID, this.ownerId);
        NBTUtil.putVector3d(compound, this.craftingQueuePosition);
        compound.putBoolean(Keys.CRAFTING_QUEUE_VISIBLE, this.craftingQueueVisible);

        ListNBT list = new ListNBT();

        this.products.forEach((k, v) -> {
            CompoundNBT productData = new CompoundNBT();

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
