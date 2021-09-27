package com.mehmet_27.punishmanager.objects;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.managers.StorageManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Locale;

public class PlayerLocale {
    private final PunishManager punishManager = PunishManager.getInstance();
    private final ConfigManager configManager = PunishManager.getInstance().getConfigManager();
    private final String playerName;

    public PlayerLocale(String playerName) {
        this.playerName = playerName;
    }
    public PlayerLocale(ProxiedPlayer player) {
        this.playerName = player.getName();
    }
    public PlayerLocale(OfflinePlayer player) {
        this.playerName = player.getPlayerName();
    }

    public Locale getLocale() {
        return !"CONSOLE".equals(playerName) ? punishManager.getOfflinePlayers().get(playerName).getLocale() : configManager.getDefaultLocale();
    }
}
