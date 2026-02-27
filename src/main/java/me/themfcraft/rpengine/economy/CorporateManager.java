package me.themfcraft.rpengine.economy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import me.themfcraft.rpengine.RPEngine;

public class CorporateManager {

    private static final Logger LOGGER = LogUtils.getLogger();
    private final Map<String, CorporateAccount> accounts = new HashMap<>();

    public CorporateManager() {
        loadAccounts();
    }

    private void loadAccounts() {
        try (Connection conn = RPEngine.getDatabaseManager().getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM corporate_accounts")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double balance = rs.getDouble("balance");
                UUID ownerUuid = UUID.fromString(rs.getString("owner_uuid"));
                String membersStr = rs.getString("members");

                List<UUID> members = new ArrayList<>();
                if (membersStr != null && !membersStr.isEmpty()) {
                    for (String m : membersStr.split(",")) {
                        members.add(UUID.fromString(m.trim()));
                    }
                }

                accounts.put(name.toLowerCase(), new CorporateAccount(id, name, balance, ownerUuid, members));
            }
            LOGGER.info("Loaded {} corporate accounts.", accounts.size());
        } catch (SQLException e) {
            LOGGER.error("Failed to load corporate accounts", e);
        }
    }

    public CompletableFuture<Boolean> createAccount(String name, UUID ownerUuid) {
        return CompletableFuture.supplyAsync(() -> {
            if (accounts.containsKey(name.toLowerCase())) {
                return false;
            }

            try (Connection conn = RPEngine.getDatabaseManager().getConnection(); PreparedStatement pstmt = conn.prepareStatement("INSERT INTO corporate_accounts (name, balance, owner_uuid, members) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {

                pstmt.setString(1, name);
                pstmt.setDouble(2, 0.0);
                pstmt.setString(3, ownerUuid.toString());
                pstmt.setString(4, "");
                pstmt.executeUpdate();

                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    accounts.put(name.toLowerCase(), new CorporateAccount(id, name, 0.0, ownerUuid, new ArrayList<>()));
                    return true;
                }
            } catch (SQLException e) {
                LOGGER.error("Failed to create corporate account", e);
            }
            return false;
        });
    }

    public void saveAccount(CorporateAccount account) {
        CompletableFuture.runAsync(() -> {
            try (Connection conn = RPEngine.getDatabaseManager().getConnection(); PreparedStatement pstmt = conn.prepareStatement("UPDATE corporate_accounts SET balance = ?, members = ? WHERE id = ?")) {

                pstmt.setDouble(1, account.getBalance());
                pstmt.setString(2, account.getMembersString());
                pstmt.setInt(3, account.getId());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                LOGGER.error("Failed to save corporate account", e);
            }
        });
    }

    public CorporateAccount getAccount(String name) {
        return accounts.get(name.toLowerCase());
    }

    public List<CorporateAccount> getPlayerAccounts(UUID playerUuid) {
        List<CorporateAccount> result = new ArrayList<>();
        for (CorporateAccount account : accounts.values()) {
            if (account.isMember(playerUuid)) {
                result.add(account);
            }
        }
        return result;
    }
}
