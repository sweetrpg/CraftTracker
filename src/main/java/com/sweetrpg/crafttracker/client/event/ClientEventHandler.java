package com.sweetrpg.crafttracker.client.event;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.client.screen.QueueManagementScreen;
import com.sweetrpg.crafttracker.common.addon.jei.CTPlugin;
import com.sweetrpg.crafttracker.common.lib.CTRuntime;
import com.sweetrpg.crafttracker.common.lib.Constants;
import com.sweetrpg.crafttracker.common.manager.CraftingQueueManager;
import com.sweetrpg.crafttracker.common.manager.ShoppingListManager;
import com.sweetrpg.crafttracker.common.registry.ModKeyBindings;
import com.sweetrpg.crafttracker.common.util.InventoryUtil;
import com.sweetrpg.crafttracker.common.util.KeyUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEventHandler {

    public static void onClientTick(TickEvent.ClientTickEvent event) {
        CraftTracker.LOGGER.trace("#onClientTick: {}", event);

//        if(event.phase != TickEvent.Phase.END) return;
//
//        if(ModKeyBindings.ADD_TO_QUEUE_MAPPING.consumeClick()) {
//            CraftTracker.LOGGER.debug("#onKeyInput: ADD_TO_QUEUE_MAPPING");
//
//            handleAddToQueue();
//        }
//        else if(ModKeyBindings.TOGGLE_CRAFT_QUEUE_MAPPING.consumeClick()) {
//            CraftTracker.LOGGER.debug("#onKeyInput: TOGGLE_CRAFT_LIST_MAPPING");
//
//            handleToggleCraftList();
//        }
//        else if(ModKeyBindings.TOGGLE_SHOPPING_LIST_MAPPING.consumeClick()) {
//            CraftTracker.LOGGER.debug("#onKeyInput: TOGGLE_SHOPPING_LIST_MAPPING");
//
//            handleToggleShoppingList();
//        }
//        else if(ModKeyBindings.OPEN_QUEUE_MANAGER_MAPPING.consumeClick()) {
//            CraftTracker.LOGGER.debug("#onKeyInput: OPEN_QUEUE_MANAGER_MAPPING");
//
//            QueueManagementScreen.open();
//        }
//        else if(ModKeyBindings.POPULATE_SHOPPING_LIST_MAPPING.consumeClick()) {
//            CraftTracker.LOGGER.debug("#onKeyInput: POPULATE_SHOPPING_LIST_MAPPING");
//
//            handlePopulateShoppingList();
//        }
    }

    //    @SubscribeEvent
    public static void onKeyInput(final InputEvent.KeyInputEvent event) {
        CraftTracker.LOGGER.trace("#onKeyInput: {}", event);

        var screen = Minecraft.getInstance().screen;
        if(screen == null) {
            // in the game world

            if(KeyUtil.isKeyDown(event.getKey()) &&
                    ModKeyBindings.TOGGLE_CRAFT_QUEUE_MAPPING.matches(event.getKey(), event.getScanCode())) {
                CraftTracker.LOGGER.debug("#onKeyInput: TOGGLE_CRAFT_LIST_MAPPING");

                handleToggleCraftList();
            }
            else if(KeyUtil.isKeyDown(event.getKey()) &&
                    ModKeyBindings.TOGGLE_SHOPPING_LIST_MAPPING.matches(event.getKey(), event.getScanCode())) {
                CraftTracker.LOGGER.debug("#onKeyInput: TOGGLE_SHOPPING_LIST_MAPPING");

                handleToggleShoppingList();
            }
            else if(KeyUtil.isKeyDown(event.getKey()) &&
                    ModKeyBindings.OPEN_QUEUE_MANAGER_MAPPING.matches(event.getKey(), event.getScanCode())) {
                CraftTracker.LOGGER.debug("#onKeyInput: OPEN_QUEUE_MANAGER_MAPPING");

                QueueManagementScreen.open();
            }
            else if(KeyUtil.isKeyDown(event.getKey()) &&
                    ModKeyBindings.POPULATE_SHOPPING_LIST_MAPPING.matches(event.getKey(), event.getScanCode())) {
                CraftTracker.LOGGER.debug("#onKeyInput: POPULATE_SHOPPING_LIST_MAPPING");

                handlePopulateShoppingList();
            }
            else if(KeyUtil.isKeyDown(event.getKey()) &&
                    ModKeyBindings.CLEAR_SHOPPING_LIST_MAPPING.matches(event.getKey(), event.getScanCode())) {
                CraftTracker.LOGGER.debug("#onKeyInput: CLEAR_SHOPPING_LIST_MAPPING");

                handleClearShoppingList();
            }

            return;
        }

        if(screen instanceof CraftingScreen ||
                screen instanceof InventoryScreen) { // TODO: others?
            if(ModKeyBindings.ADD_TO_QUEUE_MAPPING.matches(event.getKey(), event.getScanCode())) {
                CraftTracker.LOGGER.debug("#onKeyInput: ADD_TO_QUEUE_MAPPING");

                handleAddToQueue();
            }
        }
    }

    private static void handleToggleCraftList() {
        CraftTracker.LOGGER.debug("#handleToggleCraftList");

        var player = Minecraft.getInstance().player;
        TranslatableComponent msg;
        switch(CTRuntime.INSTANCE.queueOverlayRequestedState) {
            case SHOW:
                CTRuntime.INSTANCE.queueOverlayRequestedState = CTRuntime.OverlayState.HIDE;
                msg = new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_MSG_QUEUE_OVERLAY_MODE_HIDE);
                player.displayClientMessage(msg, true);
                break;

            case HIDE:
                CTRuntime.INSTANCE.queueOverlayRequestedState = CTRuntime.OverlayState.DYNAMIC;
                msg = new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_MSG_QUEUE_OVERLAY_MODE_DYNAMIC);
                player.displayClientMessage(msg, true);
                break;

            case DYNAMIC:
                CTRuntime.INSTANCE.queueOverlayRequestedState = CTRuntime.OverlayState.SHOW;
                msg = new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_MSG_QUEUE_OVERLAY_MODE_SHOW);
                player.displayClientMessage(msg, true);
                break;
        }
    }

    private static void handleClearShoppingList() {
        CraftTracker.LOGGER.debug("#handleClearShoppingList");

        var player = Minecraft.getInstance().player;
        var sMgr = ShoppingListManager.INSTANCE;

        sMgr.clearItems(player);
    }

    private static void handlePopulateShoppingList() {
        CraftTracker.LOGGER.debug("#handlePopulateShoppingList");

        var player = Minecraft.getInstance().player;
        var sMgr = ShoppingListManager.INSTANCE;
        var qMgr = CraftingQueueManager.INSTANCE;

        sMgr.clearItems(player);

        var materials = qMgr.getRawMaterials();
        var fuel = qMgr.getFuel();

        materials.forEach(m -> {
            var haveQty = InventoryUtil.getQuantityOf(player, m.getItemId());
            var needed = m.getAmount() - haveQty;

            if(needed > 0)
                sMgr.addItem(player, m.getItemId(), needed);
        });
        fuel.forEach(f -> {
            var haveQty = InventoryUtil.getQuantityOf(player, f.getItemId());
            var needed = f.getAmount() - haveQty;

            if(needed > 0)
                sMgr.addItem(player, f.getItemId(), needed);
        });
    }

    private static void handleToggleShoppingList() {
        CraftTracker.LOGGER.debug("#handleToggleShoppingList");

        var player = Minecraft.getInstance().player;
        TranslatableComponent msg;
        switch(CTRuntime.INSTANCE.shoppingOverlayRequestedState) {
            case SHOW:
                CTRuntime.INSTANCE.shoppingOverlayRequestedState = CTRuntime.OverlayState.HIDE;
                msg = new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_MSG_SLIST_OVERLAY_MODE_HIDE);
                player.displayClientMessage(msg, true);
                break;

            case HIDE:
                CTRuntime.INSTANCE.shoppingOverlayRequestedState = CTRuntime.OverlayState.DYNAMIC;
                msg = new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_MSG_SLIST_OVERLAY_MODE_DYNAMIC);
                player.displayClientMessage(msg, true);
                break;

            case DYNAMIC:
                CTRuntime.INSTANCE.shoppingOverlayRequestedState = CTRuntime.OverlayState.SHOW;
                msg = new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_MSG_SLIST_OVERLAY_MODE_SHOW);
                player.displayClientMessage(msg, true);
                break;
        }
    }

    private static void handleAddToQueue() {
        CraftTracker.LOGGER.debug("#handleAddToQueue");

        CTPlugin.jeiRuntime.getIngredientListOverlay().getIngredientUnderMouse()
                .ifPresent(ingredient -> {
                    CraftTracker.LOGGER.debug("#handleAddToQueue: type {}", ingredient.getType());
                    CraftTracker.LOGGER.debug("#handleAddToQueue: ingredient {}", ingredient.getIngredient());

                    if(ingredient.getIngredient() instanceof ItemStack itemStack) {
                        ResourceLocation res = itemStack.getItem().getRegistryName();
                        CraftTracker.LOGGER.debug("#handleAddToQueue: res {}", res);

                        var player = Minecraft.getInstance().player;
                        CraftingQueueManager.INSTANCE.addProduct(player, res, 1);
//                            PacketHandler.sendToServer(new AddToQueueData(res, 1));
                    }
                });
    }

    @SubscribeEvent
    public void onInputEvent(final MovementInputUpdateEvent event) {
        CraftTracker.LOGGER.trace("#onInputEvent: {}", event);

    }

