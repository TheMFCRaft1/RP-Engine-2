package me.themfcraft.rpengine.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class NotificationManager {

    public static void sendNotification(String title, String message, int color) {
        // Simple implementation using system messages for now
        // This could be expanded to a custom GUI overlay in the future
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.sendSystemMessage(Component.literal("§l[" + title + "]§r " + message).withStyle(s -> s.withColor(color)));
        }
    }
}
