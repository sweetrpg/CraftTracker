package com.sweetrpg.crafttracker.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.addon.jei.CTPlugin;
import com.sweetrpg.crafttracker.common.lib.CTRuntime;
import com.sweetrpg.crafttracker.common.lib.Constants;
import com.sweetrpg.crafttracker.common.manager.CraftingQueueManager;
import mezz.jei.api.constants.VanillaTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

import static com.sweetrpg.crafttracker.common.lib.CTRuntime.OverlayState.SUPPRESS;

public class QueueManagementScreen extends Screen {

    public final Player player;

    public static final int TITLE_COLOR = 0xbbbbbbbb;
    public static final int TITLE_HEIGHT = 20;
    public static final int ITEM_COLOR = 0xffffffff;
    public static final int ITEM_HEIGHT = 18;
    public static final int BUTTON_SIZE = ITEM_HEIGHT - 2;
    public static final int BACKGROUND_COLOR = 0x66666666;
    public static final int ITEM_X_ICON_OFFSET = 2;
    public static final int ITEM_X_TEXT_OFFSET = 20;
    public static final int ITEM_X_DELETE_BUTTON_OFFSET = -(2 + BUTTON_SIZE);
    public static final int ITEM_X_UP_BUTTON_OFFSET = ITEM_X_DELETE_BUTTON_OFFSET - BUTTON_SIZE - 2;
    public static final int ITEM_X_QTY_WIDTH = 30;
    public static final int ITEM_X_QTY_OFFSET = ITEM_X_UP_BUTTON_OFFSET - (ITEM_X_QTY_WIDTH / 2);
    public static final int ITEM_X_DOWN_BUTTON_OFFSET = ITEM_X_QTY_OFFSET - (int) (BUTTON_SIZE * 2) - 2;

    private List<CraftingQueueManager.ProductItem> productItems;
    private CTRuntime.OverlayState queueState;
    private CTRuntime.OverlayState shoppingState;

