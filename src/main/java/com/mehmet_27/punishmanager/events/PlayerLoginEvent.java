package com.mehmet_27.punishmanager.events;

import com.mehmet_27.punishmanager.Punishment;
import com.mehmet_27.punishmanager.managers.DisconnectManager;
import com.mehmet_27.punishmanager.managers.PunishmentManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerLoginEvent implements Listener {
    PunishmentManager punishmentManager = new PunishmentManager();
    DisconnectManager disconnectManager = new DisconnectManager();
    @EventHandler
    public void onLogin(PostLoginEvent event){
        ProxiedPlayer player = event.getPlayer();
        if (punishmentManager.PlayerIsBanned(player.getName())){
            Punishment punishment = punishmentManager.getPunishment(player.getName());
            disconnectManager.DisconnectPlayer(punishment);
        }
    }

}
