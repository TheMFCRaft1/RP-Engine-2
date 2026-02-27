package me.themfcraft.rpengine.network;

import java.util.ArrayList;
import java.util.List;

public class ClientCache {

    private static List<String> ownedKeys = new ArrayList<>();

    public static void setOwnedKeys(List<String> keys) {
        ownedKeys = keys;
    }

    public static List<String> getOwnedKeys() {
        return ownedKeys;
    }
}
