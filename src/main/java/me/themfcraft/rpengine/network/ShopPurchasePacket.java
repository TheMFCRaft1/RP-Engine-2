package me.themfcraft.rpengine.network;

import me.themfcraft.rpengine.RPEngine;
import me.themfcraft.rpengine.economy.Shop;
import me.themfcraft.rpengine.economy.ShopItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ShopPurchasePacket {

    private final String shopId;
    private final int itemIndex;

    public ShopPurchasePacket(String shopId, int itemIndex) {
        this.shopId = shopId;
        this.itemIndex = itemIndex;
    }

    public ShopPurchasePacket(FriendlyByteBuf buf) {
        this.shopId = buf.readUtf();
        this.itemIndex = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(this.shopId);
        buf.writeInt(this.itemIndex);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }

            Shop shop = RPEngine.getShopManager().getShop(shopId);
            if (shop == null) {
                return;
            }

            if (itemIndex < 0 || itemIndex >= shop.getItems().size()) {
                return;
            }
            ShopItem shopItem = shop.getItems().get(itemIndex);

            double cost = shopItem.price();
            if (RPEngine.getEconomyManager().getCash(player.getUUID()) >= cost) {
                RPEngine.getEconomyManager().removeCash(player.getUUID(), cost);
                player.getInventory().add(shopItem.item().copy());
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aGekauft: " + shopItem.item().getHoverName().getString() + " für $" + cost));

                RPEngine.getEconomyManager().saveEconomy(player.getUUID());
            } else {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cDu hast nicht genug Bargeld!"));
            }
        });
        return true;
    }
}
