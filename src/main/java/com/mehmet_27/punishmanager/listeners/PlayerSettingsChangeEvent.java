package com.mehmet_27.punishmanager.listeners;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.DatabaseManager;
import net.md_5.bungee.api.event.SettingsChangedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerSettingsChangeEvent implements Listener {
    private final PunishManager punishManager = PunishManager.getInstance();
    private final DatabaseManager dataBaseManager = PunishManager.getInstance().getDataBaseManager();

    @EventHandler
    public void onChange(SettingsChangedEvent event) {
        String playerName = event.getPlayer().getName();
        String newLocale = event.getPlayer().getLocale().toString();
        dataBaseManager.updateLanguage(playerName, newLocale);
        punishManager.getOfflinePlayers().get(playerName).setLanguage(newLocale);
    }
}
