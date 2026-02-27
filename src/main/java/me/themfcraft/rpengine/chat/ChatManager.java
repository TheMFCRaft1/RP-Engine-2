package me.themfcraft.rpengine.chat;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;

public class ChatManager {

    public enum ChatRange {
        WHISPER(5),
        TALK(20),
        SHOUT(50);

        private final int range;

        ChatRange(int range) {
            this.range = range;
        }

        public int getRange() {
            return range;
        }
    }

    public void sendMessageInRange(ServerPlayer sender, String message, ChatRange type) {
        Vec3 pos = sender.position();
        double rangeSq = type.getRange() * type.getRange();

        Component formattedMessage = formatMessage(sender, message, type);

        Collection<ServerPlayer> players = sender.server.getPlayerList().getPlayers();
        for (ServerPlayer player : players) {
            if (player.level() == sender.level() && player.distanceToSqr(pos) <= rangeSq) {
                player.sendSystemMessage(formattedMessage);
            }
        }
    }

    private Component formatMessage(ServerPlayer sender, String message, ChatRange type) {
        String prefix = switch (type) {
            case WHISPER ->
                "[Flüstern] ";
            case SHOUT ->
                "[Schreien] ";
            default ->
                "";
        };
        return Component.literal(prefix + sender.getName().getString() + ": " + message);
    }

    public void sendAction(ServerPlayer sender, String action) {
        Component msg = Component.literal("* " + sender.getName().getString() + " " + action).withStyle(s -> s.withItalic(true).withColor(0xFFAA00));
        sendLocal(sender, msg, 20);
    }

    public void sendDescription(ServerPlayer sender, String msg) {
        Component formatted = Component.literal("[!] " + msg + " (" + sender.getName().getString() + ")").withStyle(s -> s.withItalic(true).withColor(0xAAAAAA));
        sendLocal(sender, formatted, 20);
    }

    private void sendLocal(ServerPlayer sender, Component msg, double range) {
        Vec3 pos = sender.position();
        double rangeSq = range * range;
        for (ServerPlayer player : sender.server.getPlayerList().getPlayers()) {
            if (player.level() == sender.level() && player.distanceToSqr(pos) <= rangeSq) {
                player.sendSystemMessage(msg);
            }
        }
    }
}
