package me.themfcraft.rpengine.character;

import me.themfcraft.rpengine.RPEngine;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CharacterManager {

    private final Map<UUID, CharacterData> characters = new HashMap<>();

    public CompletableFuture<Void> saveCharacter(CharacterData character) {
        return CompletableFuture.runAsync(() -> {
            String sql = "INSERT OR REPLACE INTO characters (uuid, first_name, last_name, age, gender, backstory) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = RPEngine.getDatabaseManager().getConnection().prepareStatement(sql)) {
                pstmt.setString(1, character.id().toString());
                pstmt.setString(2, character.firstName());
                pstmt.setString(3, character.lastName());
                pstmt.setInt(4, character.age());
                pstmt.setString(5, character.gender());
                pstmt.setString(6, character.backstory());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public CompletableFuture<CharacterData> loadCharacter(UUID id) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT * FROM characters WHERE uuid = ?";
            try (PreparedStatement pstmt = RPEngine.getDatabaseManager().getConnection().prepareStatement(sql)) {
                pstmt.setString(1, id.toString());
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    CharacterData data = new CharacterData(
                            id,
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getInt("age"),
                            rs.getString("gender"),
                            rs.getString("backstory")
                    );
                    registerCharacter(data);
                    return data;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public void registerCharacter(CharacterData character) {
        characters.put(character.id(), character);
    }

    public CharacterData getCharacter(UUID id) {
        return characters.get(id);
    }

    public void removeCharacter(UUID id) {
        characters.remove(id);
    }
}
