package com.sweetrpg.crafttracker.common.manager;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.model.CraftingQueueProduct;
import com.sweetrpg.crafttracker.common.storage.CraftingQueueStorage;
import com.sweetrpg.crafttracker.common.util.DebugUtil;
import com.sweetrpg.crafttracker.common.util.InventoryUtil;
import com.sweetrpg.crafttracker.common.util.RecipeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.lang3.ObjectUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

public class CraftingQueueManager {

    public static CraftingQueueManager INSTANCE = new CraftingQueueManager();

    private static final int MAX_PROCESSING_LEVEL = 2;

    public static final Path STORAGE_DIR = FMLPaths.GAMEDIR.get().resolve("craft_tracker");

    private Map<ResourceLocation, CraftingQueueProduct> endProducts = new HashMap<>();
    private Map<ResourceLocation, Integer> intermediateProducts = new HashMap<>();
    private Map<ResourceLocation, Integer> rawMaterials = new HashMap<>();
    private Map<ResourceLocation, Integer> fuel = new HashMap<>();

    public CraftingQueueManager() {
    }

    public void load(Player player) {
        CraftTracker.LOGGER.info("Loading crafting queue for {}", player);

        Path file = STORAGE_DIR.resolve("queue.nbt").toAbsolutePath();
        CraftTracker.LOGGER.debug("file: {}", file);

        try {
            try (InputStream in = Files.newInputStream(file, StandardOpenOption.READ)) {
                var data = NbtIo.readCompressed(in);
                var products = CraftingQueueStorage.load(data);
                products.forEach((k, v) -> v.setRecipes(RecipeUtil.getRecipesFor(k)));
                this.endProducts = products;
                this.computeAll();
            }
        }
        catch (IOException e) {
            CraftTracker.LOGGER.error("An error occurred while loading crafting queue [" + file + "]", e);
        }
    }

    public void save(Player player) {
        CraftTracker.LOGGER.info("Saving crafting queue for {}", player);

        Path file = STORAGE_DIR.resolve("queue.nbt").toAbsolutePath();
        CraftTracker.LOGGER.debug("file: {}", file);

        try {
            Files.createDirectories(file);
        }
        catch (FileAlreadyExistsException e) {
            // ignore
        }
        catch (IOException e) {
            CraftTracker.LOGGER.error("An error occurred while creating directory for crafting queue [" + file + "]", e);
        }

        try {
            boolean overwritten = Files.deleteIfExists(file);
            try (OutputStream out = Files.newOutputStream(file, StandardOpenOption.CREATE)) {
                var root = new CompoundTag();
                var storage = new CraftingQueueStorage();
                storage.putData(this.endProducts);
                var data = storage.save(root);
                NbtIo.writeCompressed(data, out);
            }
        }
        catch (IOException e) {
            CraftTracker.LOGGER.error("An error occurred while saving crafting queue [" + file + "]", e);
        }
    }

    public List<ProductItem> getEndProducts() {
        return endProducts.entrySet()
                .stream()
                .map(e ->
                        new ProductItem(e.getKey(), e.getValue().getIterations(), new ArrayList<>()))
                .collect(Collectors.toUnmodifiableList());
    }

    public List<QueueItem> getIntermediates() {
        return intermediateProducts.entrySet()
                .stream()
                .map(e -> new QueueItem(e.getKey(), e.getValue()))
                .collect(Collectors.toUnmodifiableList());
    }

    public List<QueueItem> getRawMaterials() {
        return rawMaterials.entrySet()
                .stream()
                .map(e -> new QueueItem(e.getKey(), e.getValue()))
                .collect(Collectors.toUnmodifiableList());
    }

    public List<QueueItem> getFuel() {
        return fuel.entrySet()
                .stream()
                .map(e -> new QueueItem(e.getKey(), e.getValue()))
                .collect(Collectors.toUnmodifiableList());
    }

    public void addProduct(Player player, ResourceLocation itemId, int quantity) {
        CraftTracker.LOGGER.debug("CraftingQueueManager#addProduct: {}, quantity: {}", itemId, quantity);

        if(quantity < 1) return;

        var recipes = RecipeUtil.getRecipesFor(itemId);

        if(recipes.size() > 0) {
            CraftTracker.LOGGER.debug("recipes: {}", recipes.stream().map(DebugUtil::printRecipe));

            var product = new CraftingQueueProduct(itemId, recipes, quantity);
            endProducts.compute(itemId, (rl, p) -> p == null ? product :
                    new CraftingQueueProduct(p.getProductId(), p.getRecipes(), p.getIterations() + quantity));

            computeAll();

            this.save(player);
        }
        else {
            CraftTracker.LOGGER.info("Not adding {} to queue, since there are no recipes for it.", itemId);
        }
    }

