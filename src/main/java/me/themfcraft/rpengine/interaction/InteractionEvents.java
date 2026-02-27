package me.themfcraft.rpengine.interaction;

import me.themfcraft.rpengine.RPEngine;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RPEngine.MODID)
public class InteractionEvents {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        // Only handle on server side
        if (player.level().isClientSide) {
            return;
        }

        // Check if player is sneaking to trigger engine-specific interaction
        if (player.isShiftKeyDown()) {
            RPEngine.getInteractionManager().handleInteract(player);
            // event.setCanceled(true); // Optional: prevent default interaction
        }
    }
}
