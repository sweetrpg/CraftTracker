package com.sweetrpg.crafttracker.common.manager;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.storage.CraftingQueueStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.antlr.v4.misc.OrderedHashMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CraftingQueueManager {

    public static CraftingQueueManager INSTANCE = new CraftingQueueManager();

    private Map<ResourceLocation, Integer> endProducts = new OrderedHashMap<>();
    private Map<ResourceLocation, Integer> intermediateProducts = new OrderedHashMap<>();
    private Map<ResourceLocation, Integer> rawMaterials = new HashMap<>();

    private CraftingQueueStorage storage;

    public CraftingQueueManager() {
        this.storage = CraftingQueueStorage.get(Minecraft.getInstance().level);
    }

    public List<QueueItem> getEndProducts() {
        return endProducts.entrySet()
                .stream()
                .map((e) -> new QueueItem(e.getKey(), e.getValue()))
                .collect(Collectors.toUnmodifiableList());
    }

    public List<QueueItem> getIntermediates() {
        return intermediateProducts.entrySet()
                .stream()
                .map((e) -> new QueueItem(e.getKey(), e.getValue()))
                .collect(Collectors.toUnmodifiableList());
    }

    public List<QueueItem> getRawMaterials() {
        return rawMaterials.entrySet()
                .stream()
                .map((e) -> new QueueItem(e.getKey(), e.getValue()))
                .collect(Collectors.toUnmodifiableList());
    }

    public void addProduct(ResourceLocation itemId, int quantity) {
        CraftTracker.LOGGER.debug("#addProduct: {}, quantity: {}", itemId, quantity);

        RecipeManager rm = new RecipeManager(ICondition.IContext.EMPTY);

        rm.byKey(itemId).ifPresentOrElse(r -> {
                    this.endProducts.compute(itemId, (k, v) -> {
                        if(v == null) {
                            return quantity;
                        }

                        return v + quantity;
                    });

                    var playerId = Minecraft.getInstance().player.getUUID();
                    this.storage.getData(playerId).addItem(itemId, quantity);
                },
                () -> {
                    // should not have gotten here, since #addProduct should have filtered out the item
                    // since it had to ingredients
                    CraftTracker.LOGGER.warn("#computeAll: no recipe found for {}", itemId);
                });

        computeAll();
    }

    public void removeProduct(ResourceLocation itemId, int quantity) {

    }

    public void computeAll() {
        Map<ResourceLocation, Integer> intermediateProducts = new OrderedHashMap<>();
        Map<ResourceLocation, Integer> rawMaterials = new HashMap<>();

        RecipeManager rm = new RecipeManager(ICondition.IContext.EMPTY);

        this.endProducts.forEach((k, v) -> {
            rm.byKey(k).ifPresentOrElse(r -> {
                        var ingredients = r.getIngredients();

                    },
                    () -> {
                        // should not have gotten here, since #addProduct should have filtered out the item
                        // since it had to ingredients
                        CraftTracker.LOGGER.warn("#computeAll: no recipe found for {}", k);
                    });

        });

        this.intermediateProducts = intermediateProducts;
        this.rawMaterials = rawMaterials;
    }

    public void computeIntermediates() {

    }

    public class QueueItem {
        private ResourceLocation itemId;
        private int quantity;

        public QueueItem(ResourceLocation itemId, int quantity) {
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
