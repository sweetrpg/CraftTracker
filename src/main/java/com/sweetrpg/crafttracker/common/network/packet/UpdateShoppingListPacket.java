package com.sweetrpg.crafttracker.common.network.packet;

import com.sweetrpg.crafttracker.common.network.IPacket;
import com.sweetrpg.crafttracker.common.network.packet.data.UpdateCraftQueueData;
import com.sweetrpg.crafttracker.common.network.packet.data.UpdateShoppingListData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateShoppingListPacket implements IPacket<UpdateShoppingListData> {

    @Override
    public UpdateShoppingListData decode(FriendlyByteBuf buf) {
        return new UpdateShoppingListData();
    }


    @Override
    public void encode(UpdateShoppingListData data, FriendlyByteBuf buf) {

    }

    @Override
    public void handle(UpdateShoppingListData data, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide() == LogicalSide.SERVER) {
                ServerPlayer player = ctx.get().getSender();
//                List<CatEntity> cats = player.level.getEntitiesOfClass(CatEntity.class, player.getBoundingBox().inflate(12D, 12D, 12D),
//                        (cat) -> cat.canInteract(player) && PackCatTalent.hasInventory(cat)
//                );
//                if (!cats.isEmpty()) {
//                    Screens.openCatInventoriesScreen(player, cats);
//                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
