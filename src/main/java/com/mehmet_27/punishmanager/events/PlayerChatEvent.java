package com.mehmet_27.punishmanager.events;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.Punishment;
import com.mehmet_27.punishmanager.managers.PunishmentManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerChatEvent implements Listener {

    PunishmentManager punishmentManager = PunishManager.getInstance().getPunishmentManager();

    @EventHandler
    public void onChat(ChatEvent event){
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        Punishment punishment = punishmentManager.getPunishment(player.getName());
        if (punishment != null && punishment.playerIsMuted()){
            event.setCancelled(true);
        }
    }
}
