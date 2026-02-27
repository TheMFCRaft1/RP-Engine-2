package me.themfcraft.rpengine.network;

import me.themfcraft.rpengine.RPEngine;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(RPEngine.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, CharacterCreationPacket.class, CharacterCreationPacket::encode, CharacterCreationPacket::new, CharacterCreationPacket::handle);
        CHANNEL.registerMessage(id++, OpenCreationScreenPacket.class, OpenCreationScreenPacket::encode, OpenCreationScreenPacket::new, OpenCreationScreenPacket::handle);
        CHANNEL.registerMessage(id++, ShopPurchasePacket.class, ShopPurchasePacket::encode, ShopPurchasePacket::new, ShopPurchasePacket::handle);
        CHANNEL.registerMessage(id++, OpenShopPacket.class, OpenShopPacket::encode, OpenShopPacket::new, OpenShopPacket::handle);
        CHANNEL.registerMessage(id++, RequestKeysPacket.class, RequestKeysPacket::encode, RequestKeysPacket::new, RequestKeysPacket::handle);
        CHANNEL.registerMessage(id++, SyncKeysPacket.class, SyncKeysPacket::encode, SyncKeysPacket::new, SyncKeysPacket::handle);
        CHANNEL.registerMessage(id++, RadialActionPacket.class, RadialActionPacket::encode, RadialActionPacket::new, RadialActionPacket::handle);
        CHANNEL.registerMessage(id++, RadioSyncPacket.class, RadioSyncPacket::encode, RadioSyncPacket::new, RadioSyncPacket::handle);
    }
}
