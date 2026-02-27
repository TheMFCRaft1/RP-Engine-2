package me.themfcraft.rpengine.attributes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import me.themfcraft.rpengine.RPEngine;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class AttributeManager {

    private static final Logger LOGGER = LogUtils.getLogger();
    private final Map<UUID, Integer> strengthMap = new HashMap<>();
    private final Map<UUID, Integer> staminaMap = new HashMap<>();

    private static final UUID STRENGTH_MODIFIER_UUID = UUID.fromString("1f4a9b3c-5d6e-7f8a-9b1c-2d4e6f8a0b1c");
    private static final UUID STAMINA_MODIFIER_UUID = UUID.fromString("2d5b8c4d-6e7f-8a9b-0c2d-3e5f7a9b1c2d");

    public void loadAttributes(ServerPlayer player) {
        UUID uuid = player.getUUID();
        CompletableFuture.runAsync(() -> {
            try (Connection conn = RPEngine.getDatabaseManager().getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("SELECT strength, stamina FROM attributes WHERE uuid = ?")) {
                
                pstmt.setString(1, uuid.toString());
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    int strength = rs.getInt("strength");
                    int stamina = rs.getInt("stamina");
                    strengthMap.put(uuid, strength);
                    staminaMap.put(uuid, stamina);
                } else {
                    // Default values
                    strengthMap.put(uuid, 10);
                    staminaMap.put(uuid, 10);
                    saveAttributes(uuid); // Initialize in DB
                }
                
                // Apply vanilla attributes on the main thread
                player.getServer().execute(() -> applyVanillaAttributes(player));
                
            } catch (SQLException e) {
                LOGGER.error("Failed to load attributes for player " + player.getName().getString(), e);
            }
        });
    }

    public void saveAttributes(UUID uuid) {
        int strength = getStrength(uuid);
        int stamina = getStamina(uuid);
        
        CompletableFuture.runAsync(() -> {
            try (Connection conn = RPEngine.getDatabaseManager().getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("INSERT OR REPLACE INTO attributes (uuid, strength, stamina) VALUES (?, ?, ?)")) {
                
                pstmt.setString(1, uuid.toString());
                pstmt.setInt(2, strength);
                pstmt.setInt(3, stamina);
                pstmt.executeUpdate();
                
            } catch (SQLException e) {
                LOGGER.error("Failed to save attributes for player", e);
            }
        });
    }

    public int getStrength(UUID uuid) {
        return strengthMap.getOrDefault(uuid, 10);
    }

    public void setStrength(ServerPlayer player, int value) {
        strengthMap.put(player.getUUID(), Math.max(1, value));
        saveAttributes(player.getUUID());
        applyVanillaAttributes(player);
    }

    public int getStamina(UUID uuid) {
        return staminaMap.getOrDefault(uuid, 10);
    }

    public void setStamina(ServerPlayer player, int value) {
        staminaMap.put(player.getUUID(), Math.max(1, value));
        saveAttributes(player.getUUID());
        applyVanillaAttributes(player);
    }

    private void applyVanillaAttributes(ServerPlayer player) {
        int strength = getStrength(player.getUUID());
        int stamina = getStamina(player.getUUID());

        // Strength affects Max Health (default 20.0). 10 is baseline.
        // Let's say every point above 10 gives +0.5 max health. Every point below gives -0.5.
        AttributeInstance healthAttr = player.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttr != null) {
            healthAttr.removeModifier(STRENGTH_MODIFIER_UUID);
            double healthBonus = (strength - 10) * 0.5;
            if (healthBonus != 0) {
                healthAttr.addPermanentModifier(new AttributeModifier(STRENGTH_MODIFIER_UUID, "Strength Health Bonus", healthBonus, AttributeModifier.Operation.ADDITION));
            }
            if (player.getHealth() > player.getMaxHealth()) {
                player.setHealth(player.getMaxHealth());
            }
        }

        // Stamina affects Movement Speed (default ~0.1). 10 is baseline.
        // Let's say every point above/below 10 changes speed by 1%.
        AttributeInstance speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            speedAttr.removeModifier(STAMINA_MODIFIER_UUID);
            double speedBonus = (stamina - 10) * 0.01;
            if (speedBonus != 0) {
                speedAttr.addPermanentModifier(new AttributeModifier(STAMINA_MODIFIER_UUID, "Stamina Speed Bonus", speedBonus, AttributeModifier.Operation.MULTIPLY_TOTAL));
            }
        }
    }
}
