package com.mehmet_27.punishmanager.objects;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Locale;
import java.util.UUID;

public class OfflinePlayer {
    private final ConfigManager configManager = PunishManager.getInstance().getConfigManager();
    private String playerName, uuid, playerIp;
    private Locale locale;

    public OfflinePlayer(ProxiedPlayer player) {
        this.uuid = player.getUniqueId().toString();
        this.playerName = player.getName();
        this.playerIp = Utils.getPlayerIp(player.getUniqueId());
        this.locale = PunishManager.getInstance().getConfigManager().getDefaultLocale();
    }
    public OfflinePlayer(String uuid, String playerName, String playerIp, Locale locale) {
        this.uuid = uuid;
        this.playerName = playerName;
        this.playerIp = playerIp;
        this.locale = locale;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public UUID getUniqueId() {
        return UUID.fromString(uuid);
    }

    public void setUniqueId(String uuid) {
        this.uuid = uuid;
    }

    public String getPlayerIp() {
        return playerIp;
    }

    public void setPlayerIp(String playerIp) {
        this.playerIp = playerIp;
    }

    public Locale getLocale() {
        return locale != null ? locale : configManager.getDefaultLocale();
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
