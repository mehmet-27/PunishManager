package com.mehmet_27.punishmanager;

import com.mehmet_27.punishmanager.managers.MessageManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

import java.util.Locale;

public class PlayerPunishEvent extends Event implements Cancellable {
    MessageManager messageManager = PunishManager.getInstance().getMessageManager();

    private boolean isCancel;

    private final Punishment punishment;
    private final String announceMessage;

    public PlayerPunishEvent(Punishment punishment) {
        this.punishment = punishment;
        String path = punishment.getPunishType().name().toLowerCase(Locale.ENGLISH) + ".announce";
        this.announceMessage = messageManager.getMessage(path);
    }

    public String getAnnounceMessage() {
        return announceMessage;
    }

    public Punishment getPunishment() {
        return punishment;
    }

    @Override
    public boolean isCancelled() {
        return isCancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCancel = cancel;
    }
}
