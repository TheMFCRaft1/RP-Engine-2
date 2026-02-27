package me.themfcraft.rpengine.medical;

import me.themfcraft.rpengine.RPEngine;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RPEngine.MODID)
public class MedicalEventHandler {

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            MedicalManager manager = RPEngine.getMedicalManager();
            if (!manager.isDowned(player.getUUID())) {
                event.setCanceled(true);
                manager.setDowned(player.getUUID(), true);
                player.setHealth(1.0f);
                player.sendSystemMessage(Component.literal("§cDu bist schwer verletzt! Warte auf einen Sanitäter."));
                // In a full implementation, we would add effects and restrict movement here
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (RPEngine.getMedicalManager().isDowned(player.getUUID())) {
                event.setCanceled(true);
            }
        }
    }
}
