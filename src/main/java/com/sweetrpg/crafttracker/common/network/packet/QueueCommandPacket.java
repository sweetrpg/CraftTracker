package com.sweetrpg.crafttracker.common.network.packet;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.manager.CraftingQueueManager;
import com.sweetrpg.crafttracker.common.network.IPacket;
import com.sweetrpg.crafttracker.common.network.packet.data.QueueCommandData;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class QueueCommandPacket implements IPacket<QueueCommandData> {

    @Override
    public void encode(QueueCommandData data, FriendlyByteBuf buf) {
        buf.writeEnum(data.command);
    }

    @Override
    public QueueCommandData decode(FriendlyByteBuf buf) {
        var command = buf.readEnum(QueueCommandData.QueueCommand.class);
        return new QueueCommandData(command);
    }

    @Override
    public final void handle(QueueCommandData data, Supplier<NetworkEvent.Context> ctx) {
        CraftTracker.LOGGER.debug("AddToQueuePacket#handle: {}", data);

        ctx.get().enqueueWork(() -> {
            switch(data.command) {
                case RECALCULATE:
                    LogicalSide side = ctx.get().getDirection().getReceptionSide();
                    if(side.isClient()) {
                        CraftingQueueManager.INSTANCE.computeAll();
                    }
                    break;
            }
        });

        ctx.get().setPacketHandled(true);
    }

}
