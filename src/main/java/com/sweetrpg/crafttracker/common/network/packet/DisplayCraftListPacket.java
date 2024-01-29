package com.sweetrpg.crafttracker.common.network.packet;

import com.sweetrpg.crafttracker.common.network.IPacket;
import com.sweetrpg.crafttracker.common.network.packet.data.AddToQueueData;
import com.sweetrpg.crafttracker.common.network.packet.data.DisplayCraftListData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class DisplayCraftListPacket implements IPacket<DisplayCraftListData> {

    @Override
    public void encode(DisplayCraftListData data, FriendlyByteBuf buf) {
        buf.writeBoolean(data.display);
    }

    @Override
    public DisplayCraftListData decode(FriendlyByteBuf buf) {
        boolean display = buf.readBoolean();
        return new DisplayCraftListData(display);
    }

    @Override
    public final void handle(DisplayCraftListData data, Supplier<Context> ctx) {
        ctx.get().enqueueWork(() -> {
//            Entity target = ctx.get().getSender().level.getEntity(data.entityId);
//
//            if (!(target instanceof CatEntity)) {
//                return;
//            }
//
//            this.handleCat((CatEntity) target, data, ctx);
        });

        ctx.get().setPacketHandled(true);
    }

}
