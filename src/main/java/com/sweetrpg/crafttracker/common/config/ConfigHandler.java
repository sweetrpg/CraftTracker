package com.sweetrpg.crafttracker.common.config;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.lib.Constants;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

public class ConfigHandler {

    public static ClientConfig CLIENT;
    public static CommonConfig COMMON;
    public static ServerConfig SERVER;
    private static ForgeConfigSpec CONFIG_CLIENT_SPEC;
    private static ForgeConfigSpec CONFIG_COMMON_SPEC;
    private static ForgeConfigSpec CONFIG_SERVER_SPEC;

    public static void init(IEventBus modEventBus) {
        Pair<ClientConfig, ForgeConfigSpec> clientPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        CONFIG_CLIENT_SPEC = clientPair.getRight();
        CLIENT = clientPair.getLeft();
        Pair<CommonConfig, ForgeConfigSpec> commonPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        CONFIG_COMMON_SPEC = commonPair.getRight();
        COMMON = commonPair.getLeft();
        Pair<ServerConfig, ForgeConfigSpec> serverPair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        CONFIG_SERVER_SPEC = serverPair.getRight();
        SERVER = serverPair.getLeft();

        CraftTracker.LOGGER.debug("register configs");
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CONFIG_CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CONFIG_COMMON_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, CONFIG_SERVER_SPEC);
    }

    public static class ClientConfig {

        // General
        public ForgeConfigSpec.IntValue CALCULATION_DEPTH;
        public ForgeConfigSpec.DoubleValue NON_VANILLA_COST_MULTIPLIER;
        public ForgeConfigSpec.DoubleValue NON_CRAFTING_COST_MULTIPLIER;

        // Craft Queue
        public ForgeConfigSpec.BooleanValue CRAFT_QUEUE_OVERLAY_HIDE_EMPTY;
        public ForgeConfigSpec.IntValue CRAFT_QUEUE_OVERLAY_X;
        public ForgeConfigSpec.IntValue CRAFT_QUEUE_OVERLAY_Y;
        public ForgeConfigSpec.IntValue CRAFT_QUEUE_OVERLAY_WIDTH;
        public ForgeConfigSpec.IntValue CRAFT_QUEUE_OVERLAY_HEIGHT;

        // Shopping List
        public ForgeConfigSpec.BooleanValue SHOPPING_LIST_OVERLAY_HIDE_EMPTY;
        public ForgeConfigSpec.IntValue SHOPPING_LIST_OVERLAY_X;
        public ForgeConfigSpec.IntValue SHOPPING_LIST_OVERLAY_Y;
        public ForgeConfigSpec.IntValue SHOPPING_LIST_OVERLAY_WIDTH;
        public ForgeConfigSpec.IntValue SHOPPING_LIST_OVERLAY_HEIGHT;

        public ClientConfig(ForgeConfigSpec.Builder builder) {
            {
                builder.push("General");

                CALCULATION_DEPTH = builder.comment("Determines how far down the tree the queue calculation will go before it stops").translation(Constants.TRANSLATION_KEY_CONFIG_CLIENT_CALC_DEPTH).defineInRange("calculation_depth", 3, 1, 5);
                NON_VANILLA_COST_MULTIPLIER = builder.comment("Sets the cost multiplier for non-vanilla recipes").translation(Constants.TRANSLATION_KEY_CONFIG_CLIENT_NON_VANILLA_COST_MULTIPLIER).defineInRange("non_vanilla_cost_multiplier", 1.2f, 1, 1000);
                NON_CRAFTING_COST_MULTIPLIER = builder.comment("Sets the cost multiplier for non-crafting table recipes").translation(Constants.TRANSLATION_KEY_CONFIG_CLIENT_NON_CRAFTING_COST_MULTIPLIER).defineInRange("non_crafting_cost_multiplier", 1.25f, 1, 1000);

                builder.pop();
            }

            {
                builder.push("CraftQueue");

                CRAFT_QUEUE_OVERLAY_HIDE_EMPTY = builder.comment("Sets whether the craft queue overlay should be displayed only when it has items in it.").translation(Constants.TRANSLATION_KEY_CONFIG_CLIENT_CRAFT_QUEUE_HIDE_EMPTY).define("craft_queue_hide_empty", true);
                CRAFT_QUEUE_OVERLAY_X = builder.comment("Sets the X screen location for the craft queue overlay.").translation(Constants.TRANSLATION_KEY_CONFIG_CLIENT_CRAFT_QUEUE_X).defineInRange("craft_queue_x", 10, -1000, 10000);
                CRAFT_QUEUE_OVERLAY_Y = builder.comment("Sets the Y screen location for the craft queue overlay.").translation(Constants.TRANSLATION_KEY_CONFIG_CLIENT_CRAFT_QUEUE_Y).defineInRange("craft_queue_y", 60, -1000, 10000);
                CRAFT_QUEUE_OVERLAY_WIDTH = builder.comment("Sets the width of the craft queue overlay.").translation(Constants.TRANSLATION_KEY_CONFIG_CLIENT_CRAFT_QUEUE_WIDTH).defineInRange("craft_queue_width", 300, 100, 10000);
                CRAFT_QUEUE_OVERLAY_HEIGHT = builder.comment("Sets the height of the craft queue overlay.").translation(Constants.TRANSLATION_KEY_CONFIG_CLIENT_CRAFT_QUEUE_HEIGHT).defineInRange("craft_queue_height", 500, 100, 10000);

                builder.pop();
            }

            {
                builder.push("ShoppingList");

                SHOPPING_LIST_OVERLAY_HIDE_EMPTY = builder.comment("Sets whether the 'shopping list' overlay should be displayed only when it has items in it.").translation(Constants.TRANSLATION_KEY_CONFIG_CLIENT_SHOPPING_LIST_HIDE_EMPTY).define("shopping_list_hide_empty", true);
                SHOPPING_LIST_OVERLAY_X = builder.comment("Sets the X screen location for the 'shopping list' overlay.").translation(Constants.TRANSLATION_KEY_CONFIG_CLIENT_SHOPPING_LIST_X).defineInRange("shopping_list_x", -10, -1000, 10000);
                SHOPPING_LIST_OVERLAY_Y = builder.comment("Sets the Y screen location for the 'shopping list' overlay.").translation(Constants.TRANSLATION_KEY_CONFIG_CLIENT_SHOPPING_LIST_Y).defineInRange("shopping_list_y", 60, -1000, 10000);
                SHOPPING_LIST_OVERLAY_WIDTH = builder.comment("Sets the width of the 'shopping list' overlay.").translation(Constants.TRANSLATION_KEY_CONFIG_CLIENT_SHOPPING_LIST_WIDTH).defineInRange("shopping_list_width", 300, 100, 10000);
                SHOPPING_LIST_OVERLAY_HEIGHT = builder.comment("Sets the height of the 'shopping list' overlay.").translation(Constants.TRANSLATION_KEY_CONFIG_CLIENT_SHOPPING_LIST_HEIGHT).defineInRange("shopping_list_height", 500, 100, 10000);

                builder.pop();
            }
        }
    }

    public static class CommonConfig {

        public Map<String, ForgeConfigSpec.IntValue> tagEntries = new HashMap<>();
        public Map<String, ForgeConfigSpec.IntValue> overrideEntries = new HashMap<>();
        public Map<String, ForgeConfigSpec.DoubleValue> namespaceEntries = new HashMap<>();
        public Map<String, ForgeConfigSpec.DoubleValue> recipeTypeEntries = new HashMap<>();

        public CommonConfig(ForgeConfigSpec.Builder builder) {
            {
                builder.push("Costs By Tag");

                Costs.tags.forEach((k, v) -> {
                    tagEntries.put(k, builder.comment("A tag and corresponding cost").defineInRange(k, v, 1, 10000));
                });

                builder.pop();
            }

            {
                builder.push("Cost Overrides");

                Costs.itemOverrides.forEach((k, v) -> {
                    overrideEntries.put(k, builder.comment("An item and corresponding cost override").defineInRange(k, v, 1, 10000));
                });

                builder.pop();
            }

            {
                builder.push("Namespace Multipliers");

                Multipliers.namespaces.forEach((k, v) -> {
                    namespaceEntries.put(k, builder.comment("A namespace and the multiplier associated with it").defineInRange(k, v, 0.1f, 100f));
                });

                builder.pop();
            }

            {
                builder.push("Recipe Type Multipliers");

                Multipliers.recipeTypes.forEach((k, v) -> {
                    recipeTypeEntries.put(k, builder.comment("A recipe type and the multiplier associated with it").defineInRange(k, v, 0.1f, 100f));
                });

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
