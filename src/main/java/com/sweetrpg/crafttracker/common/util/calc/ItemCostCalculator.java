package com.sweetrpg.crafttracker.common.util.calc;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.config.ConfigHandler;
import com.sweetrpg.crafttracker.common.util.DebugUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
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
        for(ResourceLocation tagId : stack.getItem().getTags().stream().toList()) {
//            var tagId = tag.location();
            CraftTracker.LOGGER.debug("looking at tagId: {}", tagId);

            if(ConfigHandler.COMMON.tagEntries.containsKey(tagId)) {
                CraftTracker.LOGGER.debug("found item {} in tag list", tagId);

                int cost = ConfigHandler.COMMON.tagEntries.get(tagId).get() * count;
                CraftTracker.LOGGER.debug("cost of tag {} is {}", tagId, cost);

                var tagNamespace = ObjectUtils.defaultIfNull(stack.getItem().getRegistryName(), new ResourceLocation("", "")).getNamespace();
                CraftTracker.LOGGER.debug("tagNamespace: {}", tagNamespace);
                var multiplier = ConfigHandler.COMMON.namespaceEntries.get(tagNamespace);
                CraftTracker.LOGGER.debug("multiplier: {}", multiplier);

                if(multiplier != null) {
                    var newCost = (int) (cost * multiplier.get());
                    CraftTracker.LOGGER.debug("#calculate: increasing cost of tag {} in namespace {} by {}: from {} to {}",
                            tagId, tagNamespace, multiplier.get(),
                            cost, newCost);
                    cost = newCost;
                }

                if(cost > highestCost) {
                    CraftTracker.LOGGER.trace("replacing highestCost with new value: was {}, is {}", highestCost, cost);
                    highestCost = (int) cost;
                }
            }
        }
        if(highestCost > 0) {
            CraftTracker.LOGGER.debug("returning highest cost: {}", highestCost);
            return highestCost;
        }

        CraftTracker.LOGGER.debug("#calculate: fell through to rarity");
        var rarity = stack.getItem().getRarity(stack);
        return Math.max(rarity.ordinal() * count, count);
    }
}
