package me.themfcraft.rpengine.network;

import java.util.UUID;
import java.util.function.Supplier;

import me.themfcraft.rpengine.RPEngine;
import me.themfcraft.rpengine.character.CharacterData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class CharacterCreationPacket {

    private final String firstName;
    private final String lastName;
    private final int age;
    private final String gender;
    private final String backstory;

    public CharacterCreationPacket(String firstName, String lastName, int age, String gender, String backstory) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.gender = gender;
        this.backstory = backstory;
    }

    public CharacterCreationPacket(FriendlyByteBuf buf) {
        this.firstName = buf.readUtf();
        this.lastName = buf.readUtf();
        this.age = buf.readInt();
        this.gender = buf.readUtf();
        this.backstory = buf.readUtf();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(this.firstName);
        buf.writeUtf(this.lastName);
        buf.writeInt(this.age);
        buf.writeUtf(this.gender);
        buf.writeUtf(this.backstory);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }

            UUID id = player.getUUID();
            CharacterData data = new CharacterData(id, firstName, lastName, age, gender, backstory);

            RPEngine.getCharacterManager().registerCharacter(data);
            RPEngine.getCharacterManager().saveCharacter(data);

            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aCharakter erfolgreich erstellt: " + data.getFullName()));
        });
        return true;
    }
}
