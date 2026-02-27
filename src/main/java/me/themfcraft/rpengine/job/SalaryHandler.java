package me.themfcraft.rpengine.job;

import me.themfcraft.rpengine.RPEngine;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

@Mod.EventBusSubscriber(modid = RPEngine.MODID)
public class SalaryHandler {

    private static int tickCounter = 0;
    private static final int SALARY_INTERVAL = 24000; // 20 minutes in ticks

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        tickCounter++;
        if (tickCounter >= SALARY_INTERVAL) {
            tickCounter = 0;
            paySalaries();
        }
    }

    private static void paySalaries() {
        if (ServerLifecycleHooks.getCurrentServer() == null) return;
        
        for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            JobManager.PlayerJobData jobData = RPEngine.getJobManager().getPlayerJobData(player.getUUID());
            if (jobData != null && jobData.onDuty) {
                Job job = RPEngine.getJobManager().getJob(jobData.jobId);
                if (job != null) {
                    Rank rank = job.getRank(jobData.rankId);
                    if (rank != null) {
                        double salary = rank.salary();
                        RPEngine.getEconomyManager().addBank(player.getUUID(), salary);
                        RPEngine.getEconomyManager().saveEconomy(player.getUUID());
                        player.sendSystemMessage(Component.literal("§aZahltag! Dir wurden " + salary + "€ auf dein Bankkonto überwiesen."));
                    }
                }
            }
        }
    }
}
