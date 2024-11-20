//package com.sweetrpg.crafttracker.common.network.packet;
//
//import com.sweetrpg.crafttracker.common.network.IPacket;
//import com.sweetrpg.crafttracker.common.network.packet.data.ToggleCraftListData;
//import net.minecraft.network.FriendlyByteBuf;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.fml.DistExecutor;
//import net.minecraftforge.network.NetworkEvent;
//import net.minecraftforge.network.NetworkEvent.Context;
//
//import java.util.function.Supplier;
//
//public class ToggleCraftListPacket implements IPacket<ToggleCraftListData> {
//
//    @Override
//    public void encode(ToggleCraftListData data, FriendlyByteBuf buf) {
//
////        buf.writeBoolean(data.display);
//    }
//
//    @Override
//    public ToggleCraftListData decode(FriendlyByteBuf buf) {
////        boolean display = buf.readBoolean();
//        return new ToggleCraftListData();
//    }
//
//    @Override
//    public final void handle(ToggleCraftListData data, Supplier<Context> ctx) {
//        ctx.get().enqueueWork(() -> {
////            Entity target = ctx.get().getSender().level.getEntity(data.entityId);
////
////            if (!(target instanceof CatEntity)) {
////                return;
////            }
////
////            this.handleCat((CatEntity) target, data, ctx);
//            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handleClientPacket(data, ctx));
//        });
//
//        ctx.get().setPacketHandled(true);
//    }
//
//    static void handleClientPacket(ToggleCraftListData data, Supplier<NetworkEvent.Context> ctx) {
//
//    }
//
//}
