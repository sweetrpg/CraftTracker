package com.sweetrpg.crafttracker.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.function.Supplier;

public interface IPacket<D> {

    void encode(D data, FriendlyByteBuf buf);

    D decode(FriendlyByteBuf buf);

    default void doHandle(D data, CustomPayloadEvent.Context ctx) {
        handle(data, () -> ctx);
    }
    void handle(D data, Supplier<CustomPayloadEvent.Context> ctx);
}
