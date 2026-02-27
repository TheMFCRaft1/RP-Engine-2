package me.themfcraft.rpengine.network;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

public class RequestKeysPacket {

    public RequestKeysPacket() {
    }

    public RequestKeysPacket(net.minecraft.network.FriendlyByteBuf buf) {
    }

    public void encode(net.minecraft.network.FriendlyByteBuf buf) {
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }

            // In a real scenario, we'd fetch from DB. For now, let's assume we have a way to get all keys.
            // Since KeyManager doesn't have a "getAllKeys" yet, I'll add a placeholder list or implement it.
            List<String> keys = new ArrayList<>();
            // Mock keys for now
            keys.add("Haus-Schlüssel");
            keys.add("Auto-Schlüssel (B-MW 123)");

            NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new SyncKeysPacket(keys));
        });
        return true;
    }
}
