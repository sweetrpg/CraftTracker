package com.sweetrpg.crafttracker.data;

import com.sweetrpg.crafttracker.common.lib.Constants;
import com.sweetrpg.crafttracker.common.registry.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class CTBlockstateProvider extends BlockStateProvider {

    // Applies texture to all faces and for the input face culls that direction
    private static final BiFunction<String, Direction, BiConsumer<Direction, ModelBuilder<BlockModelBuilder>.ElementBuilder.FaceBuilder>> cullFaceFactory = (texture, input) -> (d, b) -> b.texture(texture).cullface(d == input ? d : null);

    public CTBlockstateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, Constants.MOD_ID, exFileHelper);
    }

    public ExistingFileHelper getExistingHelper() {
        return this.models().existingFileHelper;
    }

    @Override
    public String getName() {
        return "CraftTracker Blockstates/Block Models";
    }

    @Override
    protected void registerStatesAndModels() {
    }

    private String blockName(Block block) {
        return block.getRegistryName().getPath();
    }

    public ResourceLocation resourceBlock(String path) {
        return new ResourceLocation(Constants.MOD_ID, "block/" + path);
    }

    protected void createFromShape(Supplier<? extends Block> blockIn, AABB bb) {
        BlockModelBuilder model = this.models()
                                      .getBuilder(name(blockIn))
                                      .parent(this.models().getExistingFile(mcLoc(ModelProvider.BLOCK_FOLDER + "/block")))
                                      .texture("particle", extend(blockTexture(blockIn), "_bottom"))
                                      .texture("bottom", extend(blockTexture(blockIn), "_bottom"))
                                      .texture("top", extend(blockTexture(blockIn), "_top"))
                                      .texture("side", extend(blockTexture(blockIn), "_side"));

        model.element()
             .from((float) bb.minX, (float) bb.minY, (float) bb.minZ)
             .to((float) bb.maxX, (float) bb.maxY, (float) bb.maxZ)
             .allFaces((d, f) -> f.cullface(d == Direction.DOWN ? d : null).texture(d.getAxis().isHorizontal() ? "#side" : d == Direction.DOWN ? "#bottom" : "#top"));

        this.simpleBlock(blockIn.get(), model);
    }

    public void stageBlock(Block block, IntegerProperty ageProperty, Property<?>... ignored) {
        getVariantBuilder(block).forAllStatesExcept(state -> {
            int ageSuffix = state.getValue(ageProperty);
            String stageName = blockName(block) + "_stage" + ageSuffix;
            return ConfiguredModel.builder().modelFile(models().cross(stageName, resourceBlock(stageName))).build();
        }, ignored);
    }

    public void customStageBlock(Block block, @Nullable ResourceLocation parent, String textureKey, IntegerProperty ageProperty, List<Integer> suffixes, Property<?>... ignored) {
        getVariantBuilder(block).forAllStatesExcept(state -> {
            int ageSuffix = state.getValue(ageProperty);
            String stageName = blockName(block) + "_stage";
            stageName += suffixes.isEmpty() ? ageSuffix : suffixes.get(Math.min(suffixes.size(), ageSuffix));
            if(parent == null) {
                return ConfiguredModel.builder().modelFile(models().cross(stageName, resourceBlock(stageName))).build();
            }
            return ConfiguredModel.builder().modelFile(models().singleTexture(stageName, parent, textureKey, resourceBlock(stageName))).build();
        }, ignored);
    }

    private String name(Supplier<? extends IForgeRegistryEntry<?>> block) {
        return block.get().getRegistryName().getPath();
    }

    private ResourceLocation blockTexture(Supplier<? extends Block> block) {
        ResourceLocation base = block.get().getRegistryName();
        return prextend(base, ModelProvider.BLOCK_FOLDER + "/");
    }

    public ModelFile cross(Supplier<? extends Block> block) {
        return this.models().cross(name(block), blockTexture(block));
    }

    protected void makeSimple(Supplier<? extends Block> blockIn) {
        this.simpleBlock(blockIn.get());
    }

    private ResourceLocation prextend(ResourceLocation rl, String prefix) {
        return new ResourceLocation(rl.getNamespace(), prefix + rl.getPath());
    }

    private ResourceLocation extend(ResourceLocation rl, String suffix) {
        return new ResourceLocation(rl.getNamespace(), rl.getPath() + suffix);
    }
}
