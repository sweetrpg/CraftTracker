package com.sweetrpg.crafttracker.common.registry;

import com.sweetrpg.crafttracker.common.util.Util;
import com.sweetrpg.crafttracker.data.DisplayInfoBuilder;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.sweetrpg.crafttracker.common.advancement.SimpleTrigger.MAIN_CRITERION;

public class ModAdvancements {
    public static final Map<Key, Advancement> ENTRIES = new HashMap<>();
    public static final Advancement START = null;
    public static final Advancement ROOT = create("root", Key.ROOT, () -> Advancement.Builder.advancement()
            .display(DisplayInfoBuilder.create()
                    .icon(Items.PAPER)
                    .frame(FrameType.TASK)
                    .translate("crafttracker.main.root")
                    .background("stone.png")
                    .build())
            .addCriterion(MAIN_CRITERION, PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.location().build()))
            .requirements(RequirementsStrategy.OR));
    public static final Advancement QUEUE_ITEM = create("queue_item", Key.QUEUE_ITEM, () -> Advancement.Builder.advancement()
            .display(DisplayInfoBuilder.create()
                    .icon(Items.PAPER)
                    .frame(FrameType.TASK)
                    .translate("crafttracker.main.queue_item")
                    .background("stone.png")
                    .build())
            .addCriterion(MAIN_CRITERION, ModTriggers.addSimple("queue_item").instance())
            .requirements(RequirementsStrategy.OR));
    public static final Advancement CRAFT_ITEM = create("craft_item", Key.CRAFT_ITEM, () -> Advancement.Builder.advancement()
            .display(DisplayInfoBuilder.create()
                    .icon(Items.CRAFTING_TABLE)
                    .frame(FrameType.TASK)
                    .translate("crafttracker.main.craft_item")
                    .background("stone.png")
                    .build())
            .addCriterion(MAIN_CRITERION, ModTriggers.addSimple("craft_item").instance())
            .requirements(RequirementsStrategy.OR));
    public static final Advancement POPULATE_LIST = create("populate_list", Key.POPULATE_LIST, () -> Advancement.Builder.advancement()
            .display(DisplayInfoBuilder.create()
                    .icon(Items.PAPER)
                    .frame(FrameType.TASK)
                    .translate("crafttracker.main.populate_list")
                    .background("stone.png")
                    .build())
            .addCriterion(MAIN_CRITERION, ModTriggers.addSimple("populate_list").instance())
            .requirements(RequirementsStrategy.OR));
    public static final Advancement ACQUIRE_ITEM = create("acquire_item", Key.ACQUIRE_ITEM, () -> Advancement.Builder.advancement()
            .display(DisplayInfoBuilder.create()
                    .icon(Items.CRAFTING_TABLE)
                    .frame(FrameType.TASK)
                    .translate("crafttracker.main.acquire_item")
                    .background("stone.png")
                    .build())
            .addCriterion(MAIN_CRITERION, ModTriggers.addSimple("acquire_item").instance())
            .requirements(RequirementsStrategy.OR));
    public static final Advancement CLEAR_QUEUE = create("clear_queue", Key.CLEAR_QUEUE, () -> Advancement.Builder.advancement()
            .display(DisplayInfoBuilder.create()
                    .icon(Items.CRAFTING_TABLE)
                    .frame(FrameType.TASK)
                    .translate("crafttracker.main.clear_queue")
                    .background("stone.png")
                    .build())
            .addCriterion(MAIN_CRITERION, ModTriggers.addSimple("clear_queue").instance())
            .requirements(RequirementsStrategy.OR));

    public static <T extends Advancement.Builder> Advancement create(final String name, Key key, Supplier<T> sup) {
        var adv = sup.get().build(Util.getResource(name));
        ModAdvancements.ENTRIES.put(key, adv);
        return adv;
    }

    public static void register() {
    }

    public enum Key {
        ROOT,
        QUEUE_ITEM,
        CRAFT_ITEM,
        POPULATE_LIST,
        ACQUIRE_ITEM,
        CLEAR_QUEUE,
    }

}
