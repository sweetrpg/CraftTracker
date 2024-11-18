package com.sweetrpg.crafttracker.common.network.packet;

import com.sweetrpg.crafttracker.common.Screens;
import com.sweetrpg.crafttracker.common.network.IPacket;
import com.sweetrpg.crafttracker.common.network.packet.data.UpdateCraftQueueData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateCraftQueuePacket implements IPacket<UpdateCraftQueueData> {

    @Override
    public UpdateCraftQueueData decode(FriendlyByteBuf buf) {
        return new UpdateCraftQueueData();
    }


    @Override
    public void encode(UpdateCraftQueueData data, FriendlyByteBuf buf) {

    }

    @Override
    public void handle(UpdateCraftQueueData data, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide() == LogicalSide.SERVER) {
                ServerPlayer player = ctx.get().getSender();
                Screens.updateCraftQueue(player);
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
