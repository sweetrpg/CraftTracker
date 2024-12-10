package com.sweetrpg.crafttracker.common.util.calc;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.config.ConfigHandler;
import com.sweetrpg.crafttracker.common.util.DebugUtil;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;

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
        CraftTracker.LOGGER.debug("RecipeUtil#calculateRecipeCost: {}", DebugUtil.printRecipe(recipe));

        int cost = recipe.getIngredients().stream()
                .map(IngredientCostCalculator::new)
                .map(IngredientCostCalculator::calculate)
                .reduce(0, Integer::sum);

        // if the item's namespace is not 'minecraft:', increase the cost
        if(!recipe.getId().getNamespace().equals("minecraft")) {
            var multiplier = ConfigHandler.CLIENT.NON_VANILLA_COST_MULTIPLIER.get();
            CraftTracker.LOGGER.debug("RecipeUtil#calculateRecipeCost: increasing cost ({}) of non-vanilla recipe {} by {}",
                    cost, DebugUtil.printRecipe(recipe), multiplier);
            cost = (int) (cost * multiplier);
        }

        CraftTracker.LOGGER.debug("recipe type: {}", recipe.getType());

        if(!(recipe instanceof CraftingRecipe)) {
            var multiplier = ConfigHandler.CLIENT.NON_CRAFTING_COST_MULTIPLIER.get();
            CraftTracker.LOGGER.debug("RecipeUtil#calculateRecipeCost: increasing cost ({}) of non-crafting table recipe {} by {}",
                    cost, DebugUtil.printRecipe(recipe), multiplier);
            cost = (int) (cost * multiplier);
        }

        CraftTracker.LOGGER.debug("returning cost: {}", cost);
        return cost;
    }
}
