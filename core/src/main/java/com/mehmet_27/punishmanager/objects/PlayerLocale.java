package com.mehmet_27.punishmanager.objects;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.ConfigManager;

import java.util.Locale;

public class PlayerLocale {
    private final ConfigManager configManager = PunishManager.getInstance().getConfigManager();
    private final String playerName;

    public PlayerLocale(String playerName) {
        this.playerName = playerName;
    }

    public Locale getLocale() {
        return !"CONSOLE".equals(playerName) ? PunishManager.getInstance().getOfflinePlayers().get(playerName).getLocale() : configManager.getDefaultLocale();
    }
}
