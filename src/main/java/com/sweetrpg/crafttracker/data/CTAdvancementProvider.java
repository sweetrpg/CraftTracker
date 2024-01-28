package com.sweetrpg.crafttracker.data;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.resources.ResourceLocation;
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
    public void run(HashCache cache) {
        Path path = this.generator.getOutputFolder();
        Set<ResourceLocation> set = Sets.newHashSet();
        Consumer<Advancement> consumer = (advancement) -> {
            if(!set.add(advancement.getId())) {
                throw new IllegalStateException("Duplicate advancement " + advancement.getId());
            }
            else {
                Path path1 = getPath(path, advancement);

                try {
                    DataProvider.save(GSON, cache, advancement.deconstruct().serializeToJson(), path1);
                }
                catch (IOException ioexception) {
                    LOGGER.error("Couldn't save advancement {}", path1, ioexception);
                }
            }
        };

        // training
//        Advancement trainCat = Advancement.Builder.advancement()
////                .parent(Util.mcLoc("tame_animal"))
//                .display(DisplayInfoBuilder.create().icon(ModItems.TRAINING_TREAT).frame(FrameType.TASK).translate("CraftTracker.main.train_cat").background("stone.png").build())
//                .addCriterion("tame_cat", TameAnimalTrigger.TriggerInstance.tamedAnimal(EntityPredicate.Builder.entity().of(ModEntityTypes.CAT.get()).build()))
//                //.withCriterion("get_cat", ItemUseTrigger.TameAnimalTrigger.Instance.create(EntityPredicate.Builder.create().type(CatEntityTypes.CAT.get()).build()))
//                .requirements(RequirementsStrategy.OR)
//                .save(consumer, Util.getResourcePath("main/tame_cat"));

    }
}
