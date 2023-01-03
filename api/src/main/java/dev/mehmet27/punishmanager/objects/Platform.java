package dev.mehmet27.punishmanager.objects;

public enum Platform {
    BUKKIT_SPIGOT("Bukkit/Spigot"),
    BUNGEECORD("BungeeCord"),
    VELOCITY("Velocity");

    private final String friendlyName;

    Platform(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getFriendlyName() {
        return this.friendlyName;
    }
}
