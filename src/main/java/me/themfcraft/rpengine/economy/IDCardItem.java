package me.themfcraft.rpengine.economy;

import me.themfcraft.rpengine.RPEngine;
import me.themfcraft.rpengine.character.CharacterData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class IDCardItem extends Item {
    public IDCardItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            CharacterData data = RPEngine.getCharacterManager().getCharacter(player.getUUID());
            if (data != null) {
                player.sendSystemMessage(Component.literal("§7--- §6Personalausweis §7---"));
                player.sendSystemMessage(Component.literal("§eName: §f" + data.firstName() + " " + data.lastName()));
                player.sendSystemMessage(Component.literal("§eAlter: §f" + data.age()));
                player.sendSystemMessage(Component.literal("§eGeschlecht: §f" + data.gender()));
            } else {
                player.sendSystemMessage(Component.literal("§cKeine Charakterdaten gefunden!"));
            }
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }
}
