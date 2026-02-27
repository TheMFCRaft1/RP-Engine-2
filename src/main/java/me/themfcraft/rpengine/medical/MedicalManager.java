package me.themfcraft.rpengine.medical;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MedicalManager {
    private final Map<UUID, Boolean> downedPlayers = new HashMap<>();

    public void setDowned(UUID uuid, boolean isDowned) {
        downedPlayers.put(uuid, isDowned);
    }

    public boolean isDowned(UUID uuid) {
        return downedPlayers.getOrDefault(uuid, false);
    }
}
