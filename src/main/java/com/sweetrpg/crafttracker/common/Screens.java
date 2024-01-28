package com.sweetrpg.crafttracker.common;

import com.sweetrpg.crafttracker.common.registry.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;

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


}
