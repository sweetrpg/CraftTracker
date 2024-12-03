package com.sweetrpg.crafttracker.common.manager;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.storage.ShoppingListStorage;
import com.sweetrpg.crafttracker.common.util.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShoppingListManager {

    public static ShoppingListManager INSTANCE = new ShoppingListManager();

    private Map<ResourceLocation, Integer> items = new HashMap<>();

    public ShoppingListManager() {
    }

    public void load(Player player) {
        CraftTracker.LOGGER.info("Loading shopping list for {}", player);

        Path file = Util.getStoragePath().resolve("shopping.nbt").toAbsolutePath();
        CraftTracker.LOGGER.debug("file: {}", file);

        try {
            try (InputStream in = Files.newInputStream(file, StandardOpenOption.READ)) {
                var data = NbtIo.readCompressed(in);
                var items = ShoppingListStorage.load(data);
                this.items = items;
            }
        }
        catch (IOException e) {
            CraftTracker.LOGGER.error("An error occurred while loading shopping list [" + file + "]", e);
        }
    }

    public void save(Player player) {
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
                var root = new CompoundTag();
                var storage = new ShoppingListStorage();
                storage.putData(this.items);
                var data = storage.save(root);
                NbtIo.writeCompressed(data, out);
            }
        }
        catch (IOException e) {
            CraftTracker.LOGGER.error("An error occurred while saving shopping list [" + file + "]", e);
        }
    }

    public List<ListItem> getItems() {
        return items.entrySet()
                .stream()
                .map(e ->
                        new ShoppingListManager.ListItem(e.getKey(), e.getValue()))
                .collect(Collectors.toUnmodifiableList());
    }

    public void clearItems(Player player) {
        CraftTracker.LOGGER.debug("#clearItems: {}", player);

        this.items.clear();

        this.save(player);
    }

    public void addItem(Player player, ResourceLocation itemId, int quantity) {
        CraftTracker.LOGGER.debug("#addItem: {}", player);

        this.items.compute(itemId, (k, v) -> {
            if(v == null) {
                return quantity;
            }

            return v + quantity;
        });

        this.save(player);
    }

    public void removeItem(Player player, ResourceLocation itemId, int quantity) {
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

    public class ListItem {
        private ResourceLocation itemId;
        private int quantity;

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
    }
}
