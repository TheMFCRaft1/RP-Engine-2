package me.themfcraft.rpengine.job;

import java.util.UUID;

import me.themfcraft.rpengine.RPEngine;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RPEngine.MODID)
public class PaydaySystem {

    private static final int PAYDAY_INTERVAL_TICKS = 20 * 60 * 30; // 30 minutes
    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        tickCounter++;
        if (tickCounter >= PAYDAY_INTERVAL_TICKS) {
            tickCounter = 0;
            processPayday();
        }
    }

    private static void processPayday() {
        RPEngine.getJobManager(); // Ensure it's reachable
        RPEngine.getEconomyManager();

        for (ServerPlayer player : net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            UUID uuid = player.getUUID();
            JobManager.PlayerJobData jobData = RPEngine.getJobManager().getPlayerJobData(uuid);

            if (jobData != null && jobData.onDuty) {
                Job job = RPEngine.getJobManager().getJob(jobData.jobId);
                Rank rank = job.getRank(jobData.rankId);
                double salary = rank.salary();

                RPEngine.getEconomyManager().addBank(uuid, salary);
                player.sendSystemMessage(Component.literal("Lohnzahlung: $" + salary + " wurden auf dein Konto überwiesen.").withStyle(s -> s.withColor(0xFFFF55)));

                // Save economy after payday
                RPEngine.getEconomyManager().saveEconomy(uuid);
            }
        }
    }
}
