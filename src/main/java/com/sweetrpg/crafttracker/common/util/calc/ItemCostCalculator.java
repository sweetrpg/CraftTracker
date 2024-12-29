package com.sweetrpg.crafttracker.common.util.calc;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.config.ConfigHandler;
import com.sweetrpg.crafttracker.common.util.DebugUtil;
import com.sweetrpg.crafttracker.common.util.RecipeUtil;
import com.sweetrpg.crafttracker.common.util.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.ObjectUtils;

import java.util.List;

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
    public double calculate() {
        CraftTracker.LOGGER.debug("#calculateItemCost: {}", DebugUtil.printItemStack(stack));

        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        int count = stack.getCount();

        if(ConfigHandler.COMMON.overrideEntries.containsKey(itemId.toString())) {
            CraftTracker.LOGGER.debug("found item {} in override list", itemId);
            return ConfigHandler.COMMON.overrideEntries.get(itemId.toString()).get() * count;
        }

        // it's not, so check its tags
        int highestCost = 0;
        for(TagKey<Item> tag : stack.getTags().toList()) {
            ResourceLocation tagId = tag.location();
            String tagPath = tagId.getPath();
            CraftTracker.LOGGER.debug("looking at tagId: {}", tagId);

            if(ConfigHandler.COMMON.tagEntries.containsKey(tagPath)) {
                CraftTracker.LOGGER.debug("found item {} in tag list", tagId);

                double cost = ConfigHandler.COMMON.tagEntries.get(tagPath).get() * count;
                CraftTracker.LOGGER.debug("cost of tag {} is {}", tagId, cost);

                String stackTagNamespace = ObjectUtils.defaultIfNull(ForgeRegistries.ITEMS.getKey(stack.getItem()), new ResourceLocation("", "")).getNamespace();
                CraftTracker.LOGGER.debug("tagNamespace: {}", stackTagNamespace);
                double multiplier = Util.getConfigValueOrDefault(ConfigHandler.COMMON.namespaceEntries.get(stackTagNamespace), 1);
                CraftTracker.LOGGER.debug("multiplier: {}", multiplier);

                if(multiplier != 1) {
                    double newCost = cost * multiplier;
                    CraftTracker.LOGGER.info("Adjusting cost of tag {} in namespace {} by {}: from {} to {}.",
                            tagId, stackTagNamespace, multiplier,
                            cost, newCost);
                    cost = newCost;
                }

                if(cost > highestCost) {
                    CraftTracker.LOGGER.trace("replacing highestCost with new value: was {}, is {}", highestCost, cost);
                    highestCost = (int) cost;
                }
            }
        }

        // if the stack item has recipes, then double the cost
        List<? extends Recipe<?>> recipes = RecipeUtil.getRecipesFor(itemId);
        if(!recipes.isEmpty()) {
            CraftTracker.LOGGER.info("Doubling cost of item {} because it is crafted.", DebugUtil.printItemStack(this.stack));
            highestCost *= 2;
        }

        if(highestCost > 0) {
            CraftTracker.LOGGER.info("Returning highest cost: {}", highestCost);
            return highestCost;
        }

        CraftTracker.LOGGER.info("Fell through to rarity.");
        var rarity = stack.getItem().getRarity(stack);
        return Math.max(rarity.ordinal() * count, count);
    }
}
