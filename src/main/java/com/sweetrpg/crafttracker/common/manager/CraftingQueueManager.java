package com.sweetrpg.crafttracker.common.manager;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.model.CraftingQueueProduct;
import com.sweetrpg.crafttracker.common.storage.CraftingQueueStorage;
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
                        new ProductItem(e.getKey(), e.getValue().getQuantity(), new ArrayList<>()))
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
            CraftTracker.LOGGER.debug("recipes: {}", recipes);

            var product = new CraftingQueueProduct(itemId, recipes, quantity);
            endProducts.compute(itemId, (rl, p) -> p == null ? product :
                    new CraftingQueueProduct(p.getItemId(), p.getRecipes(), p.getQuantity() + quantity));

            computeAll();
        }
        else {
            CraftTracker.LOGGER.info("Not adding {} to queue, since there are no recipes for it.", itemId);
        }

        this.save(player);
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
        int newQuantity = product.getQuantity() - quantity;
        if(newQuantity < 1) {
            CraftTracker.LOGGER.info("Removing item from queue storage: {}", itemId);
            this.removeProduct(player, itemId);
        }
        else {
            CraftTracker.LOGGER.info("Adjusting quantity of item in queue storage to {}: {}", quantity, itemId);
            this.endProducts.computeIfPresent(itemId, (k, v) -> {
                var cqp = new CraftingQueueProduct(itemId, v.getRecipes(), v.getQuantity() - quantity);
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
            this.computeProduct(ctx, v);
        });

        this.intermediateProducts = ctx.intermediateProducts;
        this.rawMaterials = ctx.rawMaterials;
        this.fuel = ctx.fuel;
    }

    void computeProduct(ProcessingContext ctx, CraftingQueueProduct product) {
        CraftTracker.LOGGER.debug("CraftingQueueManager#computeProduct: {}", product);

        var index = Math.min(product.getIndex(), product.getRecipes().size());
        var recipe = product.getRecipes().get(index);

        this.computeRecipe(ctx, recipe, product.getQuantity());
    }

    void computeRecipe(ProcessingContext ctx, Recipe<?> recipe, int recipeQuantity) {
        CraftTracker.LOGGER.debug("CraftingQueueManager#computeRecipe: {}", recipe);

        var ingredients = recipe.getIngredients();
        CraftTracker.LOGGER.debug("ingredients: {}", ingredients);

        if(RecipeUtil.areIngredientsSame(ingredients)) {
            CraftTracker.LOGGER.debug("ingredients are the same: {}", ingredients);
            var id = recipe.getResultItem().getItem().getRegistryName();
            CraftTracker.LOGGER.debug("id: {}", id);
            if(this.intermediateProducts.containsKey(id)) {
                CraftTracker.LOGGER.debug("intermediates has this ingredient already: {}", id);
                var quantity = ctx.intermediateProducts.remove(id);
//                var item = ForgeRegistries.ITEMS.getValue(id);
                ctx.intermediateProducts.remove(id);
                this.updateRawMaterials(ctx, id, quantity * recipeQuantity);

                return;
            }
        }

        // assemble ingredients
//        Map<ResourceLocation, Tuple<Ingredient, Integer>> ingredientCounts = new HashMap<>();
//        for(Ingredient ingredient : ingredients) {
//            CraftTracker.LOGGER.debug("ingredient: {}", ingredient);
//
//            if(ingredient.isEmpty()) {
//                CraftTracker.LOGGER.debug("no ingredients for: {}", ingredient);
//                continue;
//            }
//
////            var info = new IngredientInfo();
////            info.ingredient = ingredient;
////
////            var ingredientRecipes = Arrays.stream(ingredient.getItems())
////                    .map(itemStack -> itemStack.getItem())
////                    .flatMap(item -> RecipeUtil.getRecipesFor(item.getRegistryName()).stream())
////                    .toList();
////            CraftTracker.LOGGER.debug("ingredientRecipes: {}", ingredientRecipes);
////            info.recipeIndex = RecipeUtil.chooseLeastExpensiveOf(ingredientRecipes);
////            info.recipe = ingredientRecipes.get(info.recipeIndex);
////            info.amount = recipe.getResultItem().getCount();
//
//            Arrays.stream(ingredient.getItems())
//                    .findFirst()
//                    .map(stack -> stack.getItem())
//                    .ifPresent(item -> {
//                        ingredientCounts.compute(item.getRegistryName(), (itemId, tuple) -> {
//                            if(tuple == null) {
//                                return new Tuple<>(ingredient, 1);
//                            }
//
//                            return new Tuple<>(ingredient, tuple.getB() + 1);
//                        });
//                    });
//
////            ingredientInfos.add(info);
//        }
//        CraftTracker.LOGGER.debug("ingredientCounts: {}", ingredientCounts);

        // process ingredients
        for(Ingredient ingredient : ingredients) {
//        ingredientCounts.forEach((ingredientId, tuple) -> {
//            Ingredient ingredient = tuple.getA(); // info.ingredient;
//            int amountRequired = tuple.getB(); // info.amount;

            if(ingredient.isEmpty() || ingredient.getItems().length == 0) {
                continue;
            }

            int itemIndex = RecipeUtil.chooseLeastExpensiveOf(ingredient.getItems());
            Item item = ingredient.getItems()[itemIndex].getItem();
            int amountRequired = 1; // TODO?

//            ctx.handledItems.add(info.recipe.getResultItem().getItem().getRegistryName());
            ctx.handledItems.add(item.getRegistryName());

//            Arrays.stream(ingredient.getItems())
//                    .findFirst()
//                    .ifPresent(item -> {
            CraftTracker.LOGGER.debug("item: {}", item);
            var id = item.getRegistryName();
            CraftTracker.LOGGER.debug("id: {}", id);
            var subRecipes = RecipeUtil.getRecipesFor(id);
            CraftTracker.LOGGER.debug("subRecipes: {}", subRecipes);

            // check if player already has the item
            var player = Minecraft.getInstance().player;
            var hasInInventory = InventoryUtil.getQuantityOf(player, item.getRegistryName());
            var needsQty = (amountRequired * recipeQuantity) - hasInInventory;

            if(needsQty < 1) {
                CraftTracker.LOGGER.debug("player already has enough of item {} ({} >= {})", id, hasInInventory, amountRequired);
                return;
            }

            if(subRecipes.isEmpty() || ctx.processingLevel >= MAX_PROCESSING_LEVEL) {
                CraftTracker.LOGGER.debug("subRecipes is empty; raw material");
                // no recipes for this ingredient, so it's a raw material
                this.updateRawMaterials(ctx, id, needsQty);
            }
            else {
                CraftTracker.LOGGER.debug("subRecipes has {} items; intermediate", subRecipes.size());

                int subIndex = RecipeUtil.chooseLeastExpensiveOf(subRecipes);
                CraftTracker.LOGGER.debug("least expensive item index: {}", subIndex);
                var chosenSubRecipe = subRecipes.get(subIndex);

//                            var amountProduced = chosenSubRecipe.getResultItem().getCount();

                ctx.intermediateProducts.compute(id,
                        (itemId, quantity) ->
                                ObjectUtils.defaultIfNull(quantity, 0) + needsQty);

                ctx.processingLevel++;
                this.computeRecipe(ctx, chosenSubRecipe, recipeQuantity);
                ctx.processingLevel--;
            }
//                    });
        }

//        for(Ingredient ingredient : ingredients) {
//            CraftTracker.LOGGER.debug("ingredient: {}", ingredient);
//
//            Arrays.stream(ingredient.getItems())
//                    .findFirst()
//                    .ifPresent(item -> {
//                        CraftTracker.LOGGER.debug("item: {}", item);
//                        var id = item.getItem().getRegistryName();
//                        CraftTracker.LOGGER.debug("id: {}", id);
//                        var subRecipes = RecipeUtil.getRecipesFor(id);
//                        CraftTracker.LOGGER.debug("subRecipes: {}", subRecipes);
//
//                        if(subRecipes.isEmpty()) {
//                            CraftTracker.LOGGER.debug("subRecipes is empty; raw material");
//                            // no recipes for this ingredient, so it's a raw material
//                            this.updateRawMaterials(id, item, recipeQuantity);
//                        }
//                        else {
//                            CraftTracker.LOGGER.debug("subRecipes has {} items; intermediate", subRecipes.size());
//
//                            // check if player already has the item
//                            var player = Minecraft.getInstance().player;
//                            var hasInInventory = InventoryUtil.getQuantityOf(player, item.getItem().getRegistryName());
//                            var needsToMake = item.getCount() - hasInInventory;
////                            var inventory = .getInventory();
////                            if(inventory.contains(item)) {
////                                CraftTracker.LOGGER.debug("player has item in inventory: {}", item);
////                                inventory.items.stream()
////                                        .filter(inv -> inv.getItem().getRegistryName().equals(id))
////                                        .map(inv -> inv.getCount())
////                                        .findFirst()
////                                        .ifPresent(count -> {
////                                            CraftTracker.LOGGER.debug("adjusting count of item {} to: {}", id, count);
////
////                                            this.intermediateProducts.compute(id, (itemId, quantity) -> {
////                                                Integer finalCount = (quantity == null ? 0 : quantity) + item.getCount() - count;
////                                                if(finalCount < 1) {
////                                                    CraftTracker.LOGGER.debug("final count less than 1, removing: {}", id);
////                                                    return null;
////                                                }
////
////                                                processedItems.add(itemId);
////
////                                                CraftTracker.LOGGER.debug("adjusting count of intermediate item {} to: {}", itemId, finalCount);
////                                                return finalCount;
////                                            });
////                                        });
////                            }
////                            else {
////                            CraftTracker.LOGGER.debug("player DOES NOT have item in inventory: {}", item);
//
//                            this.intermediateProducts.compute(id, (itemId, quantity) -> {
//                                if(quantity == null) {
//                                    return needsToMake;
//                                }
//
//                                processedItems.add(itemId);
//
//                                return quantity + needsToMake;
//                            });
////                            }
//
//                            int subIndex = RecipeUtil.chooseLeastExpensiveOf(subRecipes);
//                            CraftTracker.LOGGER.debug("least expensive item index: {}", subIndex);
//                            this.computeRecipe(ctx, subRecipes.get(subIndex), recipeQuantity, processedItems);
//                        }
//                    });
//        }
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

    class ProcessingContext {
        Map<ResourceLocation, Integer> intermediateProducts = new HashMap<>();
        Map<ResourceLocation, Integer> rawMaterials = new HashMap<>();
        Map<ResourceLocation, Integer> fuel = new HashMap<>();
        Set<ResourceLocation> handledItems = new HashSet<>();
        int processingLevel;
    }

    class IngredientInfo {
        Ingredient ingredient;
        Recipe<?> recipe;
        int recipeIndex;
        int amount;
    }
}
