package me.themfcraft.rpengine.network;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraft.client.Minecraft;

public class OpenATMPacket {

    public OpenATMPacket() {}

    public OpenATMPacket(FriendlyByteBuf buf) {}

    public void encode(FriendlyByteBuf buf) {}

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // Client-side UI opening
            Minecraft.getInstance().setScreen(new me.themfcraft.rpengine.ui.ATMScreen());
        });
        return true;
    }
}
