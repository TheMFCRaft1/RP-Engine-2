package me.themfcraft.rpengine;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class RPCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("rp")
                .executes(context -> displayHelp(context.getSource().getPlayerOrException()))
        );
    }

    private static int displayHelp(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("§6§l--- RP Engine 2 Befehlsübersicht ---"));
        
        player.sendSystemMessage(Component.literal("§e§lAllgemein:"));
        player.sendSystemMessage(Component.literal("§7/duty §8- §fDienststatus umschalten"));
        player.sendSystemMessage(Component.literal("§7/radio <nachricht> §8- §fFunkspruch senden"));
        
        player.sendSystemMessage(Component.literal("§e§lWirtschaft:"));
        player.sendSystemMessage(Component.literal("§7/deposit <diamonds|emeralds> <menge> §8- §fEinzahlen"));
        player.sendSystemMessage(Component.literal("§7/withdraw <diamonds|emeralds> <menge> §8- §fAbheben"));
        player.sendSystemMessage(Component.literal("§7/shop <id> §8- §fShop öffnen"));
        
        player.sendSystemMessage(Component.literal("§e§lFirmen:"));
        player.sendSystemMessage(Component.literal("§7/corp create <name> §8- §fFirma gründen"));
        player.sendSystemMessage(Component.literal("§7/corp info <name> §8- §fFirmeninfos anzeigen"));
        player.sendSystemMessage(Component.literal("§7/corp deposit <name> <betrag> §8- §fAuf Firmenkonto einzahlen"));
        player.sendSystemMessage(Component.literal("§7/corp withdraw <name> <betrag> §8- §fVom Firmenkonto abheben (Besitzer)"));
        player.sendSystemMessage(Component.literal("§7/corp invite/kick <name> <spieler> §8- §fMitglieder verwalten"));
        
        if (player.hasPermissions(2)) {
            player.sendSystemMessage(Component.literal("§c§lAdmin/Gesetzeshüter:"));
            player.sendSystemMessage(Component.literal("§7/jail <spieler> <zeit> §8- §fSpieler inhaftieren"));
            player.sendSystemMessage(Component.literal("§7/illegal set §8- §fItem als illegal markieren"));
            player.sendSystemMessage(Component.literal("§7/attribute <spieler> <attribut> <wert> §8- §fAttribute setzen"));
            player.sendSystemMessage(Component.literal("§7/setshop <shopId> §8- §fNPC einen Shop zuweisen"));
        }
        
        player.sendSystemMessage(Component.literal("§6§l----------------------------------"));
        return 1;
    }
}
