package com.sweetrpg.crafttracker.common.network.packet;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.network.IPacket;
import com.sweetrpg.crafttracker.common.network.packet.data.AdvancementData;
import com.sweetrpg.crafttracker.common.registry.ModAdvancements;
import com.sweetrpg.crafttracker.common.util.AdvancementUtil;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class AdvancementPacket implements IPacket<AdvancementData> {

    @Override
    public void encode(AdvancementData data, FriendlyByteBuf buf) {
        buf.writeEnum(data.advancement);
    }

    @Override
    public AdvancementData decode(FriendlyByteBuf buf) {
        var advancement = buf.readEnum(ModAdvancements.Key.class);
        return new AdvancementData(advancement);
    }

    @Override
    public final void handle(AdvancementData data, Supplier<NetworkEvent.Context> ctx) {
        CraftTracker.LOGGER.debug("AdvancementPacket#handle: {}", data);

        ctx.get().enqueueWork(() -> {
            AdvancementUtil.trigger(data.advancement, ctx.get().getSender());
        });

        ctx.get().setPacketHandled(true);
    }

}
