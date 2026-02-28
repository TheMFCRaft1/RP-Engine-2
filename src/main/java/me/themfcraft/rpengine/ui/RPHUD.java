package me.themfcraft.rpengine.ui;

import java.util.UUID;

import me.themfcraft.rpengine.RPEngine;
import me.themfcraft.rpengine.character.CharacterData;
import me.themfcraft.rpengine.chat.ClientRadioCache;
import me.themfcraft.rpengine.job.Job;
import me.themfcraft.rpengine.job.JobManager;
import me.themfcraft.rpengine.job.Rank;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RPEngine.MODID, value = Dist.CLIENT)
public class RPHUD {

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay() != VanillaGuiOverlay.HOTBAR.type()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }

        GuiGraphics graphics = event.getGuiGraphics();
        int height = mc.getWindow().getGuiScaledHeight();

        UUID uuid = mc.player.getUUID();

        // Identity
        CharacterData character = RPEngine.getCharacterManager().getCharacter(uuid);
        String name = "Unknown";
        if (character != null) {
            name = character.getFullName();
        } else if (mc.player != null) {
            name = mc.player.getName().getString();
        }

        // Economy
        double cash = RPEngine.getEconomyManager().getCash(uuid);
        double bank = RPEngine.getEconomyManager().getBank(uuid);
        int diamonds = RPEngine.getEconomyManager().getDiamonds(uuid);
        int emeralds = RPEngine.getEconomyManager().getEmeralds(uuid);

        // Job
        JobManager.PlayerJobData jobData = RPEngine.getJobManager().getPlayerJobData(uuid);
        String jobText = "Arbeitslos";
        if (jobData != null) {
            Job job = RPEngine.getJobManager().getJob(jobData.jobId);
            Rank rank = job.getRank(jobData.rankId);
            jobText = job.name() + " - " + rank.name() + (jobData.onDuty ? " §a(Dienst)§r" : "");
        }

        // Draw HUD (Bottom-Left)
        int x = 10;
        int y = height - 100;

        graphics.drawString(mc.font, "§6Identity:§r " + name, x, y, 0xFFFFFF);
        graphics.drawString(mc.font, "§6Job:§r " + jobText, x, y + 10, 0xFFFFFF);
        graphics.drawString(mc.font, "§2Cash:§r $" + String.format("%.2f", cash), x, y + 20, 0xFFFFFF);
        graphics.drawString(mc.font, "§9Bank:§r $" + String.format("%.2f", bank), x, y + 30, 0xFFFFFF);
        graphics.drawString(mc.font, "§bDiamonds:§r " + diamonds, x, y + 40, 0xFFFFFF);
        graphics.drawString(mc.font, "§aEmeralds:§r " + emeralds, x, y + 50, 0xFFFFFF);

        if (ClientRadioCache.isActive()) {
            graphics.drawString(mc.font, "§8[Funk: " + ClientRadioCache.getFrequency() + " MHz]§r", x, y + 65, 0x55FFFF);
        }
    }
}
