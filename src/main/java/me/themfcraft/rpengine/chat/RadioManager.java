package me.themfcraft.rpengine.chat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.server.level.ServerPlayer;

public class RadioManager {

    private final Map<UUID, Double> playerFrequencies = new HashMap<>();
    private final Map<UUID, Boolean> radioStatus = new HashMap<>();

    public void setFrequency(UUID playerUuid, double frequency) {
        playerFrequencies.put(playerUuid, frequency);
    }

    public double getFrequency(UUID playerUuid) {
        return playerFrequencies.getOrDefault(playerUuid, 0.0);
    }

    public void setRadioStatus(UUID playerUuid, boolean active) {
        radioStatus.put(playerUuid, active);
    }

    public boolean isRadioActive(UUID playerUuid) {
        return radioStatus.getOrDefault(playerUuid, false);
    }

    public void broadcast(ServerPlayer sender, String message) {
        double freq = getFrequency(sender.getUUID());
        if (!isRadioActive(sender.getUUID())) {
            return;
        }

        String formatted = "§8[Funk: " + freq + "] §7" + sender.getName().getString() + ": " + message;

        if (sender.getServer() == null) {
            return;
        }
        for (ServerPlayer player : sender.getServer().getPlayerList().getPlayers()) {
            if (isRadioActive(player.getUUID()) && getFrequency(player.getUUID()) == freq) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(formatted));
            }
        }
    }
}
