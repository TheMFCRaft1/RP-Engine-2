package me.themfcraft.rpengine.economy;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import me.themfcraft.rpengine.RPEngine;
import me.themfcraft.rpengine.network.NetworkHandler;
import me.themfcraft.rpengine.network.OpenShopPacket;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class EconomyCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("deposit")
                .then(Commands.literal("diamonds")
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                .executes(context -> deposit(context.getSource().getPlayerOrException(), "diamonds", IntegerArgumentType.getInteger(context, "amount")))))
                .then(Commands.literal("emeralds")
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                .executes(context -> deposit(context.getSource().getPlayerOrException(), "emeralds", IntegerArgumentType.getInteger(context, "amount")))))
        );

        dispatcher.register(Commands.literal("withdraw")
                .then(Commands.literal("diamonds")
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                .executes(context -> withdraw(context.getSource().getPlayerOrException(), "diamonds", IntegerArgumentType.getInteger(context, "amount")))))
                .then(Commands.literal("emeralds")
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                .executes(context -> withdraw(context.getSource().getPlayerOrException(), "emeralds", IntegerArgumentType.getInteger(context, "amount")))))
        );

        dispatcher.register(Commands.literal("shop")
                .then(Commands.argument("id", StringArgumentType.string())
                        .executes(context -> openShop(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "id"))))
        );

        dispatcher.register(Commands.literal("setshop")
                .then(Commands.argument("shopId", StringArgumentType.string())
                        .executes(context -> setShop(context.getSource().getPlayerOrException(), StringArgumentType.getString(context, "shopId"))))
        );
    }

    private static int deposit(ServerPlayer player, String type, int amount) {
        ItemStack itemToFind = type.equals("diamonds") ? new ItemStack(Items.DIAMOND) : new ItemStack(Items.EMERALD);
        int count = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (ItemStack.isSameItem(stack, itemToFind)) {
                count += stack.getCount();
            }
        }

        if (count < amount) {
            player.sendSystemMessage(Component.literal("§cDu hast nicht genug physische " + type + "!"));
            return 0;
        }

        int remaining = amount;
        for (int i = 0; i < player.getInventory().getContainerSize() && remaining > 0; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (ItemStack.isSameItem(stack, itemToFind)) {
                int toTake = Math.min(stack.getCount(), remaining);
                stack.shrink(toTake);
                remaining -= toTake;
            }
        }

        if (type.equals("diamonds")) {
            RPEngine.getEconomyManager().addDiamonds(player.getUUID(), amount);
        } else {
            RPEngine.getEconomyManager().addEmeralds(player.getUUID(), amount);
        }

        RPEngine.getEconomyManager().saveEconomy(player.getUUID());
        player.sendSystemMessage(Component.literal("§aDu hast " + amount + " " + type + " eingezahlt."));
        return 1;
    }

    private static int withdraw(ServerPlayer player, String type, int amount) {
        int balance = type.equals("diamonds") ? RPEngine.getEconomyManager().getDiamonds(player.getUUID()) : RPEngine.getEconomyManager().getEmeralds(player.getUUID());

        if (balance < amount) {
            player.sendSystemMessage(Component.literal("§cDu hast nicht genug virtuelle " + type + "!"));
            return 0;
        }

        ItemStack itemToAdd = type.equals("diamonds") ? new ItemStack(Items.DIAMOND, amount) : new ItemStack(Items.EMERALD, amount);
        if (player.getInventory().add(itemToAdd)) {
            if (type.equals("diamonds")) {
                RPEngine.getEconomyManager().removeDiamonds(player.getUUID(), amount);
            } else {
                RPEngine.getEconomyManager().removeEmeralds(player.getUUID(), amount);
            }
            RPEngine.getEconomyManager().saveEconomy(player.getUUID());
            player.sendSystemMessage(Component.literal("§aDu hast " + amount + " " + type + " abgehoben."));
            return 1;
        } else {
            player.sendSystemMessage(Component.literal("§cDein Inventar ist voll!"));
            return 0;
        }
    }

    private static int openShop(ServerPlayer player, String shopId) {
        Shop shop = RPEngine.getShopManager().getShop(shopId);
        if (shop == null) {
            player.sendSystemMessage(Component.literal("§cShop '" + shopId + "' existiert nicht!"));
            return 0;
        }

        NetworkHandler.CHANNEL.send(net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player), new OpenShopPacket(shopId));
        return 1;
    }

    private static int setShop(ServerPlayer player, String shopId) {
        if (!player.hasPermissions(2)) {
            player.sendSystemMessage(Component.literal("§cKeine Berechtigung!"));
            return 0;
        }

        if (RPEngine.getShopManager().getShop(shopId) == null) {
            player.sendSystemMessage(Component.literal("§cShop '" + shopId + "' existiert nicht!"));
            return 0;
        }

        net.minecraft.world.phys.HitResult hit = player.pick(5.0D, 0.0F, false);
        if (hit.getType() == net.minecraft.world.phys.HitResult.Type.ENTITY) {
            net.minecraft.world.phys.EntityHitResult entityHit = (net.minecraft.world.phys.EntityHitResult) hit;
            net.minecraft.world.entity.Entity entity = entityHit.getEntity();

            RPEngine.getShopManager().registerNpcShop(entity.getUUID(), shopId);
            player.sendSystemMessage(Component.literal("§aNPC wurde erfolgreich dem Shop '" + shopId + "' zugewiesen."));
            return 1;
        } else {
            player.sendSystemMessage(Component.literal("§cBitte schaue einen NPC an!"));
            return 0;
        }
    }
}
