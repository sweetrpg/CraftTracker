package com.sweetrpg.crafttracker.common.event;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.lib.Constants;
import com.sweetrpg.crafttracker.common.manager.CraftingQueueManager;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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

        CraftingQueueManager.INSTANCE.load(event.getPlayer());
    }

//    @SubscribeEvent
//    public void onLootDrop(final LootingLevelEvent event) {
//        CraftTracker.LOGGER.debug("EventHandler#onLootDrop: {}", event);
//
//    }

}
