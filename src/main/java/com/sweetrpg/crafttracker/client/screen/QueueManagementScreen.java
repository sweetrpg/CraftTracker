package com.sweetrpg.crafttracker.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.Constants;
import com.sweetrpg.crafttracker.common.Runtime;
import com.sweetrpg.crafttracker.common.manager.CraftingQueueManager;
import com.sweetrpg.crafttracker.common.model.CraftingQueueProduct;
import com.sweetrpg.crafttracker.common.network.PacketHandler;
import com.sweetrpg.crafttracker.common.network.packet.data.AdvancementData;
import com.sweetrpg.crafttracker.common.registry.ModAdvancements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.sweetrpg.crafttracker.common.Runtime.OverlayState.SUPPRESS;

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

    private static final int PICKER_GAP = 10;
    private static final int PICKER_ITEM_HEIGHT = 20;
    private static final int PICKER_MAX_VISIBLE = 8;
    private static final int PICKER_MIN_WIDTH = 80;

    private List<CraftingQueueProduct> productItems;
    private Runtime.OverlayState queueState;
    private Runtime.OverlayState shoppingState;
    private EditBox searchBox;
    private List<ResourceLocation> filteredItems;
    private int itemScrollOffset;

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

        // hide queue overlay and shopping list
        this.queueState = Runtime.INSTANCE.queueOverlayRequestedState;
        Runtime.INSTANCE.queueOverlayRequestedState = SUPPRESS;
        this.shoppingState = Runtime.INSTANCE.shoppingOverlayRequestedState;
        Runtime.INSTANCE.shoppingOverlayRequestedState = SUPPRESS;

        // item picker setup
        int queueWidth = Math.max(200, this.width / 3);
        int pickerX = (this.width / 2) + (queueWidth / 2) + PICKER_GAP;
        int pickerWidth = this.width - pickerX - PICKER_GAP;
        if (pickerWidth > PICKER_MIN_WIDTH) {
            this.searchBox = new EditBox(this.font, pickerX, 20 + TITLE_HEIGHT + 4, pickerWidth, 16, new TextComponent(""));
            this.searchBox.setMaxLength(50);
            this.searchBox.setResponder(text -> {
                this.itemScrollOffset = 0;
                this.updateFilteredItems(text);
            });
            addRenderableWidget(this.searchBox);
        }
        this.itemScrollOffset = 0;
        this.updateFilteredItems("");
    }

    private void updateFilteredItems(String search) {
        String lower = search.toLowerCase();
        this.filteredItems = ForgeRegistries.ITEMS.getKeys().stream()
                .filter(id -> lower.isEmpty()
                        || id.toString().contains(lower)
                        || ForgeRegistries.ITEMS.getValue(id).getDescription().getString().toLowerCase().contains(lower))
                .sorted(Comparator.comparing(ResourceLocation::toString))
                .collect(Collectors.toList());
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {

        int width = Math.max(200, this.width / 3);
        int height = this.height - 100;
        int topX = (this.width / 2) - (width / 2);
        int topY = 20; // (this.height / 2) - (height / 2);

        // Re-anchor searchBox after renderables.clear() from button handlers
        if (this.searchBox != null && !this.renderables.contains(this.searchBox)) {
            addRenderableWidget(this.searchBox);
        }

        this.renderBackground(poseStack);

        // title
        GuiComponent.drawCenteredString(poseStack, this.font, I18n.get(Constants.TRANSLATION_KEY_GUI_QUEUE_MGR_TITLE), this.width / 2, topY + 2, TITLE_COLOR);

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
            GuiComponent.fill(poseStack, topX, y, topX + width, y + ITEM_HEIGHT + 4, BACKGROUND_COLOR);

            // icon
            ItemRenderer itemRenderer = this.minecraft.getItemRenderer();
            itemRenderer.renderAndDecorateFakeItem(itemStack, topX + ITEM_X_ICON_OFFSET, y + 3);
            itemRenderer.renderGuiItemDecorations(this.minecraft.font, itemStack, topX + ITEM_X_ICON_OFFSET, y + 3);
//            var drawable = CTPlugin.jeiRuntime.getJeiHelpers().getGuiHelper()
//                    .createDrawableIngredient(VanillaTypes.ITEM_STACK, itemStack);
//            drawable.draw(poseStack, topX + ITEM_X_ICON_OFFSET, y + 2);

            // name
            this.font.draw(poseStack, item.getDescription(), topX + ITEM_X_TEXT_OFFSET, y + 9, ITEM_COLOR);

            // quantity and adjustment buttons
            {
                Button button = new Button(topX + width + ITEM_X_DOWN_BUTTON_OFFSET, y + 2, BUTTON_SIZE, BUTTON_SIZE + 2, new TextComponent("-"), btn -> {
                    CraftingQueueManager.INSTANCE.adjustProduct(player, pItem.getProductId(), -1);
                    QueueManagementScreen.this.productItems = CraftingQueueManager.INSTANCE.getEndProducts();
                }) /*{
                    @Override
                    public void renderToolTip(PoseStack pPoseStack, int pMouseX, int pMouseY) {
                        QueueManagementScreen.this.renderTooltip(poseStack, new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_QUEUEMGR_DEC_BUTTON_TOOLTIP), mouseX, mouseY);
                    }
                }*/;
                button.active = pItem.getIterations() > 1;
                this.addRenderableWidget(button);
            }
            {
                var text = String.format("%d", pItem.getIterations());
                GuiComponent.drawCenteredString(poseStack, this.font, text, topX + width + ITEM_X_QTY_OFFSET, y + 9, ITEM_COLOR);
            }
            {
                Button button = new Button(topX + width + ITEM_X_UP_BUTTON_OFFSET, y + 2, BUTTON_SIZE, BUTTON_SIZE + 2, new TextComponent("+"), btn -> {
                    CraftingQueueManager.INSTANCE.adjustProduct(player, pItem.getProductId(), 1);
                    QueueManagementScreen.this.productItems = CraftingQueueManager.INSTANCE.getEndProducts();
                }) /*{
                    @Override
                    public void renderToolTip(PoseStack pPoseStack, int pMouseX, int pMouseY) {
                        QueueManagementScreen.this.renderTooltip(poseStack, new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_QUEUEMGR_INC_BUTTON_TOOLTIP), mouseX, mouseY);
                    }
                }*/;
                this.addRenderableWidget(button);
            }

            // variations
            // TODO

            // delete button
            {
                Button button = new Button(topX + width + ITEM_X_DELETE_BUTTON_OFFSET, y + 2, BUTTON_SIZE, BUTTON_SIZE + 2, new TextComponent("x"), btn -> {
                    CraftingQueueManager.INSTANCE.removeProduct(player, pItem.getProductId());
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
        }

        // clear all button
        {
            Button button = new Button(topX + (width / 2) - 50, topY + height - BUTTON_SIZE - 4, 100, BUTTON_SIZE + 2,
                    new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_QUEUE_MGR_CLEAR_BUTTON),
                    btn -> {
                        CraftingQueueManager.INSTANCE.removeAll();
                        QueueManagementScreen.this.productItems = CraftingQueueManager.INSTANCE.getEndProducts();
                        this.renderables.clear();

                        // send advancement packet
                        PacketHandler.sendToServer(new AdvancementData(ModAdvancements.Key.CLEAR_QUEUE));
                    }) /*{
                @Override
                public void renderToolTip(PoseStack pPoseStack, int pMouseX, int pMouseY) {
                    QueueManagementScreen.this.renderTooltip(poseStack, new TranslatableComponent(Constants.TRANSLATION_KEY_GUI_QUEUEMGR_CLEAR_BUTTON_TOOLTIP), mouseX, mouseY);
                }
            }*/;
            this.addRenderableWidget(button);
        }

        // item picker panel (right of queue)
        int pickerX = topX + width + PICKER_GAP;
        int pickerWidth = this.width - pickerX - PICKER_GAP;
        if (pickerWidth > PICKER_MIN_WIDTH && this.filteredItems != null) {
            GuiComponent.drawCenteredString(poseStack, this.font,
                    I18n.get(Constants.TRANSLATION_KEY_GUI_QUEUE_MGR_PICKER_TITLE),
                    pickerX + pickerWidth / 2, topY + 2, TITLE_COLOR);
            this.font.draw(poseStack,
                    I18n.get(Constants.TRANSLATION_KEY_GUI_QUEUE_MGR_PICKER_SEARCH),
                    pickerX, topY + TITLE_HEIGHT + 2, TITLE_COLOR);

            int listY = topY + TITLE_HEIGHT + 22; // below label + search box
            int visible = Math.min(PICKER_MAX_VISIBLE, this.filteredItems.size() - this.itemScrollOffset);
            for (int i = 0; i < visible; i++) {
                var itemId = this.filteredItems.get(i + this.itemScrollOffset);
                var item = ForgeRegistries.ITEMS.getValue(itemId);
                int rowY = listY + i * PICKER_ITEM_HEIGHT;

                GuiComponent.fill(poseStack, pickerX, rowY, pickerX + pickerWidth, rowY + PICKER_ITEM_HEIGHT, BACKGROUND_COLOR);

                ItemRenderer itemRenderer = this.minecraft.getItemRenderer();
                itemRenderer.renderAndDecorateFakeItem(item.getDefaultInstance(), pickerX + 2, rowY + 2);

                this.font.draw(poseStack, item.getDescription(), pickerX + 20, rowY + 6, ITEM_COLOR);
            }
        }

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && this.filteredItems != null) {
            int queueWidth = Math.max(200, this.width / 3);
            int pickerX = (this.width / 2) + (queueWidth / 2) + PICKER_GAP;
            int pickerWidth = this.width - pickerX - PICKER_GAP;
            int listY = 20 + TITLE_HEIGHT + 22;

            if (pickerWidth > PICKER_MIN_WIDTH && mouseX >= pickerX && mouseX < pickerX + pickerWidth
                    && mouseY >= listY) {
                int idx = (int) (mouseY - listY) / PICKER_ITEM_HEIGHT + this.itemScrollOffset;
                if (idx >= 0 && idx < this.filteredItems.size()) {
                    var itemId = this.filteredItems.get(idx);
                    CraftingQueueManager.INSTANCE.addProduct(this.player, itemId, 1);
                    this.productItems = CraftingQueueManager.INSTANCE.getEndProducts();
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (this.filteredItems != null) {
            int queueWidth = Math.max(200, this.width / 3);
            int pickerX = (this.width / 2) + (queueWidth / 2) + PICKER_GAP;
            int pickerWidth = this.width - pickerX - PICKER_GAP;
            if (pickerWidth > PICKER_MIN_WIDTH && mouseX >= pickerX) {
                int maxOffset = Math.max(0, this.filteredItems.size() - PICKER_MAX_VISIBLE);
                this.itemScrollOffset = (int) Math.max(0, Math.min(maxOffset, this.itemScrollOffset - delta));
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public void removed() {
        super.removed();

        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);

        // restore queue overlay and shopping list
        Runtime.INSTANCE.queueOverlayRequestedState = this.queueState;
        Runtime.INSTANCE.shoppingOverlayRequestedState = this.shoppingState;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

}
