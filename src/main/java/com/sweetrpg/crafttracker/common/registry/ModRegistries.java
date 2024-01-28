package com.sweetrpg.crafttracker.common.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

public class ModRegistries {

    protected class Keys {
//        public static final ResourceLocation TALENTS_REGISTRY = Util.getResource("talents");
    }

    public static void newRegistry(NewRegistryEvent event) {
    }

    private static <T extends IForgeRegistryEntry<T>> RegistryBuilder<T> makeRegistry(final ResourceLocation rl, Class<T> type) {
        return new RegistryBuilder<T>().setName(rl).setType(type);
    }

}
