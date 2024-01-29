package com.sweetrpg.crafttracker.common.network.packet;

import com.sweetrpg.crafttracker.common.network.IPacket;
import com.sweetrpg.crafttracker.common.network.packet.data.AddToQueueData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class AddToQueuePacket implements IPacket<AddToQueueData> {

    @Override
    public void encode(AddToQueueData data, FriendlyByteBuf buf) {
        buf.writeUtf(data.itemId);
    }

    @Override
    public AddToQueueData decode(FriendlyByteBuf buf) {
        String itemId = buf.readUtf();
        return new AddToQueueData(itemId);
    }

    @Override
    public final void handle(AddToQueueData data, Supplier<Context> ctx) {
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

//    public abstract void handleCat(CatEntity catIn, T data, Supplier<Context> ctx);

}
