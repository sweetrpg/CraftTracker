package com.sweetrpg.crafttracker.data;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sweetrpg.crafttracker.common.registry.ModAdvancements;
import com.sweetrpg.crafttracker.common.util.Util;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.AdvancementProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Consumer;

public class CTAdvancementProvider extends AdvancementProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    private final DataGenerator generator;

    public CTAdvancementProvider(DataGenerator generatorIn) {
        super(generatorIn);
        this.generator = generatorIn;
    }

    private static Path getPath(Path pathIn, Advancement advancementIn) {
        return pathIn.resolve("data/" + advancementIn.getId().getNamespace() + "/advancements/" + advancementIn.getId().getPath() + ".json");
    }

    @Override
    public String getName() {
        return "CraftTracker Advancements";
    }

    @Override
    public void run(DirectoryCache cache) {
        Path path = this.generator.getOutputFolder();
        Set<ResourceLocation> set = Sets.newHashSet();
        Consumer<Advancement> consumer = (advancement) -> {
            if(!set.add(advancement.getId())) {
                throw new IllegalStateException("Duplicate advancement " + advancement.getId());
            }
            else {
                Path path1 = getPath(path, advancement);

                try {
                    IDataProvider.save(GSON, cache, advancement.deconstruct().serializeToJson(), path1);
                }
                catch (IOException ioexception) {
                    LOGGER.error("Couldn't save advancement {}", path1, ioexception);
                }
            }
        };

        var root = ModAdvancements.ROOT.deconstruct().save(consumer, Util.getResourcePath("main/root"));
        var queueItem = ModAdvancements.QUEUE_ITEM.deconstruct().parent(root).save(consumer, Util.getResourcePath("main/queue_item"));
        var craftItem = ModAdvancements.CRAFT_ITEM.deconstruct().parent(queueItem).save(consumer, Util.getResourcePath("main/craft_item"));
        var populateList = ModAdvancements.POPULATE_LIST.deconstruct().parent(root).save(consumer, Util.getResourcePath("main/populate_list"));
        var acquireItem = ModAdvancements.ACQUIRE_ITEM.deconstruct().parent(populateList).save(consumer, Util.getResourcePath("main/acquire_item"));
        var clearQueue = ModAdvancements.CLEAR_QUEUE.deconstruct().parent(queueItem).save(consumer, Util.getResourcePath("main/clear_queue"));
    }
}
