package me.themfcraft.rpengine.economy;

import java.util.ArrayList;
import java.util.List;

public class Shop {

    private final String id;
    private final String displayName;
    private final List<ShopItem> items = new ArrayList<>();

    public Shop(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void addItem(ShopItem item) {
        items.add(item);
    }

    public List<ShopItem> getItems() {
        return items;
    }
}
