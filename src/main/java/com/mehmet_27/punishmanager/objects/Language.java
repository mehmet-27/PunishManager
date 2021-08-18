package com.mehmet_27.punishmanager.objects;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.managers.PunishmentManager;

public class Language {
    private final ConfigManager configManager = PunishManager.getInstance().getConfigManager();
    private final PunishmentManager punishmentManager = PunishManager.getInstance().getPunishmentManager();
    private final String playerName;

    public Language(String playerName){
        this.playerName = playerName;
    }
    public String getLanguage(){
        return !"CONSOLE".equals(playerName) ? punishmentManager.getOfflinePlayer(playerName).getLanguage() : configManager.getDefaultLanguage();
    }
}
