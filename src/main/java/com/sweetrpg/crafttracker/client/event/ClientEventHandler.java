package com.sweetrpg.crafttracker.client.event;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.network.PacketHandler;
import com.sweetrpg.crafttracker.common.registry.ModKeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.Map;

public class ClientEventHandler {

//    public static void onModelBakeEvent(final ModelBakeEvent event) {
//        Map<ResourceLocation, BakedModel> modelRegistry = event.getModelRegistry();
//
////        // cat tree
////        try {
////            ResourceLocation resourceLocation = ForgeRegistries.BLOCKS.getKey(ModBlocks.CAT_TREE.get());
////            ResourceLocation unbakedModelLoc = new ResourceLocation(resourceLocation.getNamespace(), "block/" + resourceLocation.getPath());
////
////            BlockModel model = (BlockModel) event.getModelLoader().getModel(unbakedModelLoc);
////            BakedModel customModel = new CatTreeModel(event.getModelLoader(), model, model.bake(event.getModelLoader(), model, ForgeModelBakery.defaultTextureGetter(), BlockModelRotation.X180_Y180, unbakedModelLoc, true));
////
////            // Replace all valid block states
////            ModBlocks.CAT_TREE.get().getStateDefinition().getPossibleStates().forEach(state -> {
////                modelRegistry.put(BlockModelShaper.stateToModelLocation(state), customModel);
////            });
////
////            // Replace inventory model
////            modelRegistry.put(new ModelResourceLocation(resourceLocation, "inventory"), customModel);
////        }
////        catch(Exception e) {
////            CraftTracker.LOGGER.warn("Could not get base Cat Tree model. Reverting to default textures...");
////            e.printStackTrace();
////        }
//
//    }

//    @SubscribeEvent
    public static void onKeyInput(final InputEvent.KeyInputEvent event) {
        CraftTracker.LOGGER.debug("#onKeyInput: {}", event);

        if(ModKeyBindings.ADD_TO_QUEUE_MAPPING.consumeClick()) {

        }
    }

    @SubscribeEvent
    public void onInputEvent(final MovementInputUpdateEvent event) {

    }

    @SubscribeEvent
    public void onScreenInit(final ScreenEvent.InitScreenEvent.Post event) {
        Screen screen = event.getScreen();
        if (screen instanceof InventoryScreen || screen instanceof CreativeModeInventoryScreen) {
            boolean creative = screen instanceof CreativeModeInventoryScreen;
//            boolean dtLoaded = ModList.get().isLoaded("doggytalents");
            Minecraft mc = Minecraft.getInstance();
            int width = mc.getWindow().getGuiScaledWidth();
            int height = mc.getWindow().getGuiScaledHeight();
            int sizeX = creative ? 195 : 176;
            int sizeY = creative ? 136 : 166;
            int guiLeft = (width - sizeX) / 2 - ((creative) ? 15 : 0);
            int guiTop = (height - sizeY) / 2 - ((!creative) ? 13 : 0);

            int x = guiLeft + (creative ? 36 : sizeX / 2 - 10);
            int y = guiTop + (creative ? 7 : 48);

//            event.addListener(new Button(x, y, screen, (btn) -> {
//                PacketHandler.send(PacketDistributor.SERVER.noArg(), new OpenCatScreenData());
////                btn.active = false;
//            }));
        }
    }

    @SubscribeEvent
    public void onScreenDrawForeground(final ScreenEvent.DrawScreenEvent event) {
        Screen screen = event.getScreen();
        if (screen instanceof InventoryScreen || screen instanceof CreativeModeInventoryScreen) {
            boolean creative = screen instanceof CreativeModeInventoryScreen;
//            CatInventoryButton btn = null;
//
//            //TODO just create a static variable in this class
//            for (Widget widget : screen.renderables) {
//                if (widget instanceof CatInventoryButton) {
//                    btn = (CatInventoryButton) widget;
//                    break;
//                }
//            }
//
//            if (btn.visible && btn.isHoveredOrFocused()) {
//                Minecraft mc = Minecraft.getInstance();
//                int width = mc.getWindow().getGuiScaledWidth();
//                int height = mc.getWindow().getGuiScaledHeight();
//                int sizeX = creative ? 195 : 176;
//                int sizeY = creative ? 136 : 166;
//                int guiLeft = (width - sizeX) / 2;
//                int guiTop = (height - sizeY) / 2;
//                if (!creative) {
//                    RecipeBookComponent recipeBook = ((InventoryScreen) screen).getRecipeBookComponent();
//                    if (recipeBook.isVisible()) {
//                        guiLeft += 76;
//                    }
//                }
//
//                //event.getPoseStack().translate(-guiLeft, -guiTop, 0);
//                btn.renderToolTip(event.getPoseStack(), event.getMouseX(), event.getMouseY());
//                //event.getPoseStack().translate(guiLeft, guiTop, 0);
//            }
        }
    }

    public void drawSelectionBox(PoseStack matrixStackIn, Player player, float particleTicks, AABB boundingBox) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        // RenderSystem.disableAlphaTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 0.7F);
        //TODO Used when drawing outline of bounding box
        RenderSystem.lineWidth(2.0F);


        RenderSystem.disableTexture();
        Vec3 vec3d = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        double d0 = vec3d.x();
        double d1 = vec3d.y();
        double d2 = vec3d.z();

        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        LevelRenderer.renderLineBox(matrixStackIn, bufferbuilder, boundingBox.move(-d0, -d1, -d2), 1F, 1F, 0F, 0.8F);
        Tesselator.getInstance().end();
        RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 0.3F);
        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        //RenderSystem.enableAlphaTest();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

}
