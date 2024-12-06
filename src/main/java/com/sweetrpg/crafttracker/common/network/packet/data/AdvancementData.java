package com.sweetrpg.crafttracker.common.network.packet.data;

import com.sweetrpg.crafttracker.common.registry.ModAdvancements;

public class AdvancementData {

    public ModAdvancements.Key advancement;

    public AdvancementData(ModAdvancements.Key advancement) {
        this.advancement = advancement;
    }
}
