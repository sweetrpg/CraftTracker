package com.sweetrpg.crafttracker.common.config;

import com.sweetrpg.crafttracker.CraftTracker;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

public class ConfigHandler {

    public static ClientConfig CLIENT;
    public static ServerConfig SERVER;
    private static ForgeConfigSpec CONFIG_SERVER_SPEC;
    private static ForgeConfigSpec CONFIG_CLIENT_SPEC;


    public static void init(IEventBus modEventBus) {
        Pair<ServerConfig, ForgeConfigSpec> commonPair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        CONFIG_SERVER_SPEC = commonPair.getRight();
        SERVER = commonPair.getLeft();
        Pair<ClientConfig, ForgeConfigSpec> clientPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        CONFIG_CLIENT_SPEC = clientPair.getRight();
        CLIENT = clientPair.getLeft();
        CraftTracker.LOGGER.debug("Register configs");

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, CONFIG_SERVER_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CONFIG_CLIENT_SPEC);
    }

    public static class ClientConfig {

        public ClientConfig(ForgeConfigSpec.Builder builder) {
            {
                builder.push("General");


                builder.pop();
            }

        }
    }

    public static class ServerConfig {


        public ServerConfig(ForgeConfigSpec.Builder builder) {
            {
                builder.push("General");

                builder.pop();
            }

        }
    }

}
