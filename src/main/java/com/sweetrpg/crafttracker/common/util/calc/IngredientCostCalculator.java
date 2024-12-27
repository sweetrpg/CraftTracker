package com.sweetrpg.crafttracker.common.util.calc;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.config.ConfigHandler;
import com.sweetrpg.crafttracker.common.util.DebugUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.ObjectUtils;

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
        CraftTracker.LOGGER.info("Calculating ingredient cost: {}", DebugUtil.printIngredient(ingredient));

        for(ItemStack stack : ingredient.getItems()) {
            CraftTracker.LOGGER.debug("stack: {}", DebugUtil.printItemStack(stack));

            // is the item in the override list?
            ResourceLocation itemId = stack.getItem().getRegistryName();
            int count = stack.getCount();

            if(ConfigHandler.COMMON.overrideEntries.containsKey(itemId)) {
                CraftTracker.LOGGER.info("Found item {} in override list.", itemId);
                return ConfigHandler.COMMON.overrideEntries.get(itemId).get() * count;
            }

            // it's not, so check its tags
            int highestCost = 0;
            for(TagKey<Item> tag : stack.getTags().toList()) {
                ResourceLocation tagId = tag.location();
                CraftTracker.LOGGER.debug("looking at tagId: {}", tagId);

                if(ConfigHandler.COMMON.tagEntries.containsKey(tagId.toString())) {
                    CraftTracker.LOGGER.debug("found tag {} in tag list", tagId);

                    int cost = ConfigHandler.COMMON.tagEntries.get(tagId.toString()).get() * count;
                    CraftTracker.LOGGER.debug("cost of tag {} is {}", tagId, cost);

                    // adjust the cost by the namespace's multiplier
                    String tagNamespace = ObjectUtils.defaultIfNull(stack.getItem().getRegistryName(), new ResourceLocation("", "")).getNamespace();
                    CraftTracker.LOGGER.debug("tagNamespace: {}", tagNamespace);
                    ForgeConfigSpec.DoubleValue multiplier = ConfigHandler.COMMON.namespaceEntries.get(tagNamespace);
                    CraftTracker.LOGGER.debug("multiplier: {}", multiplier);

                    if(multiplier != null) {
                        int newCost = (int) (cost * multiplier.get());
                        CraftTracker.LOGGER.info("Increasing cost of tag {} in namespace {} by {}: from {} to {}.",
                                tagId, tagNamespace, multiplier.get(),
                                cost, newCost);
                        cost = newCost;
                    }

                    if(cost > highestCost) {
                        CraftTracker.LOGGER.trace("replacing highestCost with new value: was {}, is {}", highestCost, cost);
                        highestCost = cost;
                    }
                }
            }

            if(highestCost > 0) {
                CraftTracker.LOGGER.info("Returning highest cost: {}", highestCost);
                return highestCost;
            }
        }

        CraftTracker.LOGGER.info("Fell through to default cost.");
        return 1;
    }
}
