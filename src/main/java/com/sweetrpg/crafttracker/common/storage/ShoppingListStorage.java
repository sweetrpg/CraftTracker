package com.sweetrpg.crafttracker.common.storage;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.util.NBTUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 */
public class ShoppingListStorage extends SavedData {

    private @Nullable UUID ownerId;
    private Vec3 shoppingListPosition;
    private boolean shoppingListVisible;
    private Map<ResourceLocation, Integer> products = new HashMap<>();

    /**
     * Default constructor.
     */
    public ShoppingListStorage() {
    }

    /**
     * @param products
     */
    public void putData(Map<ResourceLocation, Integer> products) {
        CraftTracker.LOGGER.debug("ShoppingListStorage#putData: {}", products);

        this.products = products;

        this.setDirty();
    }

    /**
     *
     * @param nbt
     * @return
     */
    public static Map<ResourceLocation, Integer> load(CompoundTag nbt) {
        CraftTracker.LOGGER.debug("ShoppingListStorage#load: {}", nbt);

        ShoppingListStorage store = new ShoppingListStorage();
        store.products.clear();

        store.ownerId = NBTUtil.getUniqueId(nbt, ShoppingListStorage.Keys.OWNER_ID);
        store.shoppingListPosition = NBTUtil.getVector3d(nbt);
        store.shoppingListVisible = nbt.getBoolean(ShoppingListStorage.Keys.SHOPPING_LIST_VISIBLE);

        ListTag list = nbt.getList(ShoppingListStorage.Keys.LIST_DATA, Tag.TAG_COMPOUND);

        for(int i = 0; i < list.size(); ++i) {
            CompoundTag productData = list.getCompound(i);

            var itemId = NBTUtil.getResourceLocation(productData, ShoppingListStorage.Keys.ITEM_ID);
            var quantity = productData.getInt(ShoppingListStorage.Keys.QUANTITY);

            store.products.put(itemId, quantity);
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
        CraftTracker.LOGGER.debug("ShoppingListStorage#save: {}", compound);

        NBTUtil.putUniqueId(compound, ShoppingListStorage.Keys.OWNER_ID, this.ownerId);
        NBTUtil.putVector3d(compound, this.shoppingListPosition);
        compound.putBoolean(ShoppingListStorage.Keys.SHOPPING_LIST_VISIBLE, this.shoppingListVisible);

        ListTag list = new ListTag();

        this.products.forEach((k, v) -> {
            CompoundTag itemData = new CompoundTag();

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
