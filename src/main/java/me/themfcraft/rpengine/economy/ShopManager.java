package me.themfcraft.rpengine.economy;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShopManager {

    private final Map<String, Shop> shops = new HashMap<>();
    private final Map<UUID, String> npcShops = new HashMap<>();

    public void registerShop(Shop shop) {
        shops.put(shop.getId(), shop);
    }

    public Shop getShop(String id) {
        return shops.get(id);
    }

    public void registerNpcShop(UUID npcId, String shopId) {
        npcShops.put(npcId, shopId);
    }

    public Shop getShopByNpc(UUID npcId) {
        String shopId = npcShops.get(npcId);
        return shopId != null ? getShop(shopId) : null;
    }

    public Map<String, Shop> getShops() {
        return shops;
    }
}
