package me.themfcraft.rpengine.network;

import me.themfcraft.rpengine.RPEngine;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RadioSyncPacket {

    private final double frequency;
    private final boolean active;

    public RadioSyncPacket(double frequency, boolean active) {
        this.frequency = frequency;
        this.active = active;
    }

    public RadioSyncPacket(FriendlyByteBuf buf) {
        this.frequency = buf.readDouble();
        this.active = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(this.frequency);
        buf.writeBoolean(this.active);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }

            RPEngine.getRadioManager().setFrequency(player.getUUID(), frequency);
            RPEngine.getRadioManager().setRadioStatus(player.getUUID(), active);

            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§7Radio-Einstellungen aktualisiert: §b" + frequency + " Hz §7(Status: " + (active ? "§aEin" : "§cAus") + "§7)"));
        });
        return true;
    }
}
