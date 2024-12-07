package com.sweetrpg.crafttracker.common.manager;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.model.CraftingQueueItem;
import com.sweetrpg.crafttracker.common.model.CraftingQueueProduct;
import com.sweetrpg.crafttracker.common.storage.CraftingQueueStorage;
import com.sweetrpg.crafttracker.common.util.DebugUtil;
import com.sweetrpg.crafttracker.common.util.InventoryUtil;
import com.sweetrpg.crafttracker.common.util.RecipeUtil;
import com.sweetrpg.crafttracker.common.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.registries.ForgeRegistries;
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

/**
 * Manages the crafting queue.
 * <p/>
 * This class is responsible for doing all the calculations for intermediate recipes and raw materials when
 * items are added or removed from the queue.
 */
public class CraftingQueueManager {

    public static CraftingQueueManager INSTANCE = new CraftingQueueManager();

    private static final int MAX_PROCESSING_LEVEL = 3;

    private Map<ResourceLocation, CraftingQueueProduct> endProducts = new HashMap<>();
    private Map<ResourceLocation, CraftingQueueItem> intermediateProducts = new HashMap<>();
    private Map<ResourceLocation, CraftingQueueItem> rawMaterials = new HashMap<>();
    private Map<ResourceLocation, CraftingQueueItem> fuel = new HashMap<>();

    /**
     * Default constructor.
     */
    public CraftingQueueManager() {
    }

