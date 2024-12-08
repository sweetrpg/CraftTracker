package com.sweetrpg.crafttracker.common.network;

import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;
import net.minecraftforge.network.NetworkEvent.Context;

public interface IPacket<D> {

    void encode(D data, FriendlyByteBuf buf);

    D decode(FriendlyByteBuf buf);

    void handle(D data, Supplier<Context> ctx);
}
