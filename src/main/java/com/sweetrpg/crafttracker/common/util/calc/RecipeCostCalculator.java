package com.sweetrpg.crafttracker.common.util.calc;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.config.ConfigHandler;
import com.sweetrpg.crafttracker.common.util.DebugUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.ObjectUtils;

/**
 * Calculates the "cost" of a recipe.
 * <p/>
 * Computes the cost of a recipe from:
 * - the sum of its ingredients' costs
 * - whether the recipe is "vanilla"
 * - whether the recipe is "simple" (crafted vs. smelted, etc.)
 */
public class RecipeCostCalculator implements ICostCalculator {

    private final Recipe<?> recipe;

    /**
     * Default constructor.
     *
     * @param recipe The recipe to calculate
     */
    public RecipeCostCalculator(Recipe<?> recipe) {
        this.recipe = recipe;
    }

    /**
     * Perform the calculation.
     *
     * @return An integer value of the recipe's cost
     */
    @Override
    public int calculate() {
        CraftTracker.LOGGER.info("Calculating recipe cost: {}", DebugUtil.printRecipe(recipe));

        int cost = recipe.getIngredients().stream()
                .map(IngredientCostCalculator::new)
                .map(IngredientCostCalculator::calculate)
                .reduce(0, Integer::sum);
        CraftTracker.LOGGER.info("Summed cost of items: {}.", cost);

        // adjust the cost by the namespace's multiplier
        String recipeNamespace = ObjectUtils.defaultIfNull(recipe.getId(), new ResourceLocation("", "")).getNamespace();
        CraftTracker.LOGGER.debug("recipeNamespace: {}", recipeNamespace);
        ForgeConfigSpec.DoubleValue multiplier = ConfigHandler.COMMON.namespaceEntries.get(recipeNamespace);
        CraftTracker.LOGGER.debug("multiplier: {}", multiplier);

        if(multiplier != null) {
            int newCost = (int) (cost * multiplier.get());
            CraftTracker.LOGGER.info("Increasing cost of recipe {} in namespace {} by {}: from {} to {}.",
                    recipe.getId(), recipeNamespace, multiplier.get(),
                    cost, newCost);
            cost = newCost;
        }

        RecipeType<?> recipeType = recipe.getType();
        CraftTracker.LOGGER.debug("recipe type: {}", recipeType);

        ForgeConfigSpec.DoubleValue typeMultiplier = ConfigHandler.COMMON.recipeTypeEntries.get(recipeType.toString());
        if(typeMultiplier != null) {
            int newCost = (int) (cost * typeMultiplier.get());
            CraftTracker.LOGGER.info("Increasing cost of recipe type {} by {}: from {} to {}.",
                    recipeType, typeMultiplier.get(),
                    cost, newCost);
            cost = newCost;
        }

        CraftTracker.LOGGER.info("Returning cost: {}.", cost);
        return cost;
    }
}
