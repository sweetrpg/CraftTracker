package com.sweetrpg.crafttracker.common.config;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.Constants;
import com.sweetrpg.crafttracker.common.manager.CraftingQueueManager;
import com.sweetrpg.crafttracker.data.Costs;
import com.sweetrpg.crafttracker.data.Multipliers;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigHandler {

    public static ClientConfig CLIENT;
    public static CommonConfig COMMON;
    public static ServerConfig SERVER;

    public static void init(IEventBus modEventBus) {
        Pair<ClientConfig, ForgeConfigSpec> clientPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        ForgeConfigSpec configClientSpec = clientPair.getRight();
        CLIENT = clientPair.getLeft();
        Pair<CommonConfig, ForgeConfigSpec> commonPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        ForgeConfigSpec configCommonSpec = commonPair.getRight();
        COMMON = commonPair.getLeft();
        Pair<ServerConfig, ForgeConfigSpec> serverPair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        ForgeConfigSpec configServerSpec = serverPair.getRight();
        SERVER = serverPair.getLeft();

        CraftTracker.LOGGER.debug("register configs");
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, configClientSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, configCommonSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, configServerSpec);

        modEventBus.addListener(ConfigHandler::configEventHandler);
    }

    public static void configEventHandler(ModConfigEvent event) {
        CraftTracker.LOGGER.debug("#configEventHandler: {}", event);

        if(event instanceof ModConfigEvent.Reloading &&
                ((event.getConfig().getType() == ModConfig.Type.CLIENT) ||
                        (event.getConfig().getType() == ModConfig.Type.COMMON)) &&
                Minecraft.getInstance().level != null) {
            CraftTracker.LOGGER.debug("config is reloading");

            CraftingQueueManager.INSTANCE.computeAll();
        }
    }

    public static class ClientConfig {

        // General
        public ForgeConfigSpec.IntValue calculationDepth;

        // Craft Queue
        public ForgeConfigSpec.BooleanValue craftQueueOverlayHideEmpty;
        public ForgeConfigSpec.IntValue craftQueueOverlayX;
        public ForgeConfigSpec.IntValue craftQueueOverlayY;
        public ForgeConfigSpec.IntValue craftQueueOverlayWidth;
        public ForgeConfigSpec.IntValue craftQueueOverlayHeight;
        public ForgeConfigSpec.ConfigValue<String> craftQueueOverlayBackgroundColor;
        public ForgeConfigSpec.ConfigValue<String> craftQueueOverlayBorderColor;

        // Shopping List
        public ForgeConfigSpec.BooleanValue shoppingListOverlayHideEmpty;
        public ForgeConfigSpec.IntValue shoppingListOverlayX;
        public ForgeConfigSpec.IntValue shoppingListOverlayY;
        public ForgeConfigSpec.IntValue shoppingListOverlayWidth;
        public ForgeConfigSpec.IntValue shoppingListOverlayHeight;
        public ForgeConfigSpec.ConfigValue<String> shoppingListOverlayBackgroundColor;
        public ForgeConfigSpec.ConfigValue<String> shoppingListOverlayBorderColor;

        public ClientConfig(ForgeConfigSpec.Builder builder) {
            {
                builder.push("General");

                calculationDepth = builder.comment("Determines how far down the tree the queue calculation will go before it stops").translation(Constants.TRANSLATION_KEY_CONFIG_CLIENT_CALC_DEPTH).defineInRange("calculation_depth", 3, 1, 5);

                builder.pop();
            }

            {
                builder.push("Craft Queue");

                craftQueueOverlayHideEmpty = builder.comment("Sets whether the craft queue overlay should be displayed only when it has items in it.").translation(Constants.TRANSLATION_KEY_CONFIG_CLIENT_CRAFT_QUEUE_HIDE_EMPTY).define("craft_queue_hide_empty", true);
                craftQueueOverlayX = builder.comment("Sets the X screen location for the craft queue overlay.").translation(Constants.TRANSLATION_KEY_CONFIG_CLIENT_CRAFT_QUEUE_X).defineInRange("craft_queue_x", 10, -1000, 10000);
                craftQueueOverlayY = builder.comment("Sets the Y screen location for the craft queue overlay.").translation(Constants.TRANSLATION_KEY_CONFIG_CLIENT_CRAFT_QUEUE_Y).defineInRange("craft_queue_y", 60, -1000, 10000);
                craftQueueOverlayWidth = builder.comment("Sets the width of the craft queue overlay.").translation(Constants.TRANSLATION_KEY_CONFIG_CLIENT_CRAFT_QUEUE_WIDTH).defineInRange("craft_queue_width", 300, 100, 10000);
                craftQueueOverlayHeight = builder.comment("Sets the height of the craft queue overlay.").translation(Constants.TRANSLATION_KEY_CONFIG_CLIENT_CRAFT_QUEUE_HEIGHT).defineInRange("craft_queue_height", 500, 100, 10000);
                craftQueueOverlayBackgroundColor = builder.comment("Sets the background color of the craft queue overlay.").translation(Constants.TRANSLATION_KEY_CONFIG_CLIENT_CRAFT_QUEUE_BACKGROUND_COLOR).define("craft_queue_background_color", "0x015f5f5f");
                craftQueueOverlayBorderColor = builder.comment("Sets the border color of the craft queue overlay.").translation(Constants.TRANSLATION_KEY_CONFIG_CLIENT_CRAFT_QUEUE_BORDER_COLOR).define("craft_queue_border_color", "0x011f1f1f");

                builder.pop();
            }

            {
                builder.push("Shopping List");

                shoppingListOverlayHideEmpty = builder.comment("Sets whether the 'shopping list' overlay should be displayed only when it has items in it.").translation(Constants.TRANSLATION_KEY_CONFIG_CLIENT_SHOPPING_LIST_HIDE_EMPTY).define("shopping_list_hide_empty", true);
                shoppingListOverlayX = builder.comment("Sets the X screen location for the 'shopping list' overlay.").translation(Constants.TRANSLATION_KEY_CONFIG_CLIENT_SHOPPING_LIST_X).defineInRange("shopping_list_x", -10, -1000, 10000);
                shoppingListOverlayY = builder.comment("Sets the Y screen location for the 'shopping list' overlay.").translation(Constants.TRANSLATION_KEY_CONFIG_CLIENT_SHOPPING_LIST_Y).defineInRange("shopping_list_y", 60, -1000, 10000);
                shoppingListOverlayWidth = builder.comment("Sets the width of the 'shopping list' overlay.").translation(Constants.TRANSLATION_KEY_CONFIG_CLIENT_SHOPPING_LIST_WIDTH).defineInRange("shopping_list_width", 300, 100, 10000);
                shoppingListOverlayHeight = builder.comment("Sets the height of the 'shopping list' overlay.").translation(Constants.TRANSLATION_KEY_CONFIG_CLIENT_SHOPPING_LIST_HEIGHT).defineInRange("shopping_list_height", 500, 100, 10000);
                shoppingListOverlayBackgroundColor = builder.comment("Sets the background color of the shopping list overlay.").translation(Constants.TRANSLATION_KEY_CONFIG_CLIENT_SHOPPING_LIST_BACKGROUND_COLOR).define("shopping_list_background_color", "0x015f5f5f");
                shoppingListOverlayBorderColor = builder.comment("Sets the border color of the shopping list overlay.").translation(Constants.TRANSLATION_KEY_CONFIG_CLIENT_SHOPPING_LIST_BORDER_COLOR).define("shopping_list_border_color", "0x011f1f1f");

                builder.pop();
            }
        }
    }

    public static class CommonConfig {

        public Map<String, ForgeConfigSpec.IntValue> tagEntries = new HashMap<>();
        public Map<String, ForgeConfigSpec.IntValue> overrideEntries = new HashMap<>();
        public List<String> rawMaterials = new ArrayList<>();
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
                builder.push("Raw Material Overrides");

                rawMaterials.addAll(Costs.alwaysRawMaterials);

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
