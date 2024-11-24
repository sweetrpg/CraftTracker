package com.sweetrpg.crafttracker.common.network.packet.data;

public class QueueCommandData {

    public enum QueueCommand {
        RECALCULATE,
    }

    public QueueCommand command;

    public QueueCommandData(QueueCommand command) {
        this.command = command;
    }
}
