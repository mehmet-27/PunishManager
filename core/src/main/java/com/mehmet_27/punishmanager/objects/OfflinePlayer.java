package com.mehmet_27.punishmanager.objects;

import com.mehmet_27.punishmanager.PunishManager;

import java.util.Locale;
import java.util.UUID;

public class OfflinePlayer {
    private final Locale defaultLocale = PunishManager.getInstance().getConfigManager().getDefaultLocale();
    private String name, playerIp;
    private UUID uuid;
    private Locale locale;

    public OfflinePlayer(UUID uuid, String name, String playerIp, Locale locale) {
        this.uuid = uuid;
        this.name = name;
        this.playerIp = playerIp;
        this.locale = locale;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public void setUniqueId(UUID uuid) {
        this.uuid = uuid;
    }

    public String getPlayerIp() {
        return playerIp;
    }

    public void setPlayerIp(String playerIp) {
        this.playerIp = playerIp;
    }

    public Locale getLocale() {
        return locale != null ? locale : defaultLocale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
