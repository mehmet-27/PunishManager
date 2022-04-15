package com.mehmet_27.punishmanager.objects;

public enum Platform {
    BUKKIT("Bukkit"),
    BUNGEECORD("BungeeCord");

    private final String friendlyName;

    Platform(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getFriendlyName() {
        return this.friendlyName;
    }
}
