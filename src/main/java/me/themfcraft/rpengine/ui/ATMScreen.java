package me.themfcraft.rpengine.ui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ATMScreen extends Screen {

    private EditBox amountInput;

    public ATMScreen() {
        super(Component.literal("Geldautomat"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.amountInput = new EditBox(this.font, centerX - 50, centerY - 40, 100, 20, Component.literal("Betrag"));
        this.addRenderableWidget(this.amountInput);

        this.addRenderableWidget(Button.builder(Component.literal("Einzahlen"), (button) -> {
            // TODO: Send packet to server
        }).bounds(centerX - 110, centerY, 100, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Auszahlen"), (button) -> {
            // TODO: Send packet to server
        }).bounds(centerX + 10, centerY, 100, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Schließen"), (button) -> {
            this.onClose();
        }).bounds(centerX - 50, centerY + 30, 100, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
