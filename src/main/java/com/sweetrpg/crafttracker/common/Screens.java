package com.sweetrpg.crafttracker.common;

import com.sweetrpg.crafttracker.CraftTracker;
import net.minecraft.server.level.ServerPlayer;

public class Screens {

//    public static class PackCatContainerProvider implements MenuProvider {
//
//        private AbstractCatEntity cat;
//
//        public PackCatContainerProvider(AbstractCatEntity catIn) {
//            this.cat = catIn;
//        }
//
//        @Override
//        public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
//            return new PackCatContainer(windowId, inventory, this.cat);
//        }
//
//        @Override
//        public Component getDisplayName() {
//            return new TranslatableComponent("container.crafttracker.pack_cat");
//        }
//    }
//
//    public static void openPackCatScreen(ServerPlayer player, AbstractCatEntity catIn) {
//        if (catIn.isAlive()) {
//            NetworkHooks.openGui(player, new PackCatContainerProvider(catIn), (buf) -> {
//                buf.writeInt(catIn.getId());
//            });
//        }
//    }

    public static void updateCraftQueue(ServerPlayer player) {
        CraftTracker.LOGGER.debug("Screens#updateCraftQueue: {}", player);

        // TODO: display if hidden

//        CraftQueueOverlay.CRAFT_QUEUE.render();
        // redraw list of items
    }

}
