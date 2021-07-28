package com.mehmet_27.punishmanager.events;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.Punishment;
import com.mehmet_27.punishmanager.managers.PunishmentManager;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerChatEvent implements Listener {

    PunishmentManager punishmentManager = PunishManager.getInstance().getPunishmentManager();

    @EventHandler
    public void onChat(ChatEvent event) {
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        Punishment punishment = punishmentManager.getPunishment(player.getName(), "mute");
        if (punishment == null || !punishment.playerIsMuted()) {
            return;
        }
        if (!punishment.isStillPunished()) return;
        event.setCancelled(true);
        Utils.sendMuteMessage(punishment);
    }
}
