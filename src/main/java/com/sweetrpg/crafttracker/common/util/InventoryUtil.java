package com.sweetrpg.crafttracker.common.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.Predicate;

/**
 * Miscellaneous utility functions for player inventory
 */
public class InventoryUtil {

    /**
     * Determine the quantity of a particular item in the player's inventory.
     *
     * @param player The player to inspect
     * @param itemId The item to look for
     * @return The amount of the item
     */
    public static int getQuantityOf(Player player, ResourceLocation itemId) {
        var inventory = player.getInventory();

        var item = ForgeRegistries.ITEMS.getValue(itemId);
        var stack = item.getDefaultInstance();

        if(inventory.contains(stack)) {
            return inventory.items.stream()
                    .filter(inv -> ForgeRegistries.ITEMS.getKey(inv.getItem()).equals(itemId))
                    .map(inv -> inv.getCount())
                    .findFirst()
                    .orElse(0);
        }

        return 0;
    }

    public static Pair<ItemStack, Integer> findStack(IItemHandler source, Predicate<ItemStack> searchCriteria) {
        for(int i = 0; i < source.getSlots(); i++) {

            ItemStack stack = source.getStackInSlot(i);
            if(searchCriteria.test(stack)) {
                return Pair.of(stack.copy(), i);
            }
        }

        return null;
    }

    public static void transferStacks(IItemHandlerModifiable source, IItemHandler target) {
        for(int i = 0; i < source.getSlots(); i++) {
            ItemStack stack = source.getStackInSlot(i);
            source.setStackInSlot(i, addItem(target, stack));
        }
    }

    public static ItemStack addItem(IItemHandler target, ItemStack remaining) {
        // Try to insert item into all slots
        for(int i = 0; i < target.getSlots(); i++) {
            if(target.isItemValid(i, remaining)) {
                remaining = target.insertItem(i, remaining, false);
            }

            if(remaining.isEmpty()) {
                break;
            }
        }
        return remaining;
    }

}
