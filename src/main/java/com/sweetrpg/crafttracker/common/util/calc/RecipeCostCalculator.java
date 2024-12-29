package com.sweetrpg.crafttracker.common.util.calc;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.config.ConfigHandler;
import com.sweetrpg.crafttracker.common.util.DebugUtil;
import com.sweetrpg.crafttracker.common.util.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
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
    public double calculate() {
        CraftTracker.LOGGER.info("Calculating recipe cost: {}", DebugUtil.printRecipe(recipe));

        double cost = recipe.getIngredients().stream()
                .peek(i -> {
                    CraftTracker.LOGGER.debug("  -> ingredient: {}", DebugUtil.printIngredient(i));
                })
                .map(IngredientCostCalculator::new)
                .map(IngredientCostCalculator::calculate)
                .peek(c -> {
                    CraftTracker.LOGGER.debug("  -> ingredient cost: {}", c);
                })
                .reduce(0.0d, Double::sum);
        CraftTracker.LOGGER.info("Summed cost of items: {}.", cost);

        // adjust the cost by the namespace's multiplier
        var recipeNamespace = ObjectUtils.defaultIfNull(recipe.getId(), new ResourceLocation("", "")).getNamespace();
        CraftTracker.LOGGER.debug("recipeNamespace: {}", recipeNamespace);
        double multiplier = Util.getConfigValueOrDefault(ConfigHandler.COMMON.namespaceEntries.get(recipeNamespace), 1);
        CraftTracker.LOGGER.debug("multiplier: {}", multiplier);

        if(multiplier != 1) {
            double newCost = cost * multiplier;
            CraftTracker.LOGGER.info("Adjusting cost of recipe {} in namespace {} by {}: from {} to {}.",
                    recipe.getId(), recipeNamespace, multiplier,
                    cost, newCost);
            cost = newCost;
        }

        var recipeType = recipe.getType();
        CraftTracker.LOGGER.debug("recipe type: {}", recipeType);

        double typeMultiplier = Util.getConfigValueOrDefault(ConfigHandler.COMMON.recipeTypeEntries.get(recipeType.toString()), 1);
        CraftTracker.LOGGER.debug("typeMultiplier: {}", typeMultiplier);
        if(typeMultiplier != 1) {
            double newCost = cost * typeMultiplier;
            CraftTracker.LOGGER.info("Adjusting cost of recipe type {} by {}: from {} to {}.",
                    recipeType, typeMultiplier,
                    cost, newCost);
            cost = newCost;
        }

        CraftTracker.LOGGER.info("Returning cost: {}.", cost);
        return cost;
    }
}
