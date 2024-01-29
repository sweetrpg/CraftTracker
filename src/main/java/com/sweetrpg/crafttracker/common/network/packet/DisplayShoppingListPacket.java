package com.sweetrpg.crafttracker.common.network.packet;

import com.sweetrpg.crafttracker.common.network.IPacket;
import com.sweetrpg.crafttracker.common.network.packet.data.DisplayCraftListData;
import com.sweetrpg.crafttracker.common.network.packet.data.DisplayShoppingListData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class DisplayShoppingListPacket implements IPacket<DisplayShoppingListData> {

    @Override
    public void encode(DisplayShoppingListData data, FriendlyByteBuf buf) {
        buf.writeBoolean(data.display);
    }

    @Override
    public DisplayShoppingListData decode(FriendlyByteBuf buf) {
        boolean display = buf.readBoolean();
        return new DisplayShoppingListData(display);
    }

    @Override
    public final void handle(DisplayShoppingListData data, Supplier<Context> ctx) {
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
