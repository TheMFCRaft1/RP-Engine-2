package me.themfcraft.rpengine.economy;

import java.util.List;

import me.themfcraft.rpengine.network.NetworkHandler;
import me.themfcraft.rpengine.network.ShopPurchasePacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ShopScreen extends Screen {

    private final Shop shop;

    public ShopScreen(Shop shop) {
        super(Component.literal(shop.getDisplayName()));
        this.shop = shop;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = 50;
        List<ShopItem> items = shop.getItems();

        for (int i = 0; i < items.size(); i++) {
            ShopItem item = items.get(i);
            int index = i;

            this.addRenderableWidget(Button.builder(
                    Component.literal(item.item().getHoverName().getString() + " - $" + item.price()),
                    (button) -> {
                        NetworkHandler.CHANNEL.sendToServer(new ShopPurchasePacket(shop.getId(), index));
                    }
            ).bounds(centerX - 100, startY + (i * 25), 200, 20).build());
        }

        this.addRenderableWidget(Button.builder(Component.literal("Schließen"), (button) -> {
            this.onClose();
        }).bounds(centerX - 100, this.height - 40, 200, 20).build());
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
