package me.themfcraft.rpengine.illegal;

import me.themfcraft.rpengine.RPEngine;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

@Mod.EventBusSubscriber(modid = RPEngine.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class IllegalEventHandler {

    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        tickCounter++;
        if (tickCounter >= 20) { // Execute approximately every second
            tickCounter = 0;
            if (ServerLifecycleHooks.getCurrentServer() != null) {
                for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                    RPEngine.getIllegalManager().decreaseJailTime(player.getUUID());
                    // Additional logic: If jail time > 0, teleport to jail coords if they move too far
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent event) {
        if (RPEngine.getIllegalManager().isHandcuffed(event.getEntity().getUUID())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {
        if (RPEngine.getIllegalManager().isHandcuffed(event.getEntity().getUUID())) {
            event.setCanceled(true);
        }
    }
}
