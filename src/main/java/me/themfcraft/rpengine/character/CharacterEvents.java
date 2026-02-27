package me.themfcraft.rpengine.character;

import me.themfcraft.rpengine.RPEngine;
import me.themfcraft.rpengine.network.NetworkHandler;
import me.themfcraft.rpengine.network.OpenCreationScreenPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = RPEngine.MODID)
public class CharacterEvents {

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        RPEngine.getCharacterManager().loadCharacter(player.getUUID()).thenAccept(data -> {
            if (data == null) {
                // No character found, force open creation screen on client
                NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new OpenCreationScreenPacket());
            } else {
                // Load attributes if character exists
                RPEngine.getAttributeManager().loadAttributes(player);
            }
        });
    }
}
