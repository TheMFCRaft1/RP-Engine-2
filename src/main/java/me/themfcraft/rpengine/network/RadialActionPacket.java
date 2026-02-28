package me.themfcraft.rpengine.network;

import java.util.UUID;
import java.util.function.Supplier;

import me.themfcraft.rpengine.RPEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class RadialActionPacket {

    private final String action;
    private BlockPos pos;
    private UUID entityId;

    public RadialActionPacket(String action, BlockPos pos) {
        this.action = action;
        this.pos = pos;
    }

    public RadialActionPacket(String action, java.util.UUID entityId) {
        this.action = action;
        this.entityId = entityId;
    }

    public RadialActionPacket(FriendlyByteBuf buf) {
        this.action = buf.readUtf();
        byte type = buf.readByte();
        if (type == 0) {
            this.pos = buf.readBlockPos();
        } else if (type == 1) {
            this.entityId = buf.readUUID();
        }
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(this.action);
        if (pos != null) {
            buf.writeByte(0);
            buf.writeBlockPos(pos);
        } else if (entityId != null) {
            buf.writeByte(1);
            buf.writeUUID(entityId);
        } else {
            buf.writeByte(-1);
        }
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }

            switch (action) {
                case "lock_door" -> 
                    RPEngine.getInteractionManager().handleInteract(player);
                case "unlock_door" ->
                    RPEngine.getInteractionManager().handleInteract(player);
                case "open_npc_shop" -> {
                    me.themfcraft.rpengine.economy.Shop shop = RPEngine.getShopManager().getShopByNpc(entityId);
                    if (shop != null) {
                        NetworkHandler.CHANNEL.send(net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player), new OpenShopPacket(shop.getId()));
                    } else {
                        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cDieser NPC ist kein Händler!"));
                    }
                }
                case "search_player" -> {
                    if (player.getServer() == null) return;
                    ServerPlayer target = player.getServer().getPlayerList().getPlayer(entityId);
                    if (target != null) {
                        int illegalCount = 0;
                        StringBuilder illegalItems = new StringBuilder();
                        for (net.minecraft.world.item.ItemStack stack : target.getInventory().items) {
                            if (RPEngine.getIllegalManager().isIllegal(stack)) {
                                illegalCount++;
                                illegalItems.append(stack.getHoverName().getString()).append(" ");
                            }
                        }
                        if (illegalCount > 0) {
                            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§c" + target.getName().getString() + " hat illegale Gegenstände: " + illegalItems.toString().trim()));
                        } else {
                            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aKeine illegalen Gegenstände bei " + target.getName().getString() + " gefunden."));
                        }
                    } else {
                        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cSpieler nicht gefunden."));
                    }
                }
                case "toggle_handcuff" -> {
                     if (player.getServer() == null) return;
                     ServerPlayer target = player.getServer().getPlayerList().getPlayer(entityId);
                     if (target != null) {
                         boolean currentlyHandcuffed = RPEngine.getIllegalManager().isHandcuffed(target.getUUID());
                         RPEngine.getIllegalManager().setHandcuffed(target.getUUID(), !currentlyHandcuffed);
                         if (!currentlyHandcuffed) {
                             player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aDu hast " + target.getName().getString() + " gefesselt."));
                             target.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cDu wurdest gefesselt!"));
                         } else {
                             player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aDu hast " + target.getName().getString() + " entfesselt."));
                             target.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aDu wurdest entfesselt."));
                         }
                     }
                }
                case "show_id" -> {
                    me.themfcraft.rpengine.character.CharacterData data = RPEngine.getCharacterManager().getCharacter(player.getUUID());
                    if (data != null) {
                        String msg = "§6[Ausweis] §e" + data.firstName() + " " + data.lastName() + " §7(" + data.age() + ")";
                        player.sendSystemMessage(net.minecraft.network.chat.Component.literal(msg));
                        for (ServerPlayer nearPlayer : player.serverLevel().players()) {
                            if (nearPlayer != player && nearPlayer.distanceToSqr(player) < 25) {
                                nearPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal(msg));
                            }
                        }
                    }
                }
            }
        });
        return true;
    }
}
