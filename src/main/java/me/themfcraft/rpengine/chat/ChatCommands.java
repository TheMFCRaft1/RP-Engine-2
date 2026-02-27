package me.themfcraft.rpengine.chat;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import me.themfcraft.rpengine.RPEngine;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ChatCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("radio")
                .then(Commands.argument("message", StringArgumentType.greedyString())
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            String message = StringArgumentType.getString(context, "message");

                            if (RPEngine.getRadioManager().isRadioActive(player.getUUID())) {
                                RPEngine.getRadioManager().broadcast(player, message);
                                return 1;
                            } else {
                                player.sendSystemMessage(Component.literal("§cDein Funkgerät ist ausgeschaltet!"));
                                return 0;
                            }
                        }))
        );
    }
}