    /**
     * A convenience method to adjust the quantity of a product.
     * This will call the appropriate add* or remove* method.
     *
     * @param player
     * @param itemId
     * @param quantity
     */
    public void adjustProduct(Player player, ResourceLocation itemId, int quantity) {
        CraftTracker.LOGGER.debug("CraftingQueueManager#adjustProduct: {}, quantity: {}", itemId, quantity);

        if(quantity < 0)
            removeProduct(player, itemId, -quantity);
        else if(quantity > 0)
            addProduct(player, itemId, quantity);
    }

    public void removeProduct(Player player, ResourceLocation itemId) {
        CraftTracker.LOGGER.debug("CraftingQueueManager#removeProduct: {}", itemId);

        this.endProducts.remove(itemId);

        computeAll();

        this.save(player);
    }

    public void removeProduct(Player player, ResourceLocation itemId, int quantity) {
        CraftTracker.LOGGER.debug("CraftingQueueManager#removeProduct: {}, quantity: {}", itemId, quantity);

        if(quantity < 1) return;

        var product = this.endProducts.get(itemId);
        if(product == null) {
            CraftTracker.LOGGER.info("No product found in queue for {}", itemId);
            return;
        }

        // if we would remove more than what's left in the queue, remove it entirely
        int newQuantity = product.getIterations() - quantity;
        if(newQuantity < 1) {
            CraftTracker.LOGGER.info("Removing item from queue storage: {}", itemId);
            this.removeProduct(player, itemId);
        }
        else {
            CraftTracker.LOGGER.info("Adjusting quantity of item in queue storage to {}: {}", quantity, itemId);
            this.endProducts.computeIfPresent(itemId, (k, v) -> {
                var cqp = new CraftingQueueProduct(itemId, v.getRecipes(), v.getIterations() - quantity);
                return cqp;
            });
        }

        computeAll();

        this.save(player);
    }

    public void removeAll() {
        CraftTracker.LOGGER.debug("CraftingQueueManager#removeAll");

        this.endProducts.clear();
        this.intermediateProducts.clear();
        this.rawMaterials.clear();
        this.fuel.clear();
    }

    /**
     * Compute all the intermediate items, raw materials, and fuel needed to make the recipes
     */
    public void computeAll() {
        CraftTracker.LOGGER.debug("CraftingQueueManager#computeAll");

//        this.intermediateProducts.clear();
//        this.rawMaterials.clear();
//        this.fuel.clear();

        ProcessingContext ctx = new ProcessingContext();

        this.endProducts.forEach((k, v) -> {
            CraftTracker.LOGGER.debug("product: {}", v);
            this.computeProduct(ctx, v);
            CraftTracker.LOGGER.debug("context after computation of product {}: {}", v, ctx);
        });

        coalesceProducts(ctx);

        CraftTracker.LOGGER.debug("final state after computation: {}", this);
    }

    void computeProduct(ProcessingContext ctx, CraftingQueueProduct product) {
        CraftTracker.LOGGER.debug("CraftingQueueManager#computeProduct: {}", product);

        var index = Math.min(product.getIndex(), product.getRecipes().size());
        var recipe = product.getRecipes().get(index);

        var computedRecipe = this.computeRecipe(recipe, product.getIterations(), 0);
        CraftTracker.LOGGER.debug("#computeProduct: computedRecipe {}", computedRecipe);
        ctx.computedRecipes.add(computedRecipe);
    }

    void coalesceProducts(ProcessingContext ctx) {
        CraftTracker.LOGGER.debug("#coalesceProducts: {}", ctx);

        this.intermediateProducts.clear();
        this.rawMaterials.clear();
        this.fuel.clear();

        ctx.computedRecipes.forEach(r -> {
            CraftTracker.LOGGER.debug("coalescing recipe: {}", r);

            r.intermediateProducts.forEach((ik, iv) -> {
                this.intermediateProducts.compute(ik, (ik1, iv1) -> {
                    return ObjectUtils.defaultIfNull(iv1, 0) + iv;
                });
            });
            r.rawMaterials.forEach((rk, rv) -> {
                this.rawMaterials.compute(rk, (rk1, rv1) -> {
                    return ObjectUtils.defaultIfNull(rv1, 0) + rv;
                });
            });
            r.fuel.forEach((fk, fv) -> {
                this.fuel.compute(fk, (fk1, fv1) -> {
                    return ObjectUtils.defaultIfNull(fv1, 0) + fv;
                });
            });
        });

        CraftTracker.LOGGER.debug("coalesce complete: {}", this);
    }

