package me.themfcraft.rpengine.chat;

import me.themfcraft.rpengine.RPEngine;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RPEngine.MODID)
public class ChatEvents {

    @SubscribeEvent
    public static void onChat(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();
        String rawMessage = event.getRawText();

        // Check for prefixes to determine range
        ChatManager.ChatRange range = ChatManager.ChatRange.TALK;
        String message = rawMessage;

        if (rawMessage.startsWith("!")) {
            range = ChatManager.ChatRange.SHOUT;
            message = rawMessage.substring(1).trim();
        } else if (rawMessage.startsWith("@")) {
            range = ChatManager.ChatRange.WHISPER;
            message = rawMessage.substring(1).trim();
        }

        if (message.isEmpty()) {
            return;
        }

        RPEngine.getChatManager().sendMessageInRange(player, message, range);
        event.setCanceled(true); // Stop default global chat
    }
}