//    public static void onScreenRemove() {
//        CraftTracker.LOGGER.trace("#onScreenRemove: {}", event);
//
//        CTRuntime.INSTANCE.screenOpen = false;
//    }

    @SubscribeEvent
    public static void onScreenInit(final ScreenEvent.InitScreenEvent.Post event) {
        CraftTracker.LOGGER.trace("#onScreenInit: {}", event);

//        CTRuntime.INSTANCE.screenOpen = true;

//        Screen screen = event.getScreen();
//        if(screen instanceof InventoryScreen || screen instanceof CreativeModeInventoryScreen) {
//            boolean creative = screen instanceof CreativeModeInventoryScreen;
////            CraftTracker.LOGGER.debug("#onScreenInit: creative {}", creative);
//
//            //            boolean dtLoaded = ModList.get().isLoaded("doggytalents");
//            Minecraft mc = Minecraft.getInstance();
//            int width = mc.getWindow().getGuiScaledWidth();
//            int height = mc.getWindow().getGuiScaledHeight();
//            int sizeX = creative ? 195 : 176;
//            int sizeY = creative ? 136 : 166;
//            int guiLeft = (width - sizeX) / 2 - ((creative) ? 15 : 0);
//            int guiTop = (height - sizeY) / 2 - ((!creative) ? 13 : 0);
//
//            int x = guiLeft + (creative ? 36 : sizeX / 2 - 10);
//            int y = guiTop + (creative ? 7 : 48);
//
//            event.addListener(new SmallButton(x, y, new TranslatableComponent("X"), (btn) -> {
//                CraftTracker.LOGGER.debug("#onScreenInit: SMALL BUTTON PRESSED {}", btn);
////                PacketHandler.send(PacketDistributor.SERVER.noArg(), new AddToQueueData("TODO"));
//////                btn.active = false;
//            }));
//        }
    }

    @SubscribeEvent
    public void onScreenDrawForeground(final ScreenEvent.DrawScreenEvent event) {
        CraftTracker.LOGGER.trace("#onScreenDrawForeground: {}", event);

        Screen screen = event.getScreen();
        if(screen instanceof InventoryScreen || screen instanceof CreativeModeInventoryScreen) {
            boolean creative = screen instanceof CreativeModeInventoryScreen;
//            CraftTracker.LOGGER.debug("#onScreenInit: creative {}", creative);

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

//    public void drawSelectionBox(PoseStack matrixStackIn, Player player, float particleTicks, AABB boundingBox) {
//        CraftTracker.LOGGER.debug("#drawSelectionBox: {}, player: {}", matrixStackIn, player);
//
//        RenderSystem.setShader(GameRenderer::getPositionTexShader);
//        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//        // RenderSystem.disableAlphaTest();
//        RenderSystem.depthMask(false);
//        RenderSystem.enableBlend();
//        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
//        RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 0.7F);
//        //TODO Used when drawing outline of bounding box
//        RenderSystem.lineWidth(2.0F);
//
//
//        RenderSystem.disableTexture();
//        Vec3 vec3d = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
//        double d0 = vec3d.x();
//        double d1 = vec3d.y();
//        double d2 = vec3d.z();
//
//        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
//        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
//        LevelRenderer.renderLineBox(matrixStackIn, bufferbuilder, boundingBox.move(-d0, -d1, -d2), 1F, 1F, 0F, 0.8F);
//        Tesselator.getInstance().end();
//        RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 0.3F);
//        RenderSystem.depthMask(true);
//        RenderSystem.enableTexture();
//        RenderSystem.disableBlend();
//        //RenderSystem.enableAlphaTest();
//        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//    }

}
