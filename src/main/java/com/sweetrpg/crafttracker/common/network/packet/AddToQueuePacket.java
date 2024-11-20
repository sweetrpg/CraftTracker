//package com.sweetrpg.crafttracker.common.network.packet;
//
//import com.sweetrpg.crafttracker.CraftTracker;
//import com.sweetrpg.crafttracker.common.addon.jei.CTPlugin;
//import com.sweetrpg.crafttracker.common.manager.CraftingQueueManager;
//import com.sweetrpg.crafttracker.common.network.IPacket;
//import com.sweetrpg.crafttracker.common.network.packet.data.AddToQueueData;
//import net.minecraft.network.FriendlyByteBuf;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.item.ItemStack;
//import net.minecraftforge.fml.LogicalSide;
//import net.minecraftforge.network.NetworkEvent.Context;
//
//import java.util.function.Supplier;
//
//public class AddToQueuePacket implements IPacket<AddToQueueData> {
//
//    @Override
//    public void encode(AddToQueueData data, FriendlyByteBuf buf) {
//        buf.writeResourceLocation(data.itemId);
//        buf.writeInt(data.quantity);
//    }
//
//    @Override
//    public AddToQueueData decode(FriendlyByteBuf buf) {
//        ResourceLocation itemId = buf.readResourceLocation();
//        int quantity = buf.readInt();
//        return new AddToQueueData(itemId, quantity);
//    }
//
//    @Override
//    public final void handle(AddToQueueData data, Supplier<Context> ctx) {
//        CraftTracker.LOGGER.debug("AddToQueuePacket#handle: {}", data);
//
//        ctx.get().enqueueWork(() -> {
//            LogicalSide side = ctx.get().getDirection().getReceptionSide();
//            if(side.isClient()) {
//
//            }
//            else if(side.isServer()) {
//                var player = ctx.get().getSender();
//                CraftingQueueManager.get(player, player.level).addProduct(player.level, data.itemId, data.quantity);
//            }
//        });
//
//        ctx.get().setPacketHandled(true);
//    }
//
//}
