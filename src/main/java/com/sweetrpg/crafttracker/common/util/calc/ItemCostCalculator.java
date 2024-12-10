package com.sweetrpg.crafttracker.common.util.calc;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.config.ConfigHandler;
import com.sweetrpg.crafttracker.common.util.DebugUtil;
import com.sweetrpg.crafttracker.common.util.RecipeUtil;
import com.sweetrpg.crafttracker.common.util.Util;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.ObjectUtils;

/**
 * Calculates the cost of an item stack
 * <p/>
 * The logic here is the same as for {@link IngredientCostCalculator}, except that it applies to an
 * {@link ItemStack}. See that method's documentation for details, with the caveat that this method will fall
 * back on an item's rarity if all other calculations are insufficient.
 */
public class ItemCostCalculator implements ICostCalculator {

    private final ItemStack stack;

    /**
     * Default constructor.
     *
     * @param stack The stack to calculate
     */
    public ItemCostCalculator(ItemStack stack) {
        this.stack = stack;
    }

    /**
     * Perform the calculation.
     *
     * @return An integer value of the item stack's cost
     */
    @Override
    public int calculate() {
        CraftTracker.LOGGER.debug("#calculateItemCost: {}", DebugUtil.printItemStack(stack));

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
                if(!ObjectUtils.defaultIfNull(stack.getItem().getRegistryName(), Util.getResource("", "")).getNamespace().equals("minecraft") &&
                        !ObjectUtils.defaultIfNull(tagId.getNamespace(), "").equals("minecraft")) {
                    var multiplier = ConfigHandler.CLIENT.NON_VANILLA_COST_MULTIPLIER.get();
                    CraftTracker.LOGGER.debug("RecipeUtil#calculateItemCost: increasing cost ({}) of non-vanilla item {} by {}",
                            cost, tagId, multiplier);
                    cost = (int) (cost * multiplier);
                }

                if(cost > highestCost) {
                    highestCost = (int) cost;
                }
            }
        }
        if(highestCost > 0) {
            return highestCost;
        }

        CraftTracker.LOGGER.debug("#calculateIngredientCost: fell through to rarity");
        var rarity = stack.getItem().getRarity(stack);
        return Math.max(rarity.ordinal() * count, count);
    }
}
