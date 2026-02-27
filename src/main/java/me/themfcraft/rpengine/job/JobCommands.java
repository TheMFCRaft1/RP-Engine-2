package me.themfcraft.rpengine.job;

import com.mojang.brigadier.CommandDispatcher;

import me.themfcraft.rpengine.RPEngine;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RPEngine.MODID)
public class JobCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("duty")
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    JobManager manager = RPEngine.getJobManager();

                    if (manager.getPlayerJobData(player.getUUID()) == null) {
                        context.getSource().sendFailure(Component.literal("Du hast keinen Job!"));
                        return 0;
                    }

                    boolean onDuty = manager.toggleDuty(player);
                    String status = onDuty ? "im Dienst" : "außer Dienst";
                    int color = onDuty ? 0x55FF55 : 0xFF5555;

                    player.sendSystemMessage(Component.literal("Du bist nun " + status + ".").withStyle(s -> s.withColor(color)));
                    return 1;
                }));
    }
}
