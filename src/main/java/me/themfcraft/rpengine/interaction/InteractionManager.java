package me.themfcraft.rpengine.interaction;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class InteractionManager {

    public void handleInteract(ServerPlayer player) {
        HitResult hit = player.pick(5.0D, 0.0F, false);

        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hit;
            BlockPos pos = blockHit.getBlockPos();
            ServerLevel level = player.serverLevel();
            BlockState state = level.getBlockState(pos);

            if (state.getBlock() instanceof DoorBlock) {
                // Handle door interaction logic here (locking/unlocking)
                // For now, we'll just log it
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Du interagierst mit einer Tür an " + pos.toShortString()));
            }
        } else if (hit.getType() == HitResult.Type.ENTITY) {
            // Handle entity interaction (e.g., vehicles, other players)
        }
    }
}
