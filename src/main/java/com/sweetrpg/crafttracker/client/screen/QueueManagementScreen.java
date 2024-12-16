package com.sweetrpg.crafttracker.client.screen;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.lib.CTRuntime;
import com.sweetrpg.crafttracker.common.lib.Constants;
import com.sweetrpg.crafttracker.common.manager.CraftingQueueManager;
import com.sweetrpg.crafttracker.common.model.CraftingQueueProduct;
import com.sweetrpg.crafttracker.common.network.PacketHandler;
import com.sweetrpg.crafttracker.common.network.packet.data.AdvancementData;
import com.sweetrpg.crafttracker.common.registry.ModAdvancements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

import static com.sweetrpg.crafttracker.common.lib.CTRuntime.OverlayState.SUPPRESS;

public class QueueManagementScreen extends Screen {

    public final Player player;

    public static final int TITLE_COLOR = 0xbbbbbbbb;
    public static final int TITLE_HEIGHT = 20;
    public static final int ITEM_COLOR = 0xffffffff;
    public static final int ITEM_HEIGHT = 20;
    public static final int BUTTON_SIZE = ITEM_HEIGHT - 2;
    public static final int BACKGROUND_COLOR = 0x66666666;
    public static final int ITEM_X_ICON_OFFSET = 2;
    public static final int ITEM_X_TEXT_OFFSET = 20;
    public static final int ITEM_X_DELETE_BUTTON_OFFSET = -(2 + BUTTON_SIZE);
    public static final int ITEM_X_UP_BUTTON_OFFSET = ITEM_X_DELETE_BUTTON_OFFSET - BUTTON_SIZE - 2;
    public static final int ITEM_X_QTY_WIDTH = 30;
    public static final int ITEM_X_QTY_OFFSET = ITEM_X_UP_BUTTON_OFFSET - (ITEM_X_QTY_WIDTH / 2);
    public static final int ITEM_X_DOWN_BUTTON_OFFSET = ITEM_X_QTY_OFFSET - (int) (BUTTON_SIZE * 2) - 2;

    private List<CraftingQueueProduct> productItems;
    private CTRuntime.OverlayState queueState;
    private CTRuntime.OverlayState shoppingState;

    public QueueManagementScreen(Player player) {
        super(Component.translatable(Constants.TRANSLATION_KEY_GUI_QUEUE_MGR_TITLE));
        this.player = player;

        this.productItems = CraftingQueueManager.INSTANCE.getEndProducts();
    }

    public static void open() {
        Minecraft mc = Minecraft.getInstance();
        mc.setScreen(new QueueManagementScreen(mc.player));
    }

