package com.mehmet_27.punishmanager.objects;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Language {
    private final ConfigManager configManager = PunishManager.getInstance().getConfigManager();
    private final String playerName;

    public Language(String playerName) {
        this.playerName = playerName;
    }

    public String getLanguage() {
        ProxiedPlayer player = PunishManager.getInstance().getProxy().getPlayer(playerName);
        return !"CONSOLE".equals(playerName) ? player.getLocale().toString() : configManager.getDefaultLanguage();
    }
}
