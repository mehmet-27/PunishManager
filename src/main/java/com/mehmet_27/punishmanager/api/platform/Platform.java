package com.mehmet_27.punishmanager.api.platform;

import org.jetbrains.annotations.NotNull;

public interface Platform {

    enum Type {
        BUKKIT("Bukkit"),
        BUNGEECORD("BungeeCord");

        private final String friendlyName;

        Type(String friendlyName) {
            this.friendlyName = friendlyName;
        }

        public @NotNull String getFriendlyName() {
            return this.friendlyName;
        }
    }
}
