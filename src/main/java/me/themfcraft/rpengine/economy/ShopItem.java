package me.themfcraft.rpengine.economy;

import net.minecraft.world.item.ItemStack;

public record ShopItem(ItemStack item, double price, boolean sellable) {

}
