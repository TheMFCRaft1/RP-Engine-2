package me.themfcraft.rpengine.interaction;

import java.util.function.Consumer;

import net.minecraft.network.chat.Component;

public record RadialOption(Component label, String icon, Consumer<Void> action) {

}