    /**
     * Load the last persisted crafting queue from storage.
     *
     * @param player The player for whom to load the queue.
     */
    public void load(Player player) {
        CraftTracker.LOGGER.info("Loading crafting queue for {}", player);

        Path file = Util.getStoragePath().resolve("queue.nbt").toAbsolutePath();
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

    /**
     * Save the current crafting queue to persistent storage.
     *
     * @param player The player for whom to save the queue.
     */
    public void save(Player player) {
        CraftTracker.LOGGER.info("Saving crafting queue for {}", player);

        Path file = Util.getStoragePath().resolve("queue.nbt").toAbsolutePath();
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

    /**
     * Returns a copy of the end products in the queue.
     *
     * @return A {@link List} of {@link CraftingQueueProduct} representing the products in the queue
     */
    public List<CraftingQueueProduct> getEndProducts() {
        return endProducts.values()
                .stream()
                .toList();
    }

    /**
     * Returns a list of the intermediate recipes in the queue.
     *
     * @return A {@link List} of {@link CraftingQueueItem} representing the intermediates in the queue
     */
    public List<CraftingQueueItem> getIntermediates() {
        return intermediateProducts.values()
                .stream()
                .toList();
    }

    /**
     * Returns a list of the materials in the queue.
     *
     * @return A {@link List} of {@link CraftingQueueItem} representing the materials in the queue
     */
    public List<CraftingQueueItem> getRawMaterials() {
        return rawMaterials.values()
                .stream()
                .toList();
    }

    /**
     * Returns a list of the fuels in the queue.
     *
     * @return A {@link List} of {@link CraftingQueueItem} representing the fuels in the queue
     */
    public List<CraftingQueueItem> getFuel() {
        return fuel.values()
                .stream()
                .toList();
    }

    /**
     * Adds a product to the queue.
     * <p/>
     * This will trigger a recalculation of the entire queue.
     *
     * @param player The player whose queue is being adjusted
     * @param itemId The item to add to the queue
     * @param quantity The amount to add
     */
    public void addProduct(Player player, ResourceLocation itemId, int quantity) {
        CraftTracker.LOGGER.debug("CraftingQueueManager#addProduct: {}, quantity: {}", itemId, quantity);

        if(quantity < 1) return;

        var recipes = RecipeUtil.getRecipesFor(itemId);

        if(!recipes.isEmpty()) {
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
     * @param player The player whose queue is being adjusted
     * @param itemId The item to adjust
     * @param quantity The amount to adjust; positive values will increase the amount, negative values will reduce it.
     */
    public void adjustProduct(Player player, ResourceLocation itemId, int quantity) {
        CraftTracker.LOGGER.debug("CraftingQueueManager#adjustProduct: {}, quantity: {}", itemId, quantity);

        if(quantity < 0)
            removeProduct(player, itemId, -quantity);
        else if(quantity > 0)
            addProduct(player, itemId, quantity);
    }

    /**
     * Removes all of a single end product from the queue.
     *
     * @param player The player whose queue is being adjusted
     * @param itemId The item to remove from the queue
     */
    public void removeProduct(Player player, ResourceLocation itemId) {
        CraftTracker.LOGGER.debug("CraftingQueueManager#removeProduct: {}", itemId);

        this.endProducts.remove(itemId);

        computeAll();

        this.save(player);
    }

    /**
     * Removes a single end product, or a quantity of it, from the queue.
     *
     * @param player The player whose queue is being adjusted
     * @param itemId The item to remove from the queue
     * @param quantity The amount of the item to remove. If this value is the greater than or equal to the amount
     *                 currently in the queue, the item is removed entirely.
     */
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

    /**
     * Removes everything from the queue.
     */
    public void removeAll() {
        CraftTracker.LOGGER.debug("CraftingQueueManager#removeAll");

        this.endProducts.clear();
        this.intermediateProducts.clear();
        this.rawMaterials.clear();
        this.fuel.clear();
    }

    /**
     * Compute all the intermediate items, raw materials, and fuel needed to make the recipes.
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

    /**
     * Starts the computation of an end product's intermediates and materials.
     *
     * @param ctx The context to use for processing the queue
     * @param product The product to process
     */
    void computeProduct(ProcessingContext ctx, CraftingQueueProduct product) {
        CraftTracker.LOGGER.debug("CraftingQueueManager#computeProduct: {}", product);

        var index = Math.min(product.getIndex(), product.getRecipes().size());
        var recipe = product.getRecipes().get(index);

        var computedRecipe = this.computeRecipe(recipe, product.getIterations(), 0);
        CraftTracker.LOGGER.debug("#computeProduct: computedRecipe {}", computedRecipe);
        ctx.computedRecipes.add(computedRecipe);
    }

    /**
     * Merges the computed recipes in the context into a single list of intermediates and raw materials.
     *
     * @param ctx The context to process
     */
    void coalesceProducts(ProcessingContext ctx) {
        CraftTracker.LOGGER.debug("#coalesceProducts: {}", ctx);

        this.intermediateProducts.clear();
        this.rawMaterials.clear();
        this.fuel.clear();

        ctx.computedRecipes.forEach(r -> {
            CraftTracker.LOGGER.debug("coalescing recipe: {}", r);

            r.intermediateProducts.forEach((ik, iv) -> {
                this.intermediateProducts.compute(ik, (ik1, iv1) -> {
                    return ObjectUtils.defaultIfNull(iv1, new CraftingQueueItem(ik1, 0, false))
                            .increment(iv.amount)
                            .tag(iv.tag);
                });
            });
            r.rawMaterials.forEach((rk, rv) -> {
                this.rawMaterials.compute(rk, (rk1, rv1) -> {
                    return ObjectUtils.defaultIfNull(rv1, new CraftingQueueItem(rk1, 0, false))
                            .increment(rv.amount)
                            .tag(rv.tag);
                });
            });
            r.fuel.forEach((fk, fv) -> {
                this.fuel.compute(fk, (fk1, fv1) -> {
                    return ObjectUtils.defaultIfNull(fv1, new CraftingQueueItem(fk1, 0, false))
                            .increment(fv.amount)
                            .tag(fv.tag);
                });
            });
        });

        CraftTracker.LOGGER.debug("coalesce complete: {}", this);
    }

    /**
     * The workhorse of the crafting queue manager. This function looks at the recipe's requirements and determines
     * if the items involved require other crafted items to make this, or if they are simply materials to be
     * acquired.
     * <p/>
     * If the recipe's namespace or the result item's namespace differ from the ingredients, the function will exit
     * early with a `null` return value in order to prevent strange suggestions of intermediates and raw materials.
     *
     * @param recipe The recipe to compute
     * @param iterations The desired number of times the recipe is to be crafted by the player
     * @param depth The current depth of processing the queue. This is metadata about the processing context used to
     *              prevent potential loops in looking up required items.
     * @return A computed recipe, or `null` if certain criteria are not met or thresholds are crossed.
     */
    ComputedRecipe computeRecipe(Recipe<?> recipe, int iterations, int depth) {
        CraftTracker.LOGGER.debug("CraftingQueueManager#computeRecipe: {}", DebugUtil.printRecipe(recipe));

        var computedRecipe = new ComputedRecipe(recipe.getId());

        var ingredients = recipe.getIngredients();
        CraftTracker.LOGGER.debug("ingredients: {}", ingredients.stream().map(DebugUtil::printIngredient).toList());

        // if we're not at the root, and
        //   1. the ingredients are in a different namespace than the recipe, or
        //   2. the ingredients are in a different namespace than the result item
        var recipeNamespace = ObjectUtils.defaultIfNull(recipe.getId().getNamespace(), "");
        var itemNamespace = ObjectUtils.defaultIfNull(ForgeRegistries.ITEMS.getKey(recipe.getResultItem().getItem()).getNamespace(), "");
        if(depth > 0 &&
                (!RecipeUtil.areIngredientsSameNamespace(recipeNamespace, ingredients) ||
                        !RecipeUtil.areIngredientsSameNamespace(itemNamespace, ingredients))) {
            CraftTracker.LOGGER.debug("ingredients for sub-recipe are not in the same namespace as the recipe: {}",
                    DebugUtil.printRecipe(recipe));
            return null;
        }

        // tally ingredients
        Map<ResourceLocation, Tuple<Boolean, Integer>> ingredientTally = new HashMap<>();
        for(Ingredient ingredient : ingredients) {
            CraftTracker.LOGGER.debug("ingredient: {}", DebugUtil.printIngredient(ingredient));

            if(ingredient.isEmpty() || ingredient.getItems().length == 0) {
                continue;
            }

            CraftTracker.LOGGER.debug("ingredient class: {}", ingredient.getClass());
            CraftTracker.LOGGER.debug("ingredient.values: {}", (Object) ingredient.values);

            Boolean tag;
            if(ingredient.values.length > 0 && ingredient.values[0] instanceof Ingredient.TagValue) {
                // ingredient is a tag
                tag = true;
            }
            else {
                tag = false;
            }

            ItemStack chosenStack = RecipeUtil.chooseLeastExpensiveOf(ingredient.getItems());
            ingredientTally.compute(ForgeRegistries.ITEMS.getKey(chosenStack.getItem()), (ingredientId, tuple) -> {
//                return ObjectUtils.defaultIfNull(amount, 0) + 1;
                tuple = ObjectUtils.defaultIfNull(tuple, new Tuple<>(false, 0));
                tuple.setA(tag);
                tuple.setB(tuple.getB() + 1);
//                newTuple.setA(tag);
//                newTuple.setB();
                return tuple;
            });
        }

        // process ingredients
        ingredientTally.forEach((ingredientId, ingredientAmount) -> {
            CraftTracker.LOGGER.debug("ingredient: id {}, amount {}", ingredientId, ingredientAmount);

            Item item = ForgeRegistries.ITEMS.getValue(ingredientId);
            CraftTracker.LOGGER.debug("item: {}", DebugUtil.printItem(item));
            int amountRequired = ingredientAmount.getB(); // chosenStack.getCount();
            CraftTracker.LOGGER.debug("amountRequired: {}", amountRequired);
            boolean isTag = ingredientAmount.getA();
            CraftTracker.LOGGER.debug("isTag: {}", isTag);

            // check if player already has the item
            CraftTracker.LOGGER.debug("check if player already has {}", DebugUtil.printItem(item));
            var player = Minecraft.getInstance().player;
            var hasInInventory = InventoryUtil.getQuantityOf(player, ForgeRegistries.ITEMS.getKey(item));
            CraftTracker.LOGGER.debug("hasInInventory: {}", hasInInventory);
            var needsQty = (amountRequired * iterations) - hasInInventory;
            CraftTracker.LOGGER.debug("needsQty: {}", needsQty);

            if(needsQty < 1) {
                CraftTracker.LOGGER.debug("player already has enough of item {} ({} >= {})", ForgeRegistries.ITEMS.getKey(item), hasInInventory, amountRequired);
                return;
            }

            var id = ForgeRegistries.ITEMS.getKey(item);
            CraftTracker.LOGGER.debug("id: {}", id);
            var subRecipes = RecipeUtil.getRecipesFor(id);
            CraftTracker.LOGGER.debug("subRecipes: {}", subRecipes.stream().map(DebugUtil::printRecipe).toList());

            if(subRecipes.isEmpty() || depth >= MAX_PROCESSING_LEVEL) {
                CraftTracker.LOGGER.debug("subRecipes is empty; ingredient {} is a raw material", ingredientId);
                // no recipes for this ingredient, so it's a raw material
                computedRecipe.rawMaterials.compute(id,
                        (itemId, quantity) ->
                                ObjectUtils.defaultIfNull(quantity, new ComputedRecipeItem(itemId))
                                        .increase(amountRequired * iterations)
                                        .tag(isTag));
            }
            else {
                CraftTracker.LOGGER.debug("subRecipes has {} items; ingredient {} is an intermediate product", subRecipes.size(), ingredientId);

                var chosenSubRecipe = RecipeUtil.chooseLeastExpensiveOf(subRecipes);
                CraftTracker.LOGGER.debug("chosenSubRecipe: {}", DebugUtil.printRecipe(chosenSubRecipe));

                var computedSubRecipe = this.computeRecipe(chosenSubRecipe, amountRequired * iterations, depth + 1);
                CraftTracker.LOGGER.debug("computedSubRecipe: {}", computedSubRecipe);
                if(computedSubRecipe == null) {
                    CraftTracker.LOGGER.debug("computed sub-recipe for {} returned is null; treat as raw material", DebugUtil.printRecipe(chosenSubRecipe));
                    // if the sub-recipe comes back null, then treat the result item as a raw material
                    computedRecipe.rawMaterials.compute(id,
                            (itemId, quantity) ->
                                    ObjectUtils.defaultIfNull(quantity, new ComputedRecipeItem(itemId))
                                            .increase(amountRequired * iterations)
                                            .tag(isTag));
                    return;
                }

                computedRecipe.intermediateProducts.compute(id,
                        (itemId, quantity) ->
                                ObjectUtils.defaultIfNull(quantity, new ComputedRecipeItem(itemId))
                                        .increase(needsQty)
                                        .tag(isTag));

                // merge subrecipe items into this
                CraftTracker.LOGGER.debug("merging subrecipe contents: {} into this: {}", computedSubRecipe, computedRecipe);
                computedSubRecipe.intermediateProducts.forEach((itemId, cri) -> {
                    computedRecipe.intermediateProducts.compute(itemId, (k1, v1) -> {
                        return ObjectUtils.defaultIfNull(v1, new ComputedRecipeItem(k1))
                                .increase(cri.amount)
                                .tag(cri.tag);
                    });
                });
                computedSubRecipe.rawMaterials.forEach((itemId, cri) -> {
                    computedRecipe.rawMaterials.compute(itemId, (k1, v1) -> {
                        return ObjectUtils.defaultIfNull(v1, new ComputedRecipeItem(k1))
                                .increase(cri.amount)
                                .tag(cri.tag);
                    });
                });
                computedSubRecipe.fuel.forEach((itemId, cri) -> {
                    computedRecipe.fuel.compute(itemId, (k1, v1) -> {
                        return ObjectUtils.defaultIfNull(v1, new ComputedRecipeItem(k1))
                                .increase(cri.amount)
                                .tag(cri.tag);
                    });
                });

                CraftTracker.LOGGER.debug("this looks like: {}", this);
            }
        });

        return computedRecipe;
    }

    /**
     * A context object to keep track of state while processing the queue.
     */
    class ProcessingContext {
        Map<ResourceLocation, Integer> intermediateProducts = new HashMap<>();
        Map<ResourceLocation, Integer> rawMaterials = new HashMap<>();
        Map<ResourceLocation, Integer> fuel = new HashMap<>();
        Set<ResourceLocation> handledItems = new HashSet<>();
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

    /**
     * A wrapper object for items in the queue.
     */
    class ComputedRecipeItem {
        ResourceLocation itemId;
        int amount;
        boolean tag;

        /**
         * @param itemId
         */
        public ComputedRecipeItem(ResourceLocation itemId) {
            this.itemId = itemId;
        }

        /**
         * A convenience method for adjusting the amount, provided to allow chaining calls.
         *
         * @param amount The amount to increase the item
         * @return the same object, for chaining calls.
         */
        public ComputedRecipeItem increase(int amount) {
            this.amount += amount;
            return this;
        }

        /**
         * A convenience method for setting the tag value, provided to allow chaining calls.
         *
         * @param tag The tag value to set
         * @return The same object
         */
        public ComputedRecipeItem tag(boolean tag) {
            this.tag = tag;
            return this;
        }

        @Override
        public String toString() {
            return MessageFormat.format("""
                            ComputedRecipeItem[
                              itemId={0}
                              amount={1}
                              tag={2}
                            ]
                            """,
                    itemId, amount, tag);
        }
    }

    /**
     * An intermediate class that represents all the necessary items that make up a recipe. Used during computation
     * of the crafting queue's intermediates and materials.
     */
    class ComputedRecipe {
        ResourceLocation recipeId;
        Map<ResourceLocation, ComputedRecipeItem> intermediateProducts = new HashMap<>();
        Map<ResourceLocation, ComputedRecipeItem> rawMaterials = new HashMap<>();
        Map<ResourceLocation, ComputedRecipeItem> fuel = new HashMap<>();

        /**
         * Constructs the object with the ID of the recipe it represents.
         * @param recipeId The recipe ID
         */
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