    ComputedRecipe computeRecipe(Recipe<?> recipe, int iterations, int depth) {
        CraftTracker.LOGGER.debug("CraftingQueueManager#computeRecipe: {}", DebugUtil.printRecipe(recipe));

        var computedRecipe = new ComputedRecipe(recipe.getId());

//        var recipeItemId = recipe.getResultItem().getItem().getRegistryName();
//        if(ctx.handledItems.contains(recipeItemId)) {
//            CraftTracker.LOGGER.debug("context has already processed item: {}", recipeItemId);
//            return;
//        }

        var ingredients = recipe.getIngredients();
        CraftTracker.LOGGER.debug("ingredients: {}", ingredients.stream().map(DebugUtil::printIngredient).toList());

//        if(RecipeUtil.areIngredientsSame(ingredients)) {
//            CraftTracker.LOGGER.debug("ingredients are the same: {}", ingredients);
//            var id = recipe.getResultItem().getItem().getRegistryName();
//            CraftTracker.LOGGER.debug("id: {}", id);
//            if(this.intermediateProducts.containsKey(id)) {
//                CraftTracker.LOGGER.debug("intermediates has this ingredient already: {}", id);
//                var quantity = ctx.intermediateProducts.remove(id);
//                ctx.intermediateProducts.remove(id);
//                this.updateRawMaterials(ctx, id, quantity * iterations);
//
//                return;
//            }
//        }

        // process ingredients
        for(Ingredient ingredient : ingredients) {
            CraftTracker.LOGGER.debug("ingredient: {}", DebugUtil.printIngredient(ingredient));

            if(ingredient.isEmpty() || ingredient.getItems().length == 0) {
                continue;
            }

            int itemIndex = RecipeUtil.chooseLeastExpensiveOf(ingredient.getItems());
            Item item = ingredient.getItems()[itemIndex].getItem();
            int amountRequired = 1; // TODO?

//            ctx.handledItems.add(item.getRegistryName());

            CraftTracker.LOGGER.debug("item: {}", DebugUtil.printItem(item));
            var id = item.getRegistryName();
            CraftTracker.LOGGER.debug("id: {}", id);
            var subRecipes = RecipeUtil.getRecipesFor(id);
            CraftTracker.LOGGER.debug("subRecipes: {}", subRecipes.stream().map(DebugUtil::printRecipe).toList());

            // check if player already has the item
            var player = Minecraft.getInstance().player;
            var hasInInventory = InventoryUtil.getQuantityOf(player, item.getRegistryName());
            var needsQty = (amountRequired * iterations) - hasInInventory;

            if(needsQty < 1) {
                CraftTracker.LOGGER.debug("player already has enough of item {} ({} >= {})", id, hasInInventory, amountRequired);
                continue;
            }

            if(subRecipes.isEmpty() || depth >= MAX_PROCESSING_LEVEL) {
                CraftTracker.LOGGER.debug("subRecipes is empty; ingredient {} is a raw material", DebugUtil.printIngredient(ingredient));
                // no recipes for this ingredient, so it's a raw material
//                this.updateRawMaterials(ctx, id, needsQty);
                computedRecipe.rawMaterials.compute(id,
                        (itemId, quantity) ->
                                ObjectUtils.defaultIfNull(quantity, 0) + amountRequired);
            }
            else {
                CraftTracker.LOGGER.debug("subRecipes has {} items; ingredient {} is an intermediate product", subRecipes.size(), DebugUtil.printIngredient(ingredient));

                int subIndex = RecipeUtil.chooseLeastExpensiveOf(subRecipes);
                CraftTracker.LOGGER.debug("least expensive item index: {}", subIndex);
                var chosenSubRecipe = subRecipes.get(subIndex);
                CraftTracker.LOGGER.debug("chosenSubRecipe: {}", DebugUtil.printRecipe(chosenSubRecipe));

//                            var amountProduced = chosenSubRecipe.getResultItem().getCount();

                computedRecipe.intermediateProducts.compute(id,
                        (itemId, quantity) ->
                                ObjectUtils.defaultIfNull(quantity, 0) + needsQty);

//                ctx.processingLevel++;
                var computedSubRecipe = this.computeRecipe(chosenSubRecipe, iterations, depth + 1);

                // merge subrecipe items into this
                CraftTracker.LOGGER.debug("merging subrecipe contents: {} into this: {}", computedSubRecipe, computedRecipe);
                computedSubRecipe.intermediateProducts.forEach((itemId, amount) -> {
                    computedRecipe.intermediateProducts.compute(itemId, (k1, v1) -> {
                        return ObjectUtils.defaultIfNull(v1, 0) + amount;
                    });
                });
                computedSubRecipe.rawMaterials.forEach((itemId, amount) -> {
                    computedRecipe.rawMaterials.compute(itemId, (k1, v1) -> {
                        return ObjectUtils.defaultIfNull(v1, 0) + amount;
                    });
                });
                computedSubRecipe.fuel.forEach((itemId, amount) -> {
                    computedRecipe.fuel.compute(itemId, (k1, v1) -> {
                        return ObjectUtils.defaultIfNull(v1, 0) + amount;
                    });
                });
//                ctx.processingLevel--;

                CraftTracker.LOGGER.debug("this looks like: {}", this);
            }
        }

        return computedRecipe;
    }

