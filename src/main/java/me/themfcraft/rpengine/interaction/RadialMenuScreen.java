package me.themfcraft.rpengine.interaction;

import java.util.List;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class RadialMenuScreen extends Screen {

    private final List<RadialOption> options;
    private int selectedIndex = -1;

    public RadialMenuScreen(List<RadialOption> options) {
        super(Component.literal("Interaktionsmenü"));
        this.options = options;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        double angleStep = (2 * Math.PI) / options.size();
        double mouseAngle = Math.atan2(mouseY - centerY, mouseX - centerX);
        if (mouseAngle < 0) {
            mouseAngle += 2 * Math.PI;
        }

        double distSq = Math.pow(mouseX - centerX, 2) + Math.pow(mouseY - centerY, 2);
        boolean isMouseInRing = distSq > 400 && distSq < 10000; // Inner radius 20, outer 100

        // Draw background circle
        graphics.fill(centerX - 110, centerY - 110, centerX + 110, centerY + 110, 0x55000000);

        selectedIndex = -1;
        if (isMouseInRing) {
            selectedIndex = (int) Math.round(mouseAngle / angleStep) % options.size();
            if (selectedIndex < 0) {
                selectedIndex += options.size();
            }
        }

        // Highlight selected slice (simplified)
        if (selectedIndex != -1) {
            double startAngle = (selectedIndex * angleStep) - (angleStep / 2);
            double endAngle = startAngle + angleStep;
            // Simplified slice highlighting - in a real mod we'd use a custom shader or arc drawing
        }

        for (int i = 0; i < options.size(); i++) {
            double angle = i * angleStep;
            int x = (int) (centerX + Math.cos(angle) * 70);
            int y = (int) (centerY + Math.sin(angle) * 70);

            int color = (i == selectedIndex) ? 0xFF55FF55 : 0xFFFFFFFF;
            graphics.drawCenteredString(this.font, options.get(i).icon(), x, y - 5, 0xFFFFFF);
            graphics.drawCenteredString(this.font, options.get(i).label(), x, y + 5, color);
        }

        // Center help text
        String help = selectedIndex != -1 ? options.get(selectedIndex).label().getString() : "Wähle Aktion";
        graphics.drawCenteredString(this.font, help, centerX, centerY - 5, 0xFFAA00);

        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (selectedIndex != -1 && button == 0) {
            options.get(selectedIndex).action().accept(null);
            this.onClose();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
