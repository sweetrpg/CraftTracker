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

        public ForgeConfigSpec.BooleanValue CRAFT_QUEUE_OVERLAY_HIDE_EMPTY;
        public ForgeConfigSpec.IntValue CRAFT_QUEUE_OVERLAY_X;
        public ForgeConfigSpec.IntValue CRAFT_QUEUE_OVERLAY_Y;
        public ForgeConfigSpec.IntValue CRAFT_QUEUE_OVERLAY_WIDTH;
        public ForgeConfigSpec.IntValue CRAFT_QUEUE_OVERLAY_HEIGHT;
        public ForgeConfigSpec.BooleanValue SHOPPING_LIST_OVERLAY_HIDE_EMPTY;
        public ForgeConfigSpec.IntValue SHOPPING_LIST_OVERLAY_X;
        public ForgeConfigSpec.IntValue SHOPPING_LIST_OVERLAY_Y;
        public ForgeConfigSpec.IntValue SHOPPING_LIST_OVERLAY_WIDTH;
        public ForgeConfigSpec.IntValue SHOPPING_LIST_OVERLAY_HEIGHT;

        public ClientConfig(ForgeConfigSpec.Builder builder) {
            {
                builder.push("General");


                builder.pop();
            }

            {
                builder.push("CraftQueue");

                CRAFT_QUEUE_OVERLAY_HIDE_EMPTY = builder.comment("Sets whether the craft queue overlay should be displayed only when it has items in it.").translation("crafttracker.config.client.craft_queue_hide_empty").define("craft_queue_hide_empty", true);
                CRAFT_QUEUE_OVERLAY_X = builder.comment("Sets the X screen location for the craft queue overlay.").translation("crafttracker.config.client.craft_queue_x").defineInRange("craft_queue_x", 10, 0, 10000);
                CRAFT_QUEUE_OVERLAY_Y = builder.comment("Sets the Y screen location for the craft queue overlay.").translation("crafttracker.config.client.craft_queue_y").defineInRange("craft_queue_y", 60, 0, 10000);
                CRAFT_QUEUE_OVERLAY_WIDTH = builder.comment("Sets the width of the craft queue overlay.").translation("crafttracker.config.client.craft_queue_width").defineInRange("craft_queue_width", 300, 100, 10000);
                CRAFT_QUEUE_OVERLAY_HEIGHT = builder.comment("Sets the height of the craft queue overlay.").translation("crafttracker.config.client.craft_queue_height").defineInRange("craft_queue_height", 500, 100, 10000);

                builder.pop();
            }

            {
                builder.push("ShoppingList");

                SHOPPING_LIST_OVERLAY_HIDE_EMPTY = builder.comment("Sets whether the 'shopping list' overlay should be displayed only when it has items in it.").translation("crafttracker.config.client.shopping_list_hide_empty").define("shopping_list_hide_empty", true);
                SHOPPING_LIST_OVERLAY_X = builder.comment("Sets the X screen location for the 'shopping list' overlay.").translation("crafttracker.config.client.shopping_list_x").defineInRange("shopping_list_x", 10, 0, 10000);
                SHOPPING_LIST_OVERLAY_Y = builder.comment("Sets the Y screen location for the 'shopping list' overlay.").translation("crafttracker.config.client.shopping_list_y").defineInRange("shopping_list_y", 60, 0, 10000);
                SHOPPING_LIST_OVERLAY_WIDTH = builder.comment("Sets the width of the 'shopping list' overlay.").translation("crafttracker.config.client.shopping_list_width").defineInRange("shopping_list_width", 100, 100, 10000);
                SHOPPING_LIST_OVERLAY_HEIGHT = builder.comment("Sets the height of the 'shopping list' overlay.").translation("crafttracker.config.client.shopping_list_height").defineInRange("shopping_list_height", 100, 100, 10000);

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
