package com.sweetrpg.crafttracker.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class CTAdvancementProvider extends ForgeAdvancementProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

    public CTAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper) {
        super(output, registries, existingFileHelper,
                List.of(new DoggyAdvancementsSubProvider())
        );
    }


    // private static Path getPath(Path pathIn, Advancement advancementIn) {
    //     return pathIn.resolve("data/" + advancementIn.getId().getNamespace() + "/advancements/" + advancementIn.getId().getPath() + ".json");
    // }

    public static class DoggyAdvancementsSubProvider implements ForgeAdvancementProvider.AdvancementGenerator {

        @Override
        public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> consumer, ExistingFileHelper existingFileHelper) {
            // TODO
//            var root = ModAdvancements.ROOT.save(consumer, Util.getResourcePath("main/root"));
//            var queueItem = ModAdvancements.QUEUE_ITEM.parent(root).save(consumer, Util.getResourcePath("main/queue_item"));
//            var craftItem = ModAdvancements.CRAFT_ITEM.parent(queueItem).save(consumer, Util.getResourcePath("main/craft_item"));
//            var populateList = ModAdvancements.POPULATE_LIST.parent(root).save(consumer, Util.getResourcePath("main/populate_list"));
//            var acquireItem = ModAdvancements.ACQUIRE_ITEM.parent(populateList).save(consumer, Util.getResourcePath("main/acquire_item"));
//            var clearQueue = ModAdvancements.CLEAR_QUEUE.parent(queueItem).save(consumer, Util.getResourcePath("main/clear_queue"));

        }

    }
}
