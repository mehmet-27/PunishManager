package com.mehmet_27.punishmanager;

import com.mehmet_27.punishmanager.managers.MessageManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import net.md_5.bungee.api.plugin.Event;

import java.util.Locale;

public class PlayerPunishEvent extends Event {
    MessageManager messageManager = PunishManager.getInstance().getMessageManager();

    private final Punishment punishment;
    private final String announceMessage;
    public PlayerPunishEvent(Punishment punishment){
        this.punishment = punishment;
        String path = punishment.getPunishType().name().toLowerCase(Locale.ENGLISH) + ".announce";
        this.announceMessage = messageManager.getMessage(path);
    }

    public String getAnnounceMessage(){
        return announceMessage;
    }
    public Punishment getPunishment(){
        return punishment;
    }
}
