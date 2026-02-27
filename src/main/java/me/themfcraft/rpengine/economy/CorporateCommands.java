package me.themfcraft.rpengine.economy;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import me.themfcraft.rpengine.RPEngine;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class CorporateCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("corp")
                .then(Commands.literal("create")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .executes(context -> create(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "name"))))
                )
                .then(Commands.literal("deposit")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0.01))
                                        .executes(context -> deposit(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "name"), DoubleArgumentType.getDouble(context, "amount")))))
                )
                .then(Commands.literal("withdraw")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0.01))
                                        .executes(context -> withdraw(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "name"), DoubleArgumentType.getDouble(context, "amount")))))
                )
                .then(Commands.literal("invite")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(context -> invite(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "name"), EntityArgument.getPlayer(context, "player")))))
                )
                .then(Commands.literal("kick")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(context -> kick(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "name"), EntityArgument.getPlayer(context, "player")))))
                )
                .then(Commands.literal("info")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .executes(context -> info(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "name"))))
                )
        );
    }

    private static int create(ServerPlayer player, String name) {
        // Optional: charge money for creation
        RPEngine.getCorporateManager().createAccount(name, player.getUUID()).thenAccept(success -> {
            if (success) {
                player.sendSystemMessage(Component.literal("§aFirmenkonto '" + name + "' erfolgreich erstellt!"));
            } else {
                player.sendSystemMessage(Component.literal("§cKonto konnte nicht erstellt werden (Name vergeben?)."));
            }
        });
        return 1;
    }

    private static int deposit(ServerPlayer player, String name, double amount) {
        CorporateAccount account = RPEngine.getCorporateManager().getAccount(name);
        if (account == null || !account.isMember(player.getUUID())) {
            player.sendSystemMessage(Component.literal("§cKein Zugriff auf dieses Konto!"));
            return 0;
        }

        if (RPEngine.getEconomyManager().getBank(player.getUUID()) < amount) {
            player.sendSystemMessage(Component.literal("§cUnzureichendes Guthaben auf deinem Bankkonto!"));
            return 0;
        }

        RPEngine.getEconomyManager().addBank(player.getUUID(), -amount);
        account.setBalance(account.getBalance() + amount);
        RPEngine.getCorporateManager().saveAccount(account);

        player.sendSystemMessage(Component.literal("§a$" + amount + " auf '" + name + "' eingezahlt."));
        return 1;
    }

    private static int withdraw(ServerPlayer player, String name, double amount) {
        CorporateAccount account = RPEngine.getCorporateManager().getAccount(name);
        if (account == null || !account.getOwnerUuid().equals(player.getUUID())) {
            player.sendSystemMessage(Component.literal("§cNur der Besitzer kann Geld abheben!"));
            return 0;
        }

        if (account.getBalance() < amount) {
            player.sendSystemMessage(Component.literal("§cUnzureichendes Guthaben auf dem Firmenkonto!"));
            return 0;
        }

        account.setBalance(account.getBalance() - amount);
        RPEngine.getEconomyManager().addBank(player.getUUID(), amount);
        RPEngine.getCorporateManager().saveAccount(account);

        player.sendSystemMessage(Component.literal("§a$" + amount + " von '" + name + "' abgehoben."));
        return 1;
    }

    private static int invite(ServerPlayer player, String name, ServerPlayer target) {
        CorporateAccount account = RPEngine.getCorporateManager().getAccount(name);
        if (account == null || !account.getOwnerUuid().equals(player.getUUID())) {
            player.sendSystemMessage(Component.literal("§cNur der Besitzer kann Mitglieder einladen!"));
            return 0;
        }

        account.addMember(target.getUUID());
        RPEngine.getCorporateManager().saveAccount(account);
        player.sendSystemMessage(Component.literal("§a" + target.getName().getString() + " zu '" + name + "' hinzugefügt."));
        target.sendSystemMessage(Component.literal("§aDu wurdest zum Firmenkonto '" + name + "' hinzugefügt."));
        return 1;
    }

    private static int kick(ServerPlayer player, String name, ServerPlayer target) {
        CorporateAccount account = RPEngine.getCorporateManager().getAccount(name);
        if (account == null || !account.getOwnerUuid().equals(player.getUUID())) {
            player.sendSystemMessage(Component.literal("§cNur der Besitzer kann Mitglieder entfernen!"));
            return 0;
        }

        account.removeMember(target.getUUID());
        RPEngine.getCorporateManager().saveAccount(account);
        player.sendSystemMessage(Component.literal("§c" + target.getName().getString() + " von '" + name + "' entfernt."));
        target.sendSystemMessage(Component.literal("§cDu wurdest vom Firmenkonto '" + name + "' entfernt."));
        return 1;
    }

    private static int info(ServerPlayer player, String name) {
        CorporateAccount account = RPEngine.getCorporateManager().getAccount(name);
        if (account == null || !account.isMember(player.getUUID())) {
            player.sendSystemMessage(Component.literal("§cKein Zugriff!"));
            return 0;
        }

        player.sendSystemMessage(Component.literal("§6--- Firmenkonto: " + account.getName() + " ---"));
        player.sendSystemMessage(Component.literal("§eGuthaben: §a$" + String.format("%.2f", account.getBalance())));
        player.sendSystemMessage(Component.literal("§eBesitzer: §7" + account.getOwnerUuid()));
        player.sendSystemMessage(Component.literal("§eMitglieder: §7" + account.getMembers().size()));
        return 1;
    }
}
