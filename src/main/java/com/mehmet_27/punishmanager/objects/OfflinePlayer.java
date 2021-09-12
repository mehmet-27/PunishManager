package com.mehmet_27.punishmanager.objects;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class OfflinePlayer {
    private final ConfigManager configManager = PunishManager.getInstance().getConfigManager();
    private String playerName, uuid, playerIp, language;

    public OfflinePlayer(ProxiedPlayer player) {
        this.uuid = player.getUniqueId().toString();
        this.playerName = player.getName();
        this.playerIp = Utils.getPlayerIp(player.getName());
        this.language = PunishManager.getInstance().getConfigManager().getDefaultLanguage();
    }
    public OfflinePlayer(String uuid, String playerName, String playerIp, String language) {
        this.uuid = uuid;
        this.playerName = playerName;
        this.playerIp = playerIp;
        this.language = language;
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

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPlayerIp() {
        return playerIp;
    }

    public void setPlayerIp(String playerIp) {
        this.playerIp = playerIp;
    }

    public String getLanguage() {
        return language != null ? language : configManager.getDefaultLanguage();
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
