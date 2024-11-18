package com.sweetrpg.crafttracker.common.network.packet;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.addon.jei.CTPlugin;
import com.sweetrpg.crafttracker.common.manager.CraftingQueueManager;
import com.sweetrpg.crafttracker.common.network.IPacket;
import com.sweetrpg.crafttracker.common.network.packet.data.AddToQueueData;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class AddToQueuePacket implements IPacket<AddToQueueData> {

    @Override
    public void encode(AddToQueueData data, FriendlyByteBuf buf) {
        buf.writeUtf(data.itemId);
    }

    @Override
    public AddToQueueData decode(FriendlyByteBuf buf) {
        String itemId = buf.readUtf();
        return new AddToQueueData(itemId);
    }

    @Override
    public final void handle(AddToQueueData data, Supplier<Context> ctx) {
        CraftTracker.LOGGER.debug("AddToQueuePacket#handle: {}", data);

        ctx.get().enqueueWork(() -> {
            CTPlugin.jeiRuntime.getIngredientListOverlay().getIngredientUnderMouse()
                    .ifPresent(ingredient -> {
                        CraftTracker.LOGGER.debug("AddToQueuePacket#handle: type {}", ingredient.getType());
                        CraftTracker.LOGGER.debug("AddToQueuePacket#handle: ingredient {}", ingredient.getIngredient());

                        if(ingredient.getIngredient() instanceof ItemStack itemStack) {
                            ResourceLocation res = itemStack.getItem().getRegistryName();
                            CraftTracker.LOGGER.debug("AddToQueuePacket#handle: res {}", res);

                            CraftingQueueManager.INSTANCE.addProduct(res, 1);


                        }
                    });
        });

        ctx.get().setPacketHandled(true);
    }

}
