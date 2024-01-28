package com.sweetrpg.crafttracker.common.registry;

import com.sweetrpg.crafttracker.common.lib.Constants;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DataSerializerEntry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ModSerializers {

    public static final DeferredRegister<DataSerializerEntry> SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.DATA_SERIALIZERS, Constants.MOD_ID);

    private static <X extends EntityDataSerializer<?>> RegistryObject<DataSerializerEntry> register2(final String name, final Supplier<X> factory) {
        return register(name, () -> new DataSerializerEntry(factory.get()));
    }

    private static RegistryObject<DataSerializerEntry> register(final String name, final Supplier<DataSerializerEntry> sup) {
        return SERIALIZERS.register(name, sup);
    }
}
