package com.sweetrpg.crafttracker.common.registry;

import com.sweetrpg.crafttracker.common.lib.Constants;
import com.sweetrpg.crafttracker.common.util.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Function;
import java.util.function.Supplier;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.Keys.SOUND_EVENTS, Constants.MOD_ID);

    private static RegistryObject<SoundEvent> register(final String name) {
        return register(name, SoundEvent::new);
    }

    private static <T extends SoundEvent> RegistryObject<T> register(final String name, final Function<ResourceLocation, T> factory) {
        return register(name, () -> factory.apply(Util.getResource(name)));
    }

    private static <T extends SoundEvent> RegistryObject<T> register(final String name, final Supplier<T> sup) {
        return SOUNDS.register(name, sup);
    }
}