    private void updateRawMaterials(ProcessingContext ctx, ResourceLocation id, int amountNeeded) {
        ctx.rawMaterials.compute(id,
                (itemId, quantity) ->
                        ObjectUtils.defaultIfNull(quantity, 0) + amountNeeded);
    }

    public static class ProductItem {
        private ResourceLocation itemId;
        private int quantity;
        private List<ResourceLocation> categories;

        public ProductItem(ResourceLocation itemId, int quantity, List<ResourceLocation> categories) {
            this.itemId = itemId;
            this.quantity = quantity;
            this.categories = categories;
        }

        public ResourceLocation getItemId() {
            return itemId;
        }

        public int getQuantity() {
            return quantity;
        }

        public List<ResourceLocation> getCategories() {
            return categories;
        }

        @Override
        public String toString() {
            return "ProductItem{" +
                    "itemId=" + itemId +
                    ", quantity=" + quantity +
                    ", categories=" + categories +
                    '}';
        }
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

        @Override
        public String toString() {
            return "QueueItem{" +
                    "itemId=" + itemId +
                    ", quantity=" + quantity +
                    '}';
        }
    }

    class ProcessingContext {
        Map<ResourceLocation, Integer> intermediateProducts = new HashMap<>();
        Map<ResourceLocation, Integer> rawMaterials = new HashMap<>();
        Map<ResourceLocation, Integer> fuel = new HashMap<>();
        Set<ResourceLocation> handledItems = new HashSet<>();
        //        int processingLevel;
        List<ComputedRecipe> computedRecipes = new ArrayList<>();

        @Override
        public String toString() {
            return MessageFormat.format("""
                            ProcessingContext[
                              intermediateProducts={0}
                              rawMaterials={1}
                              fuel={2}
                              handledItems={3}
                              computedRecipes={4}
                            ]
                            """,
                    intermediateProducts, rawMaterials, fuel, handledItems, computedRecipes);
        }
    }

    class ComputedRecipe {
        ResourceLocation recipeId;
        Map<ResourceLocation, Integer> intermediateProducts = new HashMap<>();
        Map<ResourceLocation, Integer> rawMaterials = new HashMap<>();
        Map<ResourceLocation, Integer> fuel = new HashMap<>();

        ComputedRecipe(ResourceLocation recipeId) {
            this.recipeId = recipeId;
        }

        @Override
        public String toString() {
            return MessageFormat.format("""
                            ComputedRecipe[
                              recipeId={0}
                              intermediateProducts={1}
                              rawMaterials={2}
                              fuel={3}
                            ]
                            """,
                    recipeId, intermediateProducts, rawMaterials, fuel);
        }
    }

    @Override
    public String toString() {
        return MessageFormat.format("""                
                        CraftingQueueManager[
                          endProducts={0}
                          intermediateProducts={1}
                          rawMaterials={2}
                          fuel={3}
                        ]
                        """,
                endProducts, intermediateProducts, rawMaterials, fuel);
    }
}
