package me.themfcraft.rpengine.chat;

public class ClientRadioCache {

    private static double frequency = 100.0;
    private static boolean active = false;

    public static void update(double freq, boolean isActive) {
        frequency = freq;
        active = isActive;
    }

    public static double getFrequency() {
        return frequency;
    }

    public static boolean isActive() {
        return active;
    }
}
