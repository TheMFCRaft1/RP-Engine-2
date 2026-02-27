package me.themfcraft.rpengine.network;

import java.util.function.Supplier;

import me.themfcraft.rpengine.RPEngine;
import me.themfcraft.rpengine.economy.Shop;
import me.themfcraft.rpengine.economy.ShopScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class OpenShopPacket {

    private final String shopId;

    public OpenShopPacket(String shopId) {
        this.shopId = shopId;
    }

    public OpenShopPacket(FriendlyByteBuf buf) {
        this.shopId = buf.readUtf();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(this.shopId);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Shop shop = RPEngine.getShopManager().getShop(shopId);
            if (shop != null) {
                Minecraft.getInstance().setScreen(new ShopScreen(shop));
            }
        });
        return true;
    }
}
