package com.sweetrpg.crafttracker.common.util;

import com.sweetrpg.crafttracker.common.advancement.SimpleTrigger;
import com.sweetrpg.crafttracker.common.registry.ModAdvancements;
import com.sweetrpg.crafttracker.common.registry.ModTriggers;
import net.minecraft.entity.player.ServerPlayerEntity;

import static com.sweetrpg.crafttracker.common.advancement.SimpleTrigger.MAIN_CRITERION;

public class AdvancementUtil {

    public static void trigger(ModAdvancements.Key advancement, ServerPlayerEntity player) {
        var ct = ModAdvancements.ENTRIES.get(advancement)
                .getCriteria().get(MAIN_CRITERION)
                .getTrigger();
        var trigger = ModTriggers.ENTRIES.get(ct.getCriterion().getPath());
        if(trigger instanceof SimpleTrigger) {
            ((SimpleTrigger)trigger).trigger(player);
        }

    }
}
