package com.sweetrpg.crafttracker.common.manager;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.storage.ShoppingListStorage;
import com.sweetrpg.crafttracker.common.util.Util;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manages the shopping list.
 */
public class ShoppingListManager {

    public static ShoppingListManager INSTANCE = new ShoppingListManager();

    private Map<ResourceLocation, Integer> items = new HashMap<>();

    /**
     * Default constructor.
     */
    public ShoppingListManager() {
    }

    /**
     * Loads the shopping list information from storage for the specified player.
     *
     * @param player The player whose shopping list should be loaded
     */
    public void load(PlayerEntity player) {
        CraftTracker.LOGGER.info("Loading shopping list for {}", player);

        Path file = Util.getStoragePath().resolve("shopping.nbt").toAbsolutePath();
        CraftTracker.LOGGER.debug("file: {}", file);

        try {
            InputStream in = Files.newInputStream(file, StandardOpenOption.READ);
            var data = CompressedStreamTools.readCompressed(in);
            var storage = new ShoppingListStorage();
            storage.load(data);
            this.items = storage.getData();
        }
        catch (NoSuchFileException e) {
            // ignore
        }
        catch (IOException e) {
            CraftTracker.LOGGER.error("An error occurred while loading shopping list [" + file + "]", e);
        }
    }

    /**
     * Saves the shopping list information to storage for the specified player.
     *
     * @param player The player whose shopping list should be saved
     */
    public void save(PlayerEntity player) {
        CraftTracker.LOGGER.info("Saving shopping list for {}", player);

        Path file = Util.getStoragePath().resolve("shopping.nbt").toAbsolutePath();
        CraftTracker.LOGGER.debug("file: {}", file);

        try {
            Files.createDirectories(file);
        }
        catch (FileAlreadyExistsException e) {
            // ignore
        }
        catch (IOException e) {
            CraftTracker.LOGGER.error("An error occurred while creating directory for shopping list [" + file + "]", e);
        }

        try {
            boolean overwritten = Files.deleteIfExists(file);
            try (OutputStream out = Files.newOutputStream(file, StandardOpenOption.CREATE)) {
                var root = new CompoundNBT();
                var storage = new ShoppingListStorage();
                storage.putData(this.items);
                var data = storage.save(root);
                CompressedStreamTools.writeCompressed(data, out);
            }
        }
        catch (IOException e) {
            CraftTracker.LOGGER.error("An error occurred while saving shopping list [" + file + "]", e);
        }
    }

    /**
     * Gets the items in the list.
     *
     * @return A {@link List} of the items
     */
    public List<ListItem> getItems() {
        return items.entrySet()
                .stream()
                .map(e ->
                        new ShoppingListManager.ListItem(e.getKey(), e.getValue()))
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Clears all items from the shopping list.
     *
     * @param player The player whose shopping list should be cleared
     */
    public void clearItems(PlayerEntity player) {
        CraftTracker.LOGGER.debug("#clearItems: {}", player);

        this.items.clear();

        this.save(player);
    }

    /**
     * Adds an item to the shopping list. Merges with an existing item if there is one.
     *
     * @param player   The player whose shopping list is updated
     * @param itemId   The ID of the item to add
     * @param quantity The amount of the item to add
     */
    public void addItem(PlayerEntity player, ResourceLocation itemId, int quantity) {
        CraftTracker.LOGGER.debug("#addItem: {}", player);

        this.items.compute(itemId, (k, v) -> {
            if(v == null) {
                return quantity;
            }

            return v + quantity;
        });

        this.save(player);
    }

    /**
     * Removes an item from the shopping list
     *
     * @param player   The player whose shopping list is updated
     * @param itemId   The ID of the item to remove
     * @param quantity The amount of the item to remove. If the quantity is equal to or greater than what is currently
     *                 in the list, the entire entry is removed.
     */
    public void removeItem(PlayerEntity player, ResourceLocation itemId, int quantity) {
        CraftTracker.LOGGER.debug("#removeItem: {}", player);

        this.items.computeIfPresent(itemId, (k, v) -> {
            var newQuantity = v - quantity;
            if(newQuantity < 1) {
                return null;
            }

            return newQuantity;
        });

        this.save(player);
    }

    /**
     * A wrapper value object for returning list item information to the caller.
     */
    public class ListItem {
        private ResourceLocation itemId;
        private int quantity;

        /**
         * Default constructor.
         *
         * @param itemId   The ID of the item
         * @param quantity The quantity of the item
         */
        public ListItem(ResourceLocation itemId, int quantity) {
            this.itemId = itemId;
            this.quantity = quantity;
        }

        public ResourceLocation getItemId() {
            return itemId;
        }

        public void setItemId(ResourceLocation itemId) {
            this.itemId = itemId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        @Override
        public String toString() {
            return MessageFormat.format("ListItem[ itemId={0}, quantity={1} ]",
                    itemId, quantity);
        }
    }
}