    @Override
    public void init() {
        super.init();

//        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);

        // hide queue overlay and shopping list
        this.queueState = CTRuntime.INSTANCE.queueOverlayRequestedState;
        CTRuntime.INSTANCE.queueOverlayRequestedState = SUPPRESS;
        this.shoppingState = CTRuntime.INSTANCE.shoppingOverlayRequestedState;
        CTRuntime.INSTANCE.shoppingOverlayRequestedState = SUPPRESS;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {

        int width = Math.max(200, this.width / 3);
        int height = this.height - 100;
        int topX = (this.width / 2) - (width / 2);
        int topY = 20; // (this.height / 2) - (height / 2);

        this.renderBackground(graphics);

        // title
        graphics.drawCenteredString(this.font, I18n.get(Constants.TRANSLATION_KEY_GUI_QUEUE_MGR_TITLE), this.width / 2, topY + 2, TITLE_COLOR);

        // products
        for(int i = 0; i < this.productItems.size(); i++) {
            var pItem = this.productItems.get(i);
            final var itemIndex = i;

            var item = ForgeRegistries.ITEMS.getValue(pItem.getProductId());
            var itemStack = item.getDefaultInstance();
            var y = topY + TITLE_HEIGHT + (i * (ITEM_HEIGHT + 6));

            if(y + ITEM_HEIGHT > height) {
                CraftTracker.LOGGER.debug("too many items for display (stopping at item {}, y {}", i, y);
                break;
            }

            // background
            graphics.fill(topX, y, topX + width, y + ITEM_HEIGHT + 4, BACKGROUND_COLOR);

            // icon
            graphics.renderItem(itemStack, topX + ITEM_X_ICON_OFFSET, y + 4);
//            var drawable = CTPlugin.jeiRuntime.getJeiHelpers().getGuiHelper()
//                    .createDrawableIngredient(VanillaTypes.ITEM_STACK, itemStack);
//            drawable.draw(graphics, topX + ITEM_X_ICON_OFFSET, y + 2);

            // name
            graphics.drawString(this.font, item.getDescription(), topX + ITEM_X_TEXT_OFFSET, y + 9, ITEM_COLOR);

            // quantity and adjustment buttons
            {
                Button button = Button.builder(Component.literal("-"), btn -> {
                            CraftingQueueManager.INSTANCE.adjustProduct(player, pItem.getProductId(), -1);
                            QueueManagementScreen.this.productItems = CraftingQueueManager.INSTANCE.getEndProducts();
                        })
                        .pos(topX + width + ITEM_X_DOWN_BUTTON_OFFSET, y + 2)
                        .size(BUTTON_SIZE, BUTTON_SIZE + 2)
                        .build();
                        /*{
                    @Override
                    public void renderToolTip(PoseStack pPoseStack, int pMouseX, int pMouseY) {
                        QueueManagementScreen.this.renderTooltip(poseStack, new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_QUEUEMGR_DEC_BUTTON_TOOLTIP), mouseX, mouseY);
                    }
                }*/
                ;
                button.active = pItem.getIterations() > 1;
                this.addRenderableWidget(button);
            }
            {
                var text = String.format("%d", pItem.getIterations());
                graphics.drawCenteredString(this.font, text, topX + width + ITEM_X_QTY_OFFSET, y + 9, ITEM_COLOR);
            }
            {
                Button button = Button.builder(Component.literal("+"), btn -> {
                            CraftingQueueManager.INSTANCE.adjustProduct(player, pItem.getProductId(), 1);
                            QueueManagementScreen.this.productItems = CraftingQueueManager.INSTANCE.getEndProducts();
                        })
                        .pos(topX + width + ITEM_X_UP_BUTTON_OFFSET, y + 2)
                        .size(BUTTON_SIZE, BUTTON_SIZE + 2)
                        .build();
                        /*{
                    @Override
                    public void renderToolTip(PoseStack pPoseStack, int pMouseX, int pMouseY) {
                        QueueManagementScreen.this.renderTooltip(poseStack, new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_QUEUEMGR_INC_BUTTON_TOOLTIP), mouseX, mouseY);
                    }
                }*/
                ;
                this.addRenderableWidget(button);
            }

            // variations
            // TODO

            // delete button
            {
                Button button = Button.builder(Component.literal("x"), btn -> {
                            CraftingQueueManager.INSTANCE.removeProduct(player, pItem.getProductId());
                            QueueManagementScreen.this.productItems = CraftingQueueManager.INSTANCE.getEndProducts();
                            this.renderables.clear();
                        })
                        .pos(topX + width + ITEM_X_DELETE_BUTTON_OFFSET, y + 2)
                        .size(BUTTON_SIZE, BUTTON_SIZE + 2)
                        .build();
                /*{
                    @Override
                    public void renderToolTip(PoseStack pPoseStack, int pMouseX, int pMouseY) {
                        QueueManagementScreen.this.renderTooltip(poseStack, new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_QUEUEMGR_DEL_BUTTON_TOOLTIP), mouseX, mouseY);
                    }
                }*/
                ;
                this.addRenderableWidget(button);
            }
        }

        // clear all button
        {
            Button button = Button.builder(Component.translatable(Constants.TRANSLATION_KEY_GUI_QUEUE_MGR_CLEAR_BUTTON),
                            btn -> {
                                CraftingQueueManager.INSTANCE.removeAll();
                                QueueManagementScreen.this.productItems = CraftingQueueManager.INSTANCE.getEndProducts();
                                this.renderables.clear();

                                // send advancement packet
                                PacketHandler.send(PacketDistributor.SERVER.noArg(), new AdvancementData(ModAdvancements.Key.CLEAR_QUEUE));
                            })
                    .pos(topX + (width / 2) - 50, topY + height - BUTTON_SIZE - 4)
                    .size(100, BUTTON_SIZE + 2)
                    .build();
                    /*{
                @Override
                public void renderToolTip(PoseStack pPoseStack, int pMouseX, int pMouseY) {
                    QueueManagementScreen.this.renderTooltip(poseStack, new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_QUEUEMGR_CLEAR_BUTTON_TOOLTIP), mouseX, mouseY);
                }
            }*/
            ;
            this.addRenderableWidget(button);
        }

        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void removed() {
        super.removed();

//        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);

        // restore queue overlay and shopping list
        CTRuntime.INSTANCE.queueOverlayRequestedState = this.queueState;
        CTRuntime.INSTANCE.shoppingOverlayRequestedState = this.shoppingState;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

}
