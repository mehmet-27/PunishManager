package dev.mehmet27.punishmanager.bungee.events;

import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.managers.ConfigManager;
import dev.mehmet27.punishmanager.objects.Punishment;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

import java.util.Locale;

public class PunishEvent extends Event implements Cancellable {
    private final Punishment punishment;
    private final String announceMessage;
    private boolean isCancel;

    public PunishEvent(Punishment punishment) {
        this.punishment = punishment;
        String path = punishment.getPunishType().name().toLowerCase(Locale.ENGLISH) + ".announce";
        ConfigManager configManager = PunishManager.getInstance().getConfigManager();
        this.announceMessage = configManager.getMessage(path);
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

