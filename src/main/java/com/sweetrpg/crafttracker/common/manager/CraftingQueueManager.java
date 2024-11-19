package com.sweetrpg.crafttracker.common.manager;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.addon.jei.CTPlugin;
import com.sweetrpg.crafttracker.common.model.CraftingQueueProduct;
import com.sweetrpg.crafttracker.common.storage.CraftingQueueStorage;
import com.sweetrpg.crafttracker.common.util.RecipeUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.antlr.v4.misc.OrderedHashMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CraftingQueueManager {

    public static CraftingQueueManager INSTANCE = new CraftingQueueManager();

    private Map<ResourceLocation, CraftingQueueProduct> endProducts = new OrderedHashMap<>();
    private Map<ResourceLocation, Integer> intermediateProducts = new OrderedHashMap<>();
    private Map<ResourceLocation, Integer> rawMaterials = new HashMap<>();

//    private CraftingQueueStorage storage;

    public CraftingQueueManager() {
//        this.storage = new CraftingQueueStorage();
    }

//    public List<QueueItem> getEndProducts() {
//        return endProducts.entrySet()
//                .stream()
//                .map((e) -> new QueueItem(e.getKey(), e.getValue()))
//                .collect(Collectors.toUnmodifiableList());
//    }
//
//    public List<QueueItem> getIntermediates() {
//        return intermediateProducts.entrySet()
//                .stream()
//                .map((e) -> new QueueItem(e.getKey(), e.getValue()))
//                .collect(Collectors.toUnmodifiableList());
//    }
//
//    public List<QueueItem> getRawMaterials() {
//        return rawMaterials.entrySet()
//                .stream()
//                .map((e) -> new QueueItem(e.getKey(), e.getValue()))
//                .collect(Collectors.toUnmodifiableList());
//    }

    public void addProduct(Level level, ResourceLocation itemId, int quantity) {
        CraftTracker.LOGGER.debug("CraftingQueueManager#addProduct: {}, quantity: {}", itemId, quantity);

        var recipes = RecipeUtil.getRecipesFor(itemId);

        if(recipes.size() > 0) {
            CraftTracker.LOGGER.debug("recipes: {}", recipes);

            var product = new CraftingQueueProduct(itemId, recipes, quantity);
            endProducts.compute(itemId, (rl, p) -> p == null ? product :
                    new CraftingQueueProduct(p.getItemId(), p.getRecipes(), p.getQuantity() + quantity));

            CraftingQueueStorage.get(level).putData(itemId, quantity);

            computeAll();
        }
        else {
            CraftTracker.LOGGER.info("Not adding {} to queue, since there are no recipes for it.", itemId);
        }
    }

    public void removeProduct(Level level, ResourceLocation itemId, int quantity) {
        CraftTracker.LOGGER.debug("CraftingQueueManager#removeProduct: {}, quantity: {}", itemId, quantity);

        var product = this.endProducts.get(itemId);
        if(product == null) {
            CraftTracker.LOGGER.info("No product found in queue for {}", itemId);
            return;
        }

        // if we would remove more than what's left in the queue, remove it entirely
        int newQuantity = product.getQuantity() - quantity;
        if(newQuantity < 1) {
            CraftTracker.LOGGER.info("Removing item from queue storage: {}", itemId);
            CraftingQueueStorage.get(level).removeData(itemId);
        }
        else {
            CraftTracker.LOGGER.info("Adjusting quantity of item in queue storage to {}: {}", quantity, itemId);
            CraftingQueueStorage.get(level).putData(itemId, newQuantity);
        }

        computeAll();
    }

    public void computeAll() {
        CraftTracker.LOGGER.debug("CraftingQueueManager#computeAll");

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
