package com.sweetrpg.crafttracker.common.storage;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.util.NBTUtil;
import com.sweetrpg.crafttracker.common.util.Util;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A class to managed persistence of shopping list data.
 */
public class ShoppingListStorage extends WorldSavedData {

    private @Nullable UUID ownerId;
    private Vector3d shoppingListPosition;
    private boolean shoppingListVisible;
    private Map<ResourceLocation, Integer> products = new HashMap<>();

    /**
     * Default constructor.
     */
    public ShoppingListStorage() {
        super(Util.getResourcePath("shopping_list"));
    }

    /**
     * Gets the data stored.
     *
     * @return A {@link Map} of the data in this storage.
     */
    public Map<ResourceLocation, Integer> getData() {
        return this.products;
    }

    /**
     * Sets the items to be persisted.
     *
     * @param products A map of products to store
     */
    public void putData(Map<ResourceLocation, Integer> products) {
        CraftTracker.LOGGER.debug("ShoppingListStorage#putData: {}", products);

        this.products = products;

        this.setDirty();
    }

    /**
     * Given a starting NBT tag, loads the shopping list information and attaches to that tag.
     *
     * @param nbt The root tag to add the shopping list data to
     * @return A map of the shopping list data
     */
    public void load(CompoundNBT nbt) {
        CraftTracker.LOGGER.debug("ShoppingListStorage#load: {}", nbt);

        ShoppingListStorage store = new ShoppingListStorage();
        store.products.clear();

        store.ownerId = NBTUtil.getUniqueId(nbt, ShoppingListStorage.Keys.OWNER_ID);
        store.shoppingListPosition = NBTUtil.getVector3d(nbt);
        store.shoppingListVisible = nbt.getBoolean(ShoppingListStorage.Keys.SHOPPING_LIST_VISIBLE);

        ListNBT list = nbt.getList(ShoppingListStorage.Keys.LIST_DATA, Constants.NBT.TAG_COMPOUND);

        for(int i = 0; i < list.size(); ++i) {
            CompoundNBT productData = list.getCompound(i);

            var itemId = NBTUtil.getResourceLocation(productData, ShoppingListStorage.Keys.ITEM_ID);
            var quantity = productData.getInt(ShoppingListStorage.Keys.QUANTITY);

            store.products.put(itemId, quantity);
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
    public CompoundNBT save(CompoundNBT compound) {
        CraftTracker.LOGGER.debug("ShoppingListStorage#save: {}", compound);

        NBTUtil.putUniqueId(compound, ShoppingListStorage.Keys.OWNER_ID, this.ownerId);
        NBTUtil.putVector3d(compound, this.shoppingListPosition);
        compound.putBoolean(ShoppingListStorage.Keys.SHOPPING_LIST_VISIBLE, this.shoppingListVisible);

        ListNBT list = new ListNBT();

        this.products.forEach((k, v) -> {
            CompoundNBT itemData = new CompoundNBT();

            NBTUtil.putResourceLocation(itemData, ShoppingListStorage.Keys.ITEM_ID, k);
            itemData.putInt(ShoppingListStorage.Keys.QUANTITY, v);

            list.add(itemData);
        });

        compound.put(ShoppingListStorage.Keys.LIST_DATA, list);

        return compound;
    }

    static class Keys {
        static String OWNER_ID = "owner_id";
        static String SHOPPING_LIST_VISIBLE = "shopping_list_visible";
        static String LIST_DATA = "list_data";
        static String ITEM_ID = "item_id";
        static String QUANTITY = "quantity";
    }
}
