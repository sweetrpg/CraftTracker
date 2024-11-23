package com.sweetrpg.crafttracker.common.network.packet.data;

import net.minecraft.resources.ResourceLocation;

public class QueueCommandData {

    public enum QueueCommand {
        RECALCULATE,
    }

    public QueueCommand command;

    public QueueCommandData(QueueCommand command) {
        this.command = command;
    }
}
