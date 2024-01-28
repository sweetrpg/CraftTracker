package com.sweetrpg.crafttracker.common.event;

import com.sweetrpg.crafttracker.common.lib.Constants;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID)
public class EventHandler {

    @SubscribeEvent
    public void rightClickEntity(final PlayerInteractEvent.EntityInteract event) {
        Level world = event.getWorld();

        ItemStack stack = event.getItemStack();
        Entity target = event.getTarget();

    }

    @SubscribeEvent
    public static void onBiomeLoad(BiomeLoadingEvent event) {
        BiomeGenerationSettingsBuilder builder = event.getGeneration();
        Biome.ClimateSettings climate = event.getClimate();

    }

    @SubscribeEvent
    public void onEntitySpawn(final EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();


    }

    @SubscribeEvent
    public void playerLoggedIn(final PlayerLoggedInEvent event) {

    }

    @SubscribeEvent
    public void onLootDrop(final LootingLevelEvent event) {

    }
}
