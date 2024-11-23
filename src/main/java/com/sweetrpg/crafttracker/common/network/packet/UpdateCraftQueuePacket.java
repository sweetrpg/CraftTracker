//package com.sweetrpg.crafttracker.common.network.packet;
//
//import com.sweetrpg.crafttracker.CraftTracker;
//import com.sweetrpg.crafttracker.common.Screens;
//import com.sweetrpg.crafttracker.common.manager.CraftingQueueManager;
//import com.sweetrpg.crafttracker.common.network.IPacket;
//import com.sweetrpg.crafttracker.common.network.packet.data.UpdateCraftQueueData;
//import net.minecraft.network.FriendlyByteBuf;
//import net.minecraft.server.level.ServerPlayer;
//import net.minecraftforge.fml.LogicalSide;
//import net.minecraftforge.network.NetworkEvent;
//
//import java.util.List;
//import java.util.function.Supplier;
//
//public class UpdateCraftQueuePacket implements IPacket<UpdateCraftQueueData> {
//
//    @Override
//    public UpdateCraftQueueData decode(FriendlyByteBuf buf) {
//        List<CraftingQueueManager.ProductItem> items = buf.readList((lb) -> {
//            var itemId = lb.readResourceLocation();
//            var quantity = lb.readInt();
//            var categories = lb.readList(FriendlyByteBuf::readResourceLocation);
//
//            return new CraftingQueueManager.ProductItem(itemId, quantity, categories);
//        });
//
//        return new UpdateCraftQueueData(items);
//    }
//
//    @Override
//    public void encode(UpdateCraftQueueData data, FriendlyByteBuf buf) {
//        buf.writeCollection(data.getEndProducts(),
//                (pb, item) -> {
//                    pb.writeResourceLocation(item.getItemId());
//                    pb.writeInt(item.getQuantity());
//                    pb.writeCollection(item.getCategories(),
//                            (cb, category) -> pb.writeResourceLocation(category));
//                });
//    }
//
//    @Override
//    public void handle(UpdateCraftQueueData data, Supplier<NetworkEvent.Context> ctx) {
//        CraftTracker.LOGGER.debug("UpdateCraftQueuePacket#handle: {}, ctx: {}", data, ctx);
//
//        ctx.get().enqueueWork(() -> {
//            if(ctx.get().getDirection().getReceptionSide() == LogicalSide.SERVER) {
//                ServerPlayer player = ctx.get().getSender();
//                Screens.updateCraftQueue(player);
//            }
//        });
//
//        ctx.get().setPacketHandled(true);
//    }
//}
