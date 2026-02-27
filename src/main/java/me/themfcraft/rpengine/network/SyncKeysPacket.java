package me.themfcraft.rpengine.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SyncKeysPacket {

    private final List<String> keys;

    public SyncKeysPacket(List<String> keys) {
        this.keys = keys;
    }

    public SyncKeysPacket(FriendlyByteBuf buf) {
        int size = buf.readInt();
        this.keys = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            this.keys.add(buf.readUtf());
        }
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(keys.size());
        for (String key : keys) {
            buf.writeUtf(key);
        }
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // Store keys in a client-side cache for the GUI
            ClientCache.setOwnedKeys(keys);
        });
        return true;
    }
}
