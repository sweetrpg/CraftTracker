package com.sweetrpg.crafttracker.common.util.calc;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.config.ConfigHandler;
import com.sweetrpg.crafttracker.common.lib.Constants;
import com.sweetrpg.crafttracker.common.util.DebugUtil;
import com.sweetrpg.crafttracker.common.util.RecipeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.apache.commons.lang3.ObjectUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Calculates the cost of an ingredient
 * <p/>
 * Computes the cost of an ingredient by looking at the constituent items (i.e., if the ingredient is a tag, looking
 * at the cost of items that match the tag).
 * An item's cost can be set in the override list.
 * The ultimate cost of an ingredient will be the highest cost of the items matching its tag.
 */
public class IngredientCostCalculator implements ICostCalculator {

    private final Ingredient ingredient;

    /**
     * Default constructor.
     *
     * @param ingredient The ingredient to calculate
     */
    public IngredientCostCalculator(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    /**
     * Perform the calculation.
     *
     * @return An integer value of the ingredient's cost
     */
    @Override
    public int calculate() {
        CraftTracker.LOGGER.debug("RecipeUtil#getIngredientCost: {}", DebugUtil.printIngredient(ingredient));

        for(ItemStack stack : ingredient.getItems()) {
            // is the item in the override list?
            var itemId = stack.getItem().getRegistryName();
            var count = stack.getCount();

            if(ConfigHandler.COMMON.overrideEntries.containsKey(itemId)) {
                CraftTracker.LOGGER.debug("found item {} in override list", itemId);
                return ConfigHandler.COMMON.overrideEntries.get(itemId).get() * count;
            }

            // it's not, so check its tags
            int highestCost = 0;
            for(TagKey<Item> tag : stack.getTags().toList()) {
                var tagId = tag.location();
                if(ConfigHandler.COMMON.tagEntries.containsKey(tagId)) {
                    CraftTracker.LOGGER.debug("found item {} in tag list", tagId);
                    int cost = ConfigHandler.COMMON.tagEntries.get(tagId).get() * count;

                    // if the item's namespace is not 'minecraft:', increase the cost
                    if(!ObjectUtils.defaultIfNull(stack.getItem().getRegistryName().getNamespace(), "").equals("minecraft") &&
                            !ObjectUtils.defaultIfNull(tagId.getNamespace(), "").equals("minecraft")) {
                        var multiplier = ConfigHandler.CLIENT.NON_VANILLA_COST_MULTIPLIER.get();
                        CraftTracker.LOGGER.debug("RecipeUtil#calculateRecipeCost: increasing cost ({}) of non-vanilla item {} by {}",
                                cost, tagId, multiplier);
                        cost = (int) (cost * multiplier);
                    }

                    if(cost > highestCost) {
                        highestCost = cost;
                    }
                }
            }
            if(highestCost > 0) {
                return highestCost;
            }
        }

        CraftTracker.LOGGER.debug("#calculateIngredientCost: fell through to default cost");
        return 1;
    }
}
