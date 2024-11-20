//package com.sweetrpg.crafttracker.common.storage;
//
//import com.sweetrpg.crafttracker.CraftTracker;
//import com.sweetrpg.crafttracker.common.util.NBTUtil;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.resources.ResourceLocation;
//
///**
// * This class represents an entry in the crafting queue's end products list.
// */
//public class CraftingQueueData {
//
//    private final CraftingQueueStorage storage;
//    private ResourceLocation itemId;
//    private int quantity;
//
//    protected CraftingQueueData(CraftingQueueStorage storage) {
//        this.storage = storage;
//    }
//
//    protected CraftingQueueData(CraftingQueueStorage storage, ResourceLocation itemId, int quantity) {
//        this.storage = storage;
//        this.itemId = itemId;
//        this.quantity = quantity;
//    }
//
////    public void addItem(ResourceLocation itemId, int quantity) {
////        CraftTracker.LOGGER.debug("CraftingQueueData#addItem: {}, quantity {}", itemId, quantity);
////
////        this.endProducts.compute(itemId, (k, v) -> (v == null ? 0 : v) + quantity);
////
////        this.storage.setDirty();
////    }
////
////    public void removeItem(ResourceLocation itemId, int quantity) {
////        CraftTracker.LOGGER.debug("CraftingQueueData#removeItem: {}, quantity {}", itemId, quantity);
////
////        this.endProducts.computeIfPresent(itemId, (k, v) -> {
////            if(v - quantity < 1) {
////                return null;
////            }
////
////            return v - quantity;
////        });
////
////        this.storage.setDirty();
////    }
//
//    public void read(CompoundTag compound) {
//        CraftTracker.LOGGER.debug("CraftingQueueData#read");
//
//        this.itemId = NBTUtil.getResourceLocation(compound, Keys.ITEM_ID);
//        this.quantity = compound.getInt(Keys.QUANTITY);
//    }
//
//    public CompoundTag write(CompoundTag compound) {
//        CraftTracker.LOGGER.debug("CraftingQueueData#write");
//
//        NBTUtil.putResourceLocation(compound, Keys.ITEM_ID, this.itemId);
//        compound.putInt(Keys.QUANTITY, this.quantity);
//
//        return compound;
//    }
//
//    public ResourceLocation getItemId() {
//        return itemId;
//    }
//
//    public int getQuantity() {
//        return quantity;
//    }
//
//    static class Keys {
//        static String ITEM_ID = "item_id";
//        static String QUANTITY = "quantity";
//    }
//}
