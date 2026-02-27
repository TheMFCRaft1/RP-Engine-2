package me.themfcraft.rpengine.network;

import java.util.function.Supplier;

import me.themfcraft.rpengine.character.CharacterCreationScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class OpenCreationScreenPacket {

    public OpenCreationScreenPacket() {
    }

    public OpenCreationScreenPacket(FriendlyByteBuf buf) {
    }

    public void encode(FriendlyByteBuf buf) {
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Minecraft.getInstance().setScreen(new CharacterCreationScreen());
        });
        return true;
    }
}
