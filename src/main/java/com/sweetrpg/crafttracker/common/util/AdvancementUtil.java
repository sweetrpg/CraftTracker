package com.sweetrpg.crafttracker.common.util;

import com.sweetrpg.crafttracker.common.advancement.SimpleTrigger;
import com.sweetrpg.crafttracker.common.registry.ModAdvancements;
import com.sweetrpg.crafttracker.common.registry.ModTriggers;
import net.minecraft.server.level.ServerPlayer;

import static com.sweetrpg.crafttracker.common.advancement.SimpleTrigger.MAIN_CRITERION;

public class AdvancementUtil {

    public static void trigger(ModAdvancements.Key advancement, ServerPlayer player) {
        var ct = ModAdvancements.ENTRIES.get(advancement)
                .getCriteria().get(MAIN_CRITERION)
                .getTrigger();
        if(ModTriggers.ENTRIES.get(ct.getCriterion().getPath()) instanceof SimpleTrigger trigger) {
            trigger.trigger(player);
        }
    }
}
