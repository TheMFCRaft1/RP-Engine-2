package me.themfcraft.rpengine.ui;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;

import me.themfcraft.rpengine.RPEngine;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RPEngine.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class KeyBindingHandler {

    public static final KeyMapping MENU_KEY = new KeyMapping(
            "key.rpengine.menu",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "category.rpengine"
    );

    public static final KeyMapping INTERACTION_KEY = new KeyMapping(
            "key.rpengine.interaction",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_X,
            "category.rpengine"
    );

    @SubscribeEvent
    public static void onKeyRegister(RegisterKeyMappingsEvent event) {
        event.register(MENU_KEY);
        event.register(INTERACTION_KEY);
    }
}
