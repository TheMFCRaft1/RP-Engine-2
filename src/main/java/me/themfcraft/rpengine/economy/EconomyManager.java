package me.themfcraft.rpengine.economy;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import me.themfcraft.rpengine.RPEngine;

public class EconomyManager {

    private final Map<UUID, Double> cashBalances = new HashMap<>();
    private final Map<UUID, Double> bankBalances = new HashMap<>();
    private final Map<UUID, Integer> diamondBalances = new HashMap<>();
    private final Map<UUID, Integer> emeraldBalances = new HashMap<>();

    public CompletableFuture<Void> saveEconomy(UUID uuid) {
        return CompletableFuture.runAsync(() -> {
            String sql = "INSERT OR REPLACE INTO economy (uuid, cash, bank, diamonds, emeralds) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = RPEngine.getDatabaseManager().getConnection().prepareStatement(sql)) {
                pstmt.setString(1, uuid.toString());
                pstmt.setDouble(2, getCash(uuid));
                pstmt.setDouble(3, getBank(uuid));
                pstmt.setInt(4, getDiamonds(uuid));
                pstmt.setInt(5, getEmeralds(uuid));
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public CompletableFuture<Void> loadEconomy(UUID uuid) {
        return CompletableFuture.runAsync(() -> {
            String sql = "SELECT cash, bank, diamonds, emeralds FROM economy WHERE uuid = ?";
            try (PreparedStatement pstmt = RPEngine.getDatabaseManager().getConnection().prepareStatement(sql)) {
                pstmt.setString(1, uuid.toString());
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    setCash(uuid, rs.getDouble("cash"));
                    setBank(uuid, rs.getDouble("bank"));
                    setDiamonds(uuid, rs.getInt("diamonds"));
                    setEmeralds(uuid, rs.getInt("emeralds"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public double getCash(UUID uuid) {
        return cashBalances.getOrDefault(uuid, 0.0);
    }

    public void setCash(UUID uuid, double amount) {
        cashBalances.put(uuid, amount);
    }

    public void addCash(UUID uuid, double amount) {
        setCash(uuid, getCash(uuid) + amount);
    }

    public double getBank(UUID uuid) {
        return bankBalances.getOrDefault(uuid, 0.0);
    }

    public void setBank(UUID uuid, double amount) {
        bankBalances.put(uuid, amount);
    }

    public void addBank(UUID uuid, double amount) {
        setBank(uuid, getBank(uuid) + amount);
    }

    public void removeCash(UUID uuid, double amount) {
        setCash(uuid, Math.max(0, getCash(uuid) - amount));
    }

    public void removeBank(UUID uuid, double amount) {
        setBank(uuid, Math.max(0, getBank(uuid) - amount));
    }

    // Material Economy: Diamonds
    public int getDiamonds(UUID uuid) {
        return diamondBalances.getOrDefault(uuid, 0);
    }

    public void setDiamonds(UUID uuid, int amount) {
        diamondBalances.put(uuid, amount);
    }

    public void addDiamonds(UUID uuid, int amount) {
        setDiamonds(uuid, getDiamonds(uuid) + amount);
    }

    public void removeDiamonds(UUID uuid, int amount) {
        setDiamonds(uuid, Math.max(0, getDiamonds(uuid) - amount));
    }

    // Material Economy: Emeralds
    public int getEmeralds(UUID uuid) {
        return emeraldBalances.getOrDefault(uuid, 0);
    }

    public void setEmeralds(UUID uuid, int amount) {
        emeraldBalances.put(uuid, amount);
    }

    public void addEmeralds(UUID uuid, int amount) {
        setEmeralds(uuid, getEmeralds(uuid) + amount);
    }

    public void removeEmeralds(UUID uuid, int amount) {
        setEmeralds(uuid, Math.max(0, getEmeralds(uuid) - amount));
    }
}
