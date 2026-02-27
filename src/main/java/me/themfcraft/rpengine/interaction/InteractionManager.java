package me.themfcraft.rpengine.interaction;

import me.themfcraft.rpengine.RPEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionManager {

    public void handleInteract(ServerPlayer player) {
        HitResult hit = player.pick(5.0D, 0.0F, false);

        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hit;
            BlockPos pos = blockHit.getBlockPos();
            ServerLevel level = player.serverLevel();
            BlockState state = level.getBlockState(pos);

            if (state.getBlock() instanceof DoorBlock) {
                BlockPos doorPos = state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
                toggleDoorLock(player, doorPos);
            }
        } else if (hit.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hit;
            if (entityHit.getEntity() instanceof ServerPlayer target) {
                if (player.isShiftKeyDown()) {
                    searchPlayer(player, target);
                }
            }
        }
    }

    private void toggleDoorLock(ServerPlayer player, BlockPos pos) {
        String posStr = pos.getX() + "," + pos.getY() + "," + pos.getZ();
        boolean isLocked = isDoorLocked(posStr);
        
        // Check if player has key or is admin
        RPEngine.getKeyManager().hasKey(player.getUUID(), "door_" + posStr).thenAccept(hasKey -> {
            if (hasKey || player.hasPermissions(2)) {
                setDoorLocked(posStr, !isLocked, player.getUUID().toString());
                player.sendSystemMessage(Component.literal("§7Tür wurde " + (!isLocked ? "§cverschlossen" : "§aaufgeschlossen") + "§7."));
            } else {
                player.sendSystemMessage(Component.literal("§cDu hast keinen passenden Schlüssel!"));
            }
        });
    }

    public boolean isDoorLocked(String posStr) {
        String sql = "SELECT is_locked FROM locked_objects WHERE location = ?";
        try (PreparedStatement pstmt = RPEngine.getDatabaseManager().getConnection().prepareStatement(sql)) {
            pstmt.setString(1, posStr);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("is_locked") == 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void setDoorLocked(String posStr, boolean locked, String ownerUuid) {
        String sql = "INSERT OR REPLACE INTO locked_objects (location, owner_uuid, is_locked) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = RPEngine.getDatabaseManager().getConnection().prepareStatement(sql)) {
            pstmt.setString(1, posStr);
            pstmt.setString(2, ownerUuid);
            pstmt.setInt(3, locked ? 1 : 0);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void searchPlayer(ServerPlayer searcher, ServerPlayer target) {
        searcher.sendSystemMessage(Component.literal("§7--- §6Inventar von " + target.getName().getString() + " §7---"));
        boolean foundAnything = false;
        for (int i = 0; i < target.getInventory().getContainerSize(); i++) {
            ItemStack stack = target.getInventory().getItem(i);
            if (!stack.isEmpty()) {
                foundAnything = true;
                boolean isIllegal = RPEngine.getIllegalManager().isIllegal(stack);
                String color = isIllegal ? "§c" : "§a";
                searcher.sendSystemMessage(Component.literal(color + "- " + stack.getCount() + "x " + stack.getHoverName().getString() + (isIllegal ? " §7(ILLEGAL)" : "")));
            }
        }
        if (!foundAnything) {
            searcher.sendSystemMessage(Component.literal("§8Das Inventar ist leer."));
        }
    }
}
