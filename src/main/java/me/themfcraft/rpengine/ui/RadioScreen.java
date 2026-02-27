package me.themfcraft.rpengine.ui;

import me.themfcraft.rpengine.chat.ClientRadioCache;
import me.themfcraft.rpengine.network.NetworkHandler;
import me.themfcraft.rpengine.network.RadioSyncPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class RadioScreen extends Screen {

    private EditBox frequencyInput;
    private boolean isActive = true;

    public RadioScreen() {
        super(Component.literal("Radio Einstellungen"));
        this.isActive = ClientRadioCache.isActive();
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.frequencyInput = new EditBox(this.font, centerX - 50, centerY - 40, 100, 20, Component.literal("Frequenz"));
        this.frequencyInput.setValue(String.valueOf(ClientRadioCache.getFrequency()));
        this.addRenderableWidget(this.frequencyInput);

        this.addRenderableWidget(Button.builder(Component.literal(isActive ? "Ausschalten" : "Einschalten"), (button) -> {
            isActive = !isActive;
            button.setMessage(Component.literal(isActive ? "Ausschalten" : "Einschalten"));
        }).bounds(centerX - 50, centerY - 10, 100, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Speichern"), (button) -> {
            try {
                double freq = Double.parseDouble(this.frequencyInput.getValue());
                ClientRadioCache.update(freq, isActive);
                NetworkHandler.CHANNEL.sendToServer(new RadioSyncPacket(freq, isActive));
                this.onClose();
            } catch (NumberFormatException ignored) {
                this.frequencyInput.setTextColor(0xFF0000);
            }
        }).bounds(centerX - 50, centerY + 20, 100, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Abbrechen"), (button) -> this.onClose())
                .bounds(centerX - 50, centerY + 50, 100, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        graphics.drawString(this.font, "Frequenz (MHz):", this.width / 2 - 50, this.height / 2 - 55, 0xAAAAAA);
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
