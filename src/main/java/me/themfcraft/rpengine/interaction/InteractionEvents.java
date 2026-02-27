package me.themfcraft.rpengine.interaction;

import me.themfcraft.rpengine.RPEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
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
            event.setCanceled(true); 
        } else {
            // Check if door is locked
            BlockState state = player.level().getBlockState(event.getPos());
            if (state.getBlock() instanceof DoorBlock) {
                BlockPos doorPos = state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER ? event.getPos().below() : event.getPos();
                String posStr = doorPos.getX() + "," + doorPos.getY() + "," + doorPos.getZ();
                if (RPEngine.getInteractionManager().isDoorLocked(posStr)) {
                    player.sendSystemMessage(Component.literal("§cDiese Tür ist abgeschlossen!"));
                    event.setCanceled(true);
                }
            }
        }
    }
}
