package com.sweetrpg.crafttracker.common.network;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.network.packet.AdvancementPacket;
import com.sweetrpg.crafttracker.common.network.packet.QueueCommandPacket;
import com.sweetrpg.crafttracker.common.network.packet.data.AdvancementData;
import com.sweetrpg.crafttracker.common.network.packet.data.QueueCommandData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

public final class PacketHandler {

    private static int disc = 0;

    public static void init() {
        registerPacket(new QueueCommandPacket(), QueueCommandData.class);
        registerPacket(new AdvancementPacket(), AdvancementData.class);
    }

    public static <MSG> void sendToServer(MSG message) {
        CraftTracker.HANDLER.send(message, PacketDistributor.SERVER.noArg());
    }

    public static <MSG> void sendToPlayer(ServerPlayer player, MSG message) {
        CraftTracker.HANDLER.send(message, PacketDistributor.PLAYER.with(player));
    }

    public static <D> void registerPacket(IPacket<D> packet, Class<D> dataClass) {
        CraftTracker.HANDLER.messageBuilder(dataClass, PacketHandler.disc++)
                .encoder(packet::encode)
                .decoder(packet::decode)
                .consumerNetworkThread(packet::handle)
                .add();
    }
}
