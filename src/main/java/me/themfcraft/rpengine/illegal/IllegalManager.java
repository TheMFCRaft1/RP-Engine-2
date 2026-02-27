package me.themfcraft.rpengine.illegal;

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
import net.minecraft.world.item.ItemStack;

public class IllegalManager {

    private static final Logger LOGGER = LogUtils.getLogger();
    private final Map<UUID, Integer> jailTimers = new HashMap<>();
    private final Map<UUID, Boolean> handcuffed = new HashMap<>();

    public boolean isIllegal(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        // Items can be marked illegal via NBT tag for simplicity in this system
        if (stack.hasTag() && stack.getTag().getBoolean("rpengine_illegal")) {
            return true;
        }

        return false;
    }

    public void makeIllegal(ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        stack.getOrCreateTag().putBoolean("rpengine_illegal", true);
    }

    public void setJailTime(UUID uuid, int seconds) {
        jailTimers.put(uuid, seconds);
        saveJailTime(uuid, seconds);
    }

    public int getJailTime(UUID uuid) {
        return jailTimers.getOrDefault(uuid, 0);
    }

    public void decreaseJailTime(UUID uuid) {
        int time = getJailTime(uuid);
        if (time > 0) {
            jailTimers.put(uuid, time - 1);
            if (time - 1 == 0) {
                jailTimers.remove(uuid);
                saveJailTime(uuid, 0);
                // The actual release teleportation will be handled in a tick event
            }
        }
    }

    public void setHandcuffed(UUID uuid, boolean isHandcuffed) {
        handcuffed.put(uuid, isHandcuffed);
    }

    public boolean isHandcuffed(UUID uuid) {
        return handcuffed.getOrDefault(uuid, false);
    }

    private void saveJailTime(UUID uuid, int time) {
        CompletableFuture.runAsync(() -> {
            try (Connection conn = RPEngine.getDatabaseManager().getConnection(); PreparedStatement pstmt = conn.prepareStatement("INSERT OR REPLACE INTO jail_time (uuid, remaining_time) VALUES (?, ?)")) {
                pstmt.setString(1, uuid.toString());
                pstmt.setInt(2, time);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                LOGGER.error("Failed to save jail time", e);
            }
        });
    }

    public void loadJailTime(UUID uuid, java.util.function.Consumer<Integer> callback) {
        CompletableFuture.runAsync(() -> {
            try (Connection conn = RPEngine.getDatabaseManager().getConnection(); PreparedStatement pstmt = conn.prepareStatement("SELECT remaining_time FROM jail_time WHERE uuid = ?")) {
                pstmt.setString(1, uuid.toString());
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    int time = rs.getInt("remaining_time");
                    if (time > 0) {
                        jailTimers.put(uuid, time);
                    }
                    if (callback != null) {
                        callback.accept(time);
                    }
                } else {
                    if (callback != null) {
                        callback.accept(0);
                    }
                }
            } catch (SQLException e) {
                LOGGER.error("Failed to load jail time", e);
            }
        });
    }
}
