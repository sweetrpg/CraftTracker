package com.sweetrpg.crafttracker.common.manager;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.addon.jei.CTPlugin;
import com.sweetrpg.crafttracker.common.storage.CraftingQueueStorage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingRecipe;
import org.antlr.v4.misc.OrderedHashMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CraftingQueueManager {

    public static CraftingQueueManager INSTANCE = new CraftingQueueManager();

    private Map<ResourceLocation, Integer> endProducts = new OrderedHashMap<>();
    private Map<ResourceLocation, Integer> intermediateProducts = new OrderedHashMap<>();
    private Map<ResourceLocation, Integer> rawMaterials = new HashMap<>();

    private CraftingQueueStorage storage;

    public CraftingQueueManager() {
        this.storage = new CraftingQueueStorage();
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

        var rm = CTPlugin.jeiRuntime.getRecipeManager();

        rm.createRecipeCategoryLookup().get()
                .peek(c -> CraftTracker.LOGGER.debug("category: {}", c))
                .map(c -> c.getRecipeType())
                .peek(t -> CraftTracker.LOGGER.debug("type: {}", t))
                .flatMap(t -> rm.createRecipeLookup(t).get())
                .peek(r -> CraftTracker.LOGGER.debug("recipe: {}", r))
                .filter(r -> r instanceof CraftingRecipe)
                .map(r -> CraftingRecipe.class.cast(r))
                .peek(r -> CraftTracker.LOGGER.debug("CraftingRecipe: {}", r.getId()))
                .filter(cr -> cr.getId().equals(itemId))
                .peek(cr -> CraftTracker.LOGGER.debug("{}: {}", itemId, cr))
                .findFirst()
                .ifPresentOrElse(r -> {
                            CraftTracker.LOGGER.debug("r: {}", r);
                            endProducts.compute(itemId, (k, v) -> v == null ? quantity : v + quantity);
                        },
                        () -> {
                            CraftTracker.LOGGER.warn("No recipe found for {}", itemId);
                        });

        computeAll();
    }

    public void removeProduct(ResourceLocation itemId, int quantity) {

    }

    public void computeAll() {
        Map<ResourceLocation, Integer> intermediateProducts = new OrderedHashMap<>();
        Map<ResourceLocation, Integer> rawMaterials = new HashMap<>();

        var rm = CTPlugin.jeiRuntime.getRecipeManager();

//        RecipeManager rm = new RecipeManager(ICondition.IContext.EMPTY);

        this.endProducts.forEach((k, v) -> {

//            rm.byKey(k).ifPresentOrElse(r -> {
//                        var ingredients = r.getIngredients();
//
//                    },
//                    () -> {
//                        // should not have gotten here, since #addProduct should have filtered out the item
//                        // since it had to ingredients
//                        CraftTracker.LOGGER.warn("#computeAll: no recipe found for {}", k);
//                    });

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
