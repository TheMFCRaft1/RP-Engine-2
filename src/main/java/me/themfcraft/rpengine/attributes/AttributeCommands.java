package me.themfcraft.rpengine.attributes;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.themfcraft.rpengine.RPEngine;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class AttributeCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("attribute")
            .requires(source -> source.hasPermission(2))
            .then(Commands.argument("player", EntityArgument.player())
                .then(Commands.argument("attribute", StringArgumentType.word())
                    .then(Commands.argument("value", IntegerArgumentType.integer(1))
                        .executes(context -> {
                            ServerPlayer source = context.getSource().getPlayerOrException();
                            ServerPlayer target = EntityArgument.getPlayer(context, "player");
                            String attribute = StringArgumentType.getString(context, "attribute").toLowerCase();
                            int value = IntegerArgumentType.getInteger(context, "value");
                            
                            switch (attribute) {
                                case "strength" -> {
                                    RPEngine.getAttributeManager().setStrength(target, value);
                                    source.sendSystemMessage(Component.literal("§aStärke von " + target.getName().getString() + " auf " + value + " gesetzt."));
                                }
                                case "stamina" -> {
                                    RPEngine.getAttributeManager().setStamina(target, value);
                                    source.sendSystemMessage(Component.literal("§aAusdauer von " + target.getName().getString() + " auf " + value + " gesetzt."));
                                }
                                default -> {
                                    source.sendSystemMessage(Component.literal("§cUngültiges Attribut. Nutze 'strength' oder 'stamina'."));
                                    return 0;
                                }
                            }
                            return 1;
                        }))))
        );
    }
}
