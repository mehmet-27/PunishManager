package com.mehmet_27.punishmanager.bungee.events;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.objects.PunishmentRevoke;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

import java.util.Locale;

public class PunishRevokeEvent extends Event implements Cancellable {
    private final PunishmentRevoke punishmentRevoke;
    private final String announceMessage;
    private boolean isCancel;

    public PunishRevokeEvent(PunishmentRevoke punishmentRevoke) {
        this.punishmentRevoke = punishmentRevoke;
        String path = punishmentRevoke.getRevokeType().name().toLowerCase(Locale.ENGLISH) + ".announce";
        ConfigManager configManager = PunishManager.getInstance().getConfigManager();
        this.announceMessage = configManager.getMessage(path);
    }

    public String getAnnounceMessage() {
        return announceMessage;
    }

    public PunishmentRevoke getPunishmentRevoke() {
        return punishmentRevoke;
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
