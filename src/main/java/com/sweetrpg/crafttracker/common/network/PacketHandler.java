package com.sweetrpg.crafttracker.common.network;

import com.sweetrpg.crafttracker.CraftTracker;
import com.sweetrpg.crafttracker.common.network.packet.AddToQueuePacket;
import com.sweetrpg.crafttracker.common.network.packet.ToggleCraftListPacket;
import com.sweetrpg.crafttracker.common.network.packet.ToggleShoppingListPacket;
import com.sweetrpg.crafttracker.common.network.packet.data.AddToQueueData;
import com.sweetrpg.crafttracker.common.network.packet.data.ToggleCraftListData;
import com.sweetrpg.crafttracker.common.network.packet.data.ToggleShoppingListData;
import net.minecraftforge.network.PacketDistributor;

public final class PacketHandler {

    private static int disc = 0;

    public static void init() {
        registerPacket(new AddToQueuePacket(), AddToQueueData.class);
        registerPacket(new ToggleCraftListPacket(), ToggleCraftListData.class);
        registerPacket(new ToggleShoppingListPacket(), ToggleShoppingListData.class);
//        registerPacket(new CatNamePacket(), CatNameData.class);
//        registerPacket(new CatObeyPacket(), CatObeyData.class);
//        registerPacket(new CatTalentPacket(), CatTalentData.class);
//        //registerPacket(new CatTexturePacket(), CatTextureData.class);
//        registerPacket(new FriendlyFirePacket(), FriendlyFireData.class);
//        registerPacket(new SendSkinPacket(), SendSkinData.class);
//        registerPacket(new RequestSkinPacket(), RequestSkinData.class);
//        registerPacket(new OpenCatScreenPacket(), OpenCatScreenData.class);
//        registerPacket(new CatInventoryPagePacket(), CatInventoryPageData.class);
//        registerPacket(new CatTexturePacket(), CatTextureData.class);
    }

    public static <MSG> void send(PacketDistributor.PacketTarget target, MSG message) {
        CraftTracker.HANDLER.send(target, message);
    }

    public static <D> void registerPacket(IPacket<D> packet, Class<D> dataClass) {
        CraftTracker.HANDLER.registerMessage(PacketHandler.disc++, dataClass, packet::encode, packet::decode, packet::handle);
    }
}