    public QueueManagementScreen(Player player) {
        super(new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_QUEUE_MGR_TITLE));
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

        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);

        int topX = this.width / 2;
        int topY = this.height / 2;

        // hide queue overlay and shopping list
        this.queueState = CTRuntime.INSTANCE.queueOverlayRequestedState;
        CTRuntime.INSTANCE.queueOverlayRequestedState = SUPPRESS;
        this.shoppingState = CTRuntime.INSTANCE.shoppingOverlayRequestedState;
        CTRuntime.INSTANCE.shoppingOverlayRequestedState = SUPPRESS;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {

        int width = Math.max(200, this.width / 3);
        int height = this.height - 100;
        int topX = (this.width / 2) - (width / 2);
        int topY = 20; // (this.height / 2) - (height / 2);

        this.renderBackground(poseStack);

        // title
        GuiComponent.drawCenteredString(poseStack, this.font, I18n.get(Constants.TRANSLATION_KEY_GUI_QUEUE_MGR_TITLE), this.width / 2, topY + 2, TITLE_COLOR);
        topY += TITLE_HEIGHT;

        // products
        for(int i = 0; i < this.productItems.size(); i++) {
            var pItem = this.productItems.get(i);
//            final var itemIndex = i;

            var item = ForgeRegistries.ITEMS.getValue(pItem.getItemId());
            var itemStack = item.getDefaultInstance();
            var y = topY; // + TITLE_HEIGHT + (i * (ITEM_HEIGHT + 4));

            if(y + ITEM_HEIGHT > height) {
                CraftTracker.LOGGER.debug("too many items for display (stopping at item {}, y {}", i, y);
                break;
            }

            // background
            var rowHeight = ITEM_HEIGHT + 2;
            if(pItem.getMethods().size() > 1) {
                // increase row height if there are multiple ways to craft the item
                rowHeight += ITEM_HEIGHT;
            }
            GuiComponent.fill(poseStack, topX, y, topX + width, y + rowHeight, BACKGROUND_COLOR);

            // icon
            var drawable = CTPlugin.jeiRuntime.getJeiHelpers().getGuiHelper()
                    .createDrawableIngredient(VanillaTypes.ITEM_STACK, itemStack);
            drawable.draw(poseStack, topX + ITEM_X_ICON_OFFSET, y + 2);

            // name
            this.font.draw(poseStack, item.getDescription(), topX + ITEM_X_TEXT_OFFSET, y + 6, ITEM_COLOR);

            // quantity and adjustment buttons
            {
                Button button = new Button(topX + width + ITEM_X_DOWN_BUTTON_OFFSET, y + 2, BUTTON_SIZE, BUTTON_SIZE, new TextComponent("-"), btn -> {
                    CraftingQueueManager.INSTANCE.adjustProduct(player, pItem.getItemId(), -1);
                    QueueManagementScreen.this.productItems = CraftingQueueManager.INSTANCE.getEndProducts();
                }) /*{
                    @Override
                    public void renderToolTip(PoseStack pPoseStack, int pMouseX, int pMouseY) {
                        QueueManagementScreen.this.renderTooltip(poseStack, new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_QUEUEMGR_DEC_BUTTON_TOOLTIP), mouseX, mouseY);
                    }
                }*/;
                button.active = pItem.getQuantity() > 1;
                this.addRenderableWidget(button);
            }
            {
                var text = String.format("%d", pItem.getQuantity());
//                this.font.draw(poseStack, text, topX + width + ITEM_X_QTY_OFFSET, y + 6, ITEM_COLOR);
                GuiComponent.drawCenteredString(poseStack, this.font, text, topX + width + ITEM_X_QTY_OFFSET, y + 6, ITEM_COLOR);
            }
            {
                Button button = new Button(topX + width + ITEM_X_UP_BUTTON_OFFSET, y + 2, BUTTON_SIZE, BUTTON_SIZE - 2, new TextComponent("+"), btn -> {
                    CraftingQueueManager.INSTANCE.adjustProduct(player, pItem.getItemId(), 1);
                    QueueManagementScreen.this.productItems = CraftingQueueManager.INSTANCE.getEndProducts();
                }) /*{
                    @Override
                    public void renderToolTip(PoseStack pPoseStack, int pMouseX, int pMouseY) {
                        QueueManagementScreen.this.renderTooltip(poseStack, new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_QUEUEMGR_INC_BUTTON_TOOLTIP), mouseX, mouseY);
                    }
                }*/;
                this.addRenderableWidget(button);
            }

            // methods
            if(pItem.getMethods().size() > 1) {
                for(int j = 0; j < pItem.getMethods().size(); j++) {
                    var method = pItem.getMethods().get(j);
                    CraftTracker.LOGGER.debug("methods: {}, {} ({})", j, method, method.getGroup());

                    var methodItem = ForgeRegistries.ITEMS.getValue(method.getId());
                    var methodItemStack = methodItem.getDefaultInstance();

                    // icon
                    var methodDrawable = CTPlugin.jeiRuntime.getJeiHelpers().getGuiHelper()
                            .createDrawableIngredient(VanillaTypes.ITEM_STACK, methodItemStack);
                    methodDrawable.draw(poseStack, topX + ITEM_X_ICON_OFFSET + 20 + (j * BUTTON_SIZE + 2), y + ITEM_HEIGHT + 2);
                }
            }

            // delete button
            {
                Button button = new Button(topX + width + ITEM_X_DELETE_BUTTON_OFFSET, y + 2, BUTTON_SIZE, BUTTON_SIZE - 2, new TextComponent("x"), btn -> {
                    CraftingQueueManager.INSTANCE.removeProduct(player, pItem.getItemId());
                    QueueManagementScreen.this.productItems = CraftingQueueManager.INSTANCE.getEndProducts();
                    this.renderables.clear();
                }) /*{
                    @Override
                    public void renderToolTip(PoseStack pPoseStack, int pMouseX, int pMouseY) {
                        QueueManagementScreen.this.renderTooltip(poseStack, new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_QUEUEMGR_DEL_BUTTON_TOOLTIP), mouseX, mouseY);
                    }
                }*/;
                this.addRenderableWidget(button);
            }

            topY += rowHeight + 2;
        }

        // clear all button
        {
            Button button = new Button(topX + (width / 2) - 50, topY + height - BUTTON_SIZE - 4, 100, BUTTON_SIZE + 2,
                    new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_QUEUE_MGR_CLEAR_BUTTON),
                    btn -> {
                        CraftingQueueManager.INSTANCE.removeAll();
                        QueueManagementScreen.this.productItems = CraftingQueueManager.INSTANCE.getEndProducts();
                        this.renderables.clear();
                    }) /*{
                @Override
                public void renderToolTip(PoseStack pPoseStack, int pMouseX, int pMouseY) {
                    QueueManagementScreen.this.renderTooltip(poseStack, new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_QUEUEMGR_CLEAR_BUTTON_TOOLTIP), mouseX, mouseY);
                }
            }*/;
            this.addRenderableWidget(button);
        }

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void removed() {
        super.removed();

        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);

        // restore queue overlay and shopping list
        CTRuntime.INSTANCE.queueOverlayRequestedState = this.queueState;
        CTRuntime.INSTANCE.shoppingOverlayRequestedState = this.shoppingState;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

}
