package com.sweetrpg.crafttracker.common.event;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.lib.Constants;
import com.sweetrpg.crafttracker.common.network.PacketHandler;
import com.sweetrpg.crafttracker.common.network.packet.data.QueueCommandData;
import com.sweetrpg.crafttracker.client.event.CraftingEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

import static com.sweetrpg.crafttracker.common.network.packet.data.QueueCommandData.QueueCommand.RECALCULATE;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID)
public class EventHandler {

//    @SubscribeEvent
//    public void rightClickEntity(final PlayerInteractEvent.EntityInteract event) {
//        CraftTracker.LOGGER.debug("EventHandler#rightClickEntity: {}", event);
//
//        Level world = event.getWorld();
//
//        ItemStack stack = event.getItemStack();
//        Entity target = event.getTarget();
//
//    }
//
//    @SubscribeEvent
//    public static void onBiomeLoad(BiomeLoadingEvent event) {
//        CraftTracker.LOGGER.debug("EventHandler#onBiomeLoad: {}", event);
//
//        BiomeGenerationSettingsBuilder builder = event.getGeneration();
//        Biome.ClimateSettings climate = event.getClimate();
//
//    }
//
    @SubscribeEvent
    public void onEntitySpawn(final EntityJoinWorldEvent event) {
        CraftTracker.LOGGER.trace("EventHandler#onEntitySpawn: {}", event);

        Entity entity = event.getEntity();

//        if(entity instanceof ServerPlayer player) {
//            CraftingQueueManager.get(player, entity.level);
//        }
    }

    @SubscribeEvent
    public void playerLoggedIn(final PlayerLoggedInEvent event) {
        CraftTracker.LOGGER.debug("EventHandler#playerLoggedIn: {}", event);

    }

    @SubscribeEvent
    public void onItemCrafted(final ItemCraftedEvent event) {
        CraftTracker.LOGGER.debug("EventHandler#onItemCrafted: {}", event);

        if (event.getPlayer().level.isClientSide) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                var itemId = event.getCrafting().getItem().getRegistryName();
                var quantity = event.getCrafting().getCount();
                CraftingEvents.removeProduct(itemId, quantity);
            });
        }
        else {
            // send packet
            PacketHandler.sendToPlayer((ServerPlayer)event.getPlayer(), new QueueCommandData(RECALCULATE));
        }
    }

    @SubscribeEvent
    public void onItemSmelted(final ItemSmeltedEvent event) {
        CraftTracker.LOGGER.debug("EventHandler#onItemSmelted: {}", event);

        if (event.getPlayer().level.isClientSide) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                var itemId = event.getSmelting().getItem().getRegistryName();
                var quantity = event.getSmelting().getCount();
                CraftingEvents.removeProduct(itemId, quantity);
            });
        }
        else {
            // send packet
            PacketHandler.sendToPlayer((ServerPlayer)event.getPlayer(), new QueueCommandData(RECALCULATE));
        }
    }

    @SubscribeEvent
    public void onItemPickedUp(final ItemPickupEvent event) {
        CraftTracker.LOGGER.debug("EventHandler#onItemPickedUp: {}", event);

        if (event.getPlayer().level.isClientSide) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                var itemId = event.getStack().getItem().getRegistryName();
                var quantity = event.getStack().getCount();
                CraftingEvents.removeProduct(itemId, quantity);
            });
        }
        else {
            // send packet
            PacketHandler.sendToPlayer((ServerPlayer)event.getPlayer(), new QueueCommandData(RECALCULATE));
        }
    }

}
