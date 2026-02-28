package me.themfcraft.rpengine.economy;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.themfcraft.rpengine.RPEngine;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.ItemStack;

public class ShopManager {

    private final Map<String, Shop> shops = new HashMap<>();
    private final Map<UUID, String> npcShops = new HashMap<>();

    public void registerShop(Shop shop) {
        shops.put(shop.getId(), shop);
    }

    public synchronized void createShop(String id, String name) {
        Shop shop = new Shop(id, name);
        registerShop(shop);
        saveShop(shop);
    }

    public synchronized void addItemToShop(String shopId, ItemStack stack, int price) {
        Shop shop = getShop(shopId);
        if (shop != null) {
            shop.getItems().add(new ShopItem(stack, (double) price, true));
            saveShopItem(shopId, stack, price);
        }
    }

    public Shop getShop(String id) {
        return shops.get(id);
    }

    public void registerNpcShop(UUID npcId, String shopId) {
        npcShops.put(npcId, shopId);
        saveNpcShop(npcId, shopId);
    }

    public void saveShop(Shop shop) {
        try (PreparedStatement pstmt = RPEngine.getDatabaseManager().getConnection().prepareStatement("INSERT OR REPLACE INTO shops (id, name) VALUES (?, ?)")) {
            pstmt.setString(1, shop.getId());
            pstmt.setString(2, shop.getDisplayName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            RPEngine.getLogger().error("Failed to save shop " + shop.getId(), e);
        }
    }

    public void saveShopItem(String shopId, ItemStack stack, int price) {
        try (PreparedStatement pstmt = RPEngine.getDatabaseManager().getConnection().prepareStatement("INSERT INTO shop_items (shop_id, item_nbt, price) VALUES (?, ?, ?)")) {
            pstmt.setString(1, shopId);
            pstmt.setString(2, stack.save(new CompoundTag()).toString());
            pstmt.setInt(3, price);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            RPEngine.getLogger().error("Failed to save shop item for " + shopId, e);
        }
    }

    public void saveNpcShop(UUID npcId, String shopId) {
        try (PreparedStatement pstmt = RPEngine.getDatabaseManager().getConnection().prepareStatement("INSERT OR REPLACE INTO npc_shops (npc_uuid, shop_id) VALUES (?, ?)")) {
            pstmt.setString(1, npcId.toString());
            pstmt.setString(2, shopId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            RPEngine.getLogger().error("Failed to save NPC shop " + shopId, e);
        }
    }

    public void loadAll() {
        shops.clear();
        npcShops.clear();
        try {
            // Load Shops
            try (PreparedStatement pstmt = RPEngine.getDatabaseManager().getConnection().prepareStatement("SELECT * FROM shops");
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String id = rs.getString("id");
                    String name = rs.getString("name");
                    shops.put(id, new Shop(id, name));
                }
            }

            // Load Shop Items
            try (PreparedStatement pstmt = RPEngine.getDatabaseManager().getConnection().prepareStatement("SELECT * FROM shop_items");
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Shop shop = getShop(rs.getString("shop_id"));
                    if (shop != null) {
                        try {
                            CompoundTag nbt = TagParser.parseTag(rs.getString("item_nbt"));
                            ItemStack stack = ItemStack.of(nbt);
                            shop.getItems().add(new ShopItem(stack, (double) rs.getInt("price"), true));
                        } catch (CommandSyntaxException e) {
                            RPEngine.getLogger().error("Failed to parse shop item NBT", e);
                        }
                    }
                }
            }

            // Load NPC Shops
            try (PreparedStatement pstmt = RPEngine.getDatabaseManager().getConnection().prepareStatement("SELECT * FROM npc_shops");
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    npcShops.put(UUID.fromString(rs.getString("npc_uuid")), rs.getString("shop_id"));
                }
            }
        } catch (SQLException e) {
            RPEngine.getLogger().error("Failed to load shops/items", e);
        }
    }

    public Shop getShopByNpc(UUID npcId) {
        String shopId = npcShops.get(npcId);
        return shopId != null ? getShop(shopId) : null;
    }

    public Map<String, Shop> getShops() {
        return shops;
    }
}
