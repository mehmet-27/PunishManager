package com.mehmet_27.punishmanager.events;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.DatabaseManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.SettingsChangedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerSettingsChangeEvent implements Listener {
    private final DatabaseManager dataBaseManager = PunishManager.getInstance().getDataBaseManager();

    @EventHandler
    public void onChange(SettingsChangedEvent event) {
        ProxiedPlayer player = event.getPlayer();
        dataBaseManager.updateLanguage(player.getName(), player.getLocale().toString());
    }
}
