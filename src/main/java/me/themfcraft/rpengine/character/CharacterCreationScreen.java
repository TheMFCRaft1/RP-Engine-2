package me.themfcraft.rpengine.character;

import me.themfcraft.rpengine.network.CharacterCreationPacket;
import me.themfcraft.rpengine.network.NetworkHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class CharacterCreationScreen extends Screen {

    private EditBox firstNameBox;
    private EditBox lastNameBox;
    private EditBox ageBox;
    private EditBox genderBox;
    private EditBox backstoryBox;

    public CharacterCreationScreen() {
        super(Component.literal("Charakter Erstellung"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.firstNameBox = new EditBox(this.font, centerX - 100, centerY - 80, 200, 20, Component.literal("Vorname"));
        this.firstNameBox.setHint(Component.literal("Vorname"));
        this.addRenderableWidget(this.firstNameBox);

        this.lastNameBox = new EditBox(this.font, centerX - 100, centerY - 50, 200, 20, Component.literal("Nachname"));
        this.lastNameBox.setHint(Component.literal("Nachname"));
        this.addRenderableWidget(this.lastNameBox);

        this.ageBox = new EditBox(this.font, centerX - 100, centerY - 20, 200, 20, Component.literal("Alter"));
        this.ageBox.setHint(Component.literal("Alter"));
        this.addRenderableWidget(this.ageBox);

        this.genderBox = new EditBox(this.font, centerX - 100, centerY + 10, 200, 20, Component.literal("Geschlecht"));
        this.genderBox.setHint(Component.literal("Geschlecht"));
        this.addRenderableWidget(this.genderBox);

        this.backstoryBox = new EditBox(this.font, centerX - 100, centerY + 40, 200, 20, Component.literal("Hintergrundgeschichte"));
        this.backstoryBox.setHint(Component.literal("Kurze Story..."));
        this.addRenderableWidget(this.backstoryBox);

        this.addRenderableWidget(Button.builder(Component.literal("Erstellen"), (button) -> {
            String fName = this.firstNameBox.getValue();
            String lName = this.lastNameBox.getValue();
            String ageStr = this.ageBox.getValue();
            String gender = this.genderBox.getValue();
            String backstory = this.backstoryBox.getValue();

            if (fName.isEmpty() || lName.isEmpty() || ageStr.isEmpty()) {
                return;
            }

            try {
                int age = Integer.parseInt(ageStr);
                NetworkHandler.CHANNEL.sendToServer(new CharacterCreationPacket(fName, lName, age, gender, backstory));
                this.onClose();
            } catch (NumberFormatException ignored) {
            }
        }).bounds(centerX - 100, centerY + 80, 200, 20).build());
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
