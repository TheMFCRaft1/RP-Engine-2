package me.themfcraft.rpengine.ui;

import java.util.UUID;

import me.themfcraft.rpengine.RPEngine;
import me.themfcraft.rpengine.character.CharacterData;
import me.themfcraft.rpengine.job.Job;
import me.themfcraft.rpengine.job.JobManager;
import me.themfcraft.rpengine.job.Rank;
import me.themfcraft.rpengine.network.ClientCache;
import me.themfcraft.rpengine.network.NetworkHandler;
import me.themfcraft.rpengine.network.RequestKeysPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class CharacterManagementScreen extends Screen {

    public CharacterManagementScreen() {
        super(Component.literal("Charakter Management"));
    }

    @Override
    protected void init() {
        // Request keys from server every time we open
        NetworkHandler.CHANNEL.sendToServer(new RequestKeysPacket());

        int centerX = this.width / 2;
        this.addRenderableWidget(Button.builder(Component.literal("Schließen"), (button) -> this.onClose())
                .bounds(centerX - 100, this.height - 40, 200, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);

        Minecraft mc = Minecraft.getInstance();
        var localPlayer = mc.player;
        if (localPlayer == null) {
            return;
        }
        UUID uuid = localPlayer.getUUID();

        // Left Side: Identity & Bio
        int leftX = 50;
        int topY = 50;
        CharacterData character = RPEngine.getCharacterManager().getCharacter(uuid);
        String playerName = mc.player != null ? mc.player.getName().getString() : "Unknown";
        String name = character != null ? character.getFullName() : playerName;

        graphics.drawString(this.font, "§6§lIdentität§r", leftX, topY, 0xFFFFFF);
        graphics.drawString(this.font, "Name: " + name, leftX, topY + 15, 0xFFFFFF);
        if (character != null) {
            graphics.drawString(this.font, "Alter: " + character.age(), leftX, topY + 25, 0xFFFFFF);
            graphics.drawString(this.font, "Geschlecht: " + character.gender(), leftX, topY + 35, 0xFFFFFF);
            graphics.drawString(this.font, "Hintergrund:", leftX, topY + 50, 0xFFFFFF);
            // Simple wrap for backstory
            String bio = character.backstory();
            int bioY = topY + 60;
            for (FormattedCharSequence line : mc.font.split(Component.literal(bio), 150)) {
                graphics.drawString(this.font, line, leftX + 5, bioY, 0xAAAAAA);
                bioY += 10;
            }
        }

        // Right Side: Economy & Job
        int rightX = this.width - 200;
        graphics.drawString(this.font, "§2§lFinanzen§r", rightX, topY, 0xFFFFFF);
        graphics.drawString(this.font, "Bargeld: $" + String.format("%.2f", RPEngine.getEconomyManager().getCash(uuid)), rightX, topY + 15, 0xFFFFFF);
        graphics.drawString(this.font, "Bank: $" + String.format("%.2f", RPEngine.getEconomyManager().getBank(uuid)), rightX, topY + 25, 0xFFFFFF);
        graphics.drawString(this.font, "Diamanten: " + RPEngine.getEconomyManager().getDiamonds(uuid), rightX, topY + 35, 0xFFFFFF);
        graphics.drawString(this.font, "Smaragde: " + RPEngine.getEconomyManager().getEmeralds(uuid), rightX, topY + 45, 0xFFFFFF);

        int jobY = topY + 70;
        graphics.drawString(this.font, "§b§lBeruf§r", rightX, jobY, 0xFFFFFF);
        JobManager.PlayerJobData jobData = RPEngine.getJobManager().getPlayerJobData(uuid);
        if (jobData != null) {
            Job job = RPEngine.getJobManager().getJob(jobData.jobId);
            Rank rank = job.getRank(jobData.rankId);
            graphics.drawString(this.font, "Job: " + job.name(), rightX, jobY + 15, 0xFFFFFF);
            graphics.drawString(this.font, "Rang: " + rank.name(), rightX, jobY + 25, 0xFFFFFF);
            graphics.drawString(this.font, "Status: " + (jobData.onDuty ? "§aIm Dienst§r" : "§cAußer Dienst§r"), rightX, jobY + 35, 0xFFFFFF);
        } else {
            graphics.drawString(this.font, "Arbeitslos", rightX, jobY + 15, 0xFFFFFF);
        }

        // Bottom Middle: Keys
        int middleX = this.width / 2 - 75;
        int middleY = this.height - 150;
        graphics.drawString(this.font, "§7§lSchlüsselbund§r", middleX, middleY, 0xFFFFFF);
        int keyY = middleY + 15;
        for (String key : ClientCache.getOwnedKeys()) {
            graphics.drawString(this.font, "- " + key, middleX, keyY, 0xCCCCCC);
            keyY += 10;
        }

        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
