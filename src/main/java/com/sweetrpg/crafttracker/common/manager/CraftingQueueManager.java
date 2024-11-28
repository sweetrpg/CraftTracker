package com.sweetrpg.crafttracker.common.manager;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.model.CraftingQueueProduct;
import com.sweetrpg.crafttracker.common.storage.CraftingQueueStorage;
import com.sweetrpg.crafttracker.common.util.RecipeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

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

//            CraftingQueueStorage.get(level).putData(itemId, quantity);

            computeAll();

//            PacketHandler.sendToPlayer(this.player, new UpdateCraftQueueData(this.getEndProducts()));
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

        this.intermediateProducts.clear();
        this.rawMaterials.clear();
        this.fuel.clear();

        this.endProducts.forEach((k, v) -> {
            this.computeProduct(v);
        });
    }

    public void computeProduct(CraftingQueueProduct product) {
        CraftTracker.LOGGER.debug("CraftingQueueManager#computeProduct: {}", product);

        var index = Math.min(product.getIndex(), product.getRecipes().size());
        var recipe = product.getRecipes().get(index);

        this.computeRecipe(recipe, product.getQuantity());
    }

    public void computeRecipe(Recipe recipe, int recipeQuantity) {
        CraftTracker.LOGGER.debug("CraftingQueueManager#computeRecipe: {}", recipe);

        var ingredients = recipe.getIngredients();
        CraftTracker.LOGGER.debug("ingredients: {}", ingredients);

        if(RecipeUtil.areIngredientsSame(ingredients)) {
            CraftTracker.LOGGER.debug("ingredients are the same: {}", ingredients);
            var id = recipe.getResultItem().getItem().getRegistryName();
            CraftTracker.LOGGER.debug("id: {}", id);
            if(this.intermediateProducts.containsKey(id)) {
                CraftTracker.LOGGER.debug("intermediates has this ingredient already: {}", id);
                var quantity = this.intermediateProducts.remove(id);
                var item = ForgeRegistries.ITEMS.getValue(id);
                this.intermediateProducts.remove(id);
                this.updateRawMaterials(id, new ItemStack(item, quantity), recipeQuantity);

                return;
            }
        }

        ingredients.stream()
                .filter((i) -> i instanceof Ingredient)
                .map((i) -> Ingredient.class.cast(i))
                .forEach((i) -> {
                    CraftTracker.LOGGER.debug("i: {}", i);

                    if(i instanceof Ingredient ingredient) {
                        CraftTracker.LOGGER.debug("ingredient: {}", ingredient);

                        Arrays.stream(ingredient.getItems())
                                .findFirst()
                                .ifPresent(item -> {
                                    CraftTracker.LOGGER.debug("item: {}", item);
                                    var id = item.getItem().getRegistryName();
                                    CraftTracker.LOGGER.debug("id: {}", id);
                                    var subRecipes = RecipeUtil.getRecipesFor(id);
                                    CraftTracker.LOGGER.debug("subRecipes: {}", subRecipes);

                                    if(subRecipes.isEmpty()) {
                                        CraftTracker.LOGGER.debug("subRecipes is empty; raw material");
                                        // no recipes for this ingredient, so it's a raw material
                                        this.updateRawMaterials(id, item, recipeQuantity);
                                    }
                                    else {
                                        CraftTracker.LOGGER.debug("subRecipes has {} items; intermediate", subRecipes.size());

                                        // check if player already has the item
                                        var inventory = Minecraft.getInstance().player.getInventory();
                                        if(inventory.contains(item)) {
                                            inventory.items.stream()
                                                    .filter(inv -> inv.getItem().getRegistryName().equals(id))
                                                    .map(inv -> inv.getCount())
                                                    .findFirst()
                                                    .ifPresent(count -> {
                                                        this.intermediateProducts.compute(id, (itemId, quantity) -> {
                                                            Integer finalCount = (quantity == null ? 0 : quantity) + item.getCount() - count;
                                                            if(finalCount < 1) {
                                                                return null;
                                                            }

                                                            return finalCount;
                                                        });
                                                    });
                                        }
                                        else {
                                            this.intermediateProducts.compute(id, (itemId, quantity) -> {
                                                if(quantity == null) {
                                                    return item.getCount();
                                                }

                                                return quantity + item.getCount();
                                            });
                                        }

                                        this.computeRecipe(subRecipes.get(0), recipeQuantity);
                                    }
                                });
                    }
                });
    }

    private void updateRawMaterials(ResourceLocation id, ItemStack item, int recipeQuantity) {
        this.rawMaterials.compute(id, (itemId, quantity) -> {
            if(quantity == null) {
                return item.getCount() * recipeQuantity;
            }

            return quantity + (item.getCount() * recipeQuantity);
        });
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
}
