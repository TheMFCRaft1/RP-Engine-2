package me.themfcraft.rpengine.economy;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class ATMBlock extends Block {
    public ATMBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            // Here we would open the ATM UI
            player.sendSystemMessage(Component.literal("§6Geldautomat wird geöffnet... (Noch in Arbeit)"));
            // TODO: Implement ATMScreen and OpenATMPacket
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
