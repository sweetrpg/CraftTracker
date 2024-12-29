package com.sweetrpg.crafttracker;

import com.sweetrpg.crafttracker.client.ClientSetup;
import com.sweetrpg.crafttracker.client.event.ClientEventHandler;
import com.sweetrpg.crafttracker.common.CommonSetup;
import com.sweetrpg.crafttracker.common.Constants;
import com.sweetrpg.crafttracker.common.config.ConfigHandler;
import com.sweetrpg.crafttracker.common.event.EventHandler;
import com.sweetrpg.crafttracker.data.CTAdvancementProvider;
import com.sweetrpg.crafttracker.data.CTLangProvider;
import com.sweetrpg.crafttracker.integration.AddonManager;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Paulyhedral
 */
@Mod(Constants.MOD_ID)
public class CraftTracker {

    public static final Logger LOGGER = LogManager.getLogger(Constants.MOD_ID);

    public static final SimpleChannel HANDLER = NetworkRegistry.ChannelBuilder.named(Constants.CHANNEL_NAME)
            .clientAcceptedVersions(Constants.PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(Constants.PROTOCOL_VERSION::equals)
            .networkProtocolVersion(Constants.PROTOCOL_VERSION::toString)
            .simpleChannel();


    public CraftTracker() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Mod lifecycle
        modEventBus.addListener(this::gatherData);
        modEventBus.addListener(CommonSetup::init);
        modEventBus.addListener(this::interModProcess);

        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        forgeEventBus.addListener(this::serverStarting);
        forgeEventBus.addListener(this::registerCommands);

        forgeEventBus.register(new EventHandler());

        // Client Events
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            modEventBus.addListener(this::clientSetup);
            modEventBus.addListener(ClientSetup::addKeyBindings);

            forgeEventBus.register(new ClientEventHandler());
        });

        ConfigHandler.init(modEventBus);

        AddonManager.init();
    }

    public void serverStarting(final ServerStartingEvent event) {
        LOGGER.debug("Server starting");
    }

    public void registerCommands(final RegisterCommandsEvent event) {
        LOGGER.debug("Register commands");
    }

    @OnlyIn(Dist.CLIENT)
    public void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.debug("Client startup");

    }

    protected void interModProcess(final InterModProcessEvent event) {
        LOGGER.debug("event {}", event);

        AddonManager.init();
    }

    private void gatherData(final GatherDataEvent event) {
        LOGGER.debug("Gather data: {}", event);

        DataGenerator gen = event.getGenerator();
        PackOutput packOutput = gen.getPackOutput();
        var lookup = event.getLookupProvider();

        gen.addProvider(event.includeServer(), new CTLangProvider(packOutput, Constants.LOCALE_EN_US));
        gen.addProvider(event.includeServer(), new CTLangProvider(packOutput, Constants.LOCALE_EN_GB));
        gen.addProvider(event.includeServer(), new CTLangProvider(packOutput, Constants.LOCALE_DE_DE));

        gen.addProvider(event.includeServer(), new CTAdvancementProvider(packOutput, lookup, event.getExistingFileHelper()));
    }
}
