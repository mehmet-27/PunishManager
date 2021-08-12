package com.mehmet_27.punishmanager.events;

import com.mehmet_27.punishmanager.PlayerPunishEvent;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PunishEvent implements Listener {

    @EventHandler
    public void onPunish(PlayerPunishEvent event){
        Punishment punishment = event.getPunishment();
        String announceMessage = event.getAnnounceMessage();
        if (announceMessage == null || announceMessage.isEmpty()) return;
        announceMessage = announceMessage.
                replace("%reason%", punishment.getReason()).
                replace("%operator%", punishment.getOperator()).
                replace("%player%", punishment.getPlayerName()).
                replace("%duration%", punishment.getDuration());
        PunishManager.getInstance().getProxy().broadcast(new TextComponent(announceMessage));
    }
}
