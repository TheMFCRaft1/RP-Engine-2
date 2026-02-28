package me.themfcraft.rpengine.ui;

import java.util.ArrayList;
import java.util.List;

import me.themfcraft.rpengine.RPEngine;
import me.themfcraft.rpengine.interaction.RadialMenuScreen;
import me.themfcraft.rpengine.interaction.RadialOption;
import me.themfcraft.rpengine.network.NetworkHandler;
import me.themfcraft.rpengine.network.RadialActionPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RPEngine.MODID, value = Dist.CLIENT)
public class KeyInputEvents {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            return;
        }

        while (KeyBindingHandler.MENU_KEY.consumeClick()) {
            mc.setScreen(new CharacterManagementScreen());
        }

        while (KeyBindingHandler.INTERACTION_KEY.consumeClick()) {
            var localPlayer = mc.player;
            if (localPlayer == null) continue;
            HitResult hit = localPlayer.pick(5.0D, 0.0F, false);
            List<RadialOption> options = new ArrayList<>();

            switch (hit.getType()) {
                case BLOCK -> {
                    BlockHitResult blockHit = (BlockHitResult) hit;
                    BlockPos pos = blockHit.getBlockPos();
                    if (mc.level != null) {
                        BlockState state = mc.level.getBlockState(pos);
                        if (state.getBlock() instanceof DoorBlock) {
                            options.add(new RadialOption(Component.literal("Aufschließen"), "🔓", (v) -> {
                                NetworkHandler.CHANNEL.sendToServer(new RadialActionPacket("unlock_door", pos));
                            }));
                            options.add(new RadialOption(Component.literal("Abschließen"), "🔒", (v) -> {
                                NetworkHandler.CHANNEL.sendToServer(new RadialActionPacket("lock_door", pos));
                            }));
                        }
                    }
                }
                case ENTITY -> {
                    EntityHitResult entityHit = (EntityHitResult) hit;
                    Entity entity = entityHit.getEntity();
                    if (entity instanceof net.minecraft.world.entity.player.Player) {
                        options.add(new RadialOption(Component.literal("Durchsuchen"), "🔍", (v) -> {
                            NetworkHandler.CHANNEL.sendToServer(new RadialActionPacket("search_player", entity.getUUID()));
                        }));
                        options.add(new RadialOption(Component.literal("Fesseln/Lösen"), "🔗", (v) -> {
                            NetworkHandler.CHANNEL.sendToServer(new RadialActionPacket("toggle_handcuff", entity.getUUID()));
                        }));
                    } else {
                        options.add(new RadialOption(Component.literal("Shop öffnen"), "💰", (v) -> {
                            NetworkHandler.CHANNEL.sendToServer(new RadialActionPacket("open_npc_shop", entity.getUUID()));
                        }));
                    }
                }
                default -> {
                    options.add(new RadialOption(Component.literal("Animationen"), "🎭", (v) -> {
                    }));
                    if (mc.player != null) {
                        options.add(new RadialOption(Component.literal("Ausweis zeigen"), "🆔", (v) -> {
                            NetworkHandler.CHANNEL.sendToServer(new RadialActionPacket("show_id", Minecraft.getInstance().player.getUUID()));
                        }));
                    }
                }
            }

            if (!options.isEmpty()) {
                mc.setScreen(new RadialMenuScreen(options));
            }
        }
    }
}
