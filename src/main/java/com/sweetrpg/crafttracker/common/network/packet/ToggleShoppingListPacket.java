package com.sweetrpg.crafttracker.common.network.packet;

import com.sweetrpg.crafttracker.common.network.IPacket;
import com.sweetrpg.crafttracker.common.network.packet.data.ToggleShoppingListData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class ToggleShoppingListPacket implements IPacket<ToggleShoppingListData> {

    @Override
    public void encode(ToggleShoppingListData data, FriendlyByteBuf buf) {

//        buf.writeBoolean(data.display);
    }

    @Override
    public ToggleShoppingListData decode(FriendlyByteBuf buf) {
//        boolean display = buf.readBoolean();
        return new ToggleShoppingListData();
    }

    @Override
    public final void handle(ToggleShoppingListData data, Supplier<Context> ctx) {
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
