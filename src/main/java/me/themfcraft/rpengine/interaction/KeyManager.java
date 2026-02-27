package me.themfcraft.rpengine.interaction;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import me.themfcraft.rpengine.RPEngine;

public class KeyManager {

    private static final Logger LOGGER = LogUtils.getLogger();

    public CompletableFuture<Boolean> hasKey(UUID playerUuid, String keyId) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT 1 FROM character_keys WHERE uuid = ? AND key_id = ?";
            try (PreparedStatement pstmt = RPEngine.getDatabaseManager().getConnection().prepareStatement(sql)) {
                pstmt.setString(1, playerUuid.toString());
                pstmt.setString(2, keyId);
                ResultSet rs = pstmt.executeQuery();
                return rs.next();
            } catch (SQLException e) {
                LOGGER.error("Failed to check key existence", e);
            }
            return false;
        });
    }

    public CompletableFuture<Void> giveKey(UUID playerUuid, String keyId) {
        return CompletableFuture.runAsync(() -> {
            String sql = "INSERT OR IGNORE INTO character_keys (uuid, key_id) VALUES (?, ?)";
            try (PreparedStatement pstmt = RPEngine.getDatabaseManager().getConnection().prepareStatement(sql)) {
                pstmt.setString(1, playerUuid.toString());
                pstmt.setString(2, keyId);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                LOGGER.error("Failed to give key", e);
            }
        });
    }

    public CompletableFuture<Void> removeKey(UUID playerUuid, String keyId) {
        return CompletableFuture.runAsync(() -> {
            String sql = "DELETE FROM character_keys WHERE uuid = ? AND key_id = ?";
            try (PreparedStatement pstmt = RPEngine.getDatabaseManager().getConnection().prepareStatement(sql)) {
                pstmt.setString(1, playerUuid.toString());
                pstmt.setString(2, keyId);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                LOGGER.error("Failed to remove key", e);
            }
        });
    }
}
