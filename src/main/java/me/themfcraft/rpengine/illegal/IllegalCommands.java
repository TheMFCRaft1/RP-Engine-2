package me.themfcraft.rpengine.illegal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import me.themfcraft.rpengine.RPEngine;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class IllegalCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("jail")
            .requires(source -> source.hasPermission(2))
            .then(Commands.argument("player", EntityArgument.player())
                .then(Commands.argument("time", IntegerArgumentType.integer(1))
                    .executes(context -> {
                        ServerPlayer source = context.getSource().getPlayerOrException();
                        ServerPlayer target = EntityArgument.getPlayer(context, "player");
                        int duration = IntegerArgumentType.getInteger(context, "time");
                        
                        RPEngine.getIllegalManager().setJailTime(target.getUUID(), duration);
                        RPEngine.getIllegalManager().setHandcuffed(target.getUUID(), false); // Remove handcuffs when jailed
                        
                        source.sendSystemMessage(Component.literal("§a" + target.getName().getString() + " wurde für " + duration + " Sekunden inhaftiert."));
                        target.sendSystemMessage(Component.literal("§cDu wurdest für " + duration + " Sekunden inhaftiert."));
                        return 1;
                    })))
        );

        dispatcher.register(Commands.literal("illegal")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("set")
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
                    if (!stack.isEmpty()) {
                        RPEngine.getIllegalManager().makeIllegal(stack);
                        player.sendSystemMessage(Component.literal("§aGegenstand als illegal markiert."));
                    } else {
                        player.sendSystemMessage(Component.literal("§cDu hältst keinen Gegenstand in der Hand."));
                    }
                    return 1;
                }))
        );
    }
}
