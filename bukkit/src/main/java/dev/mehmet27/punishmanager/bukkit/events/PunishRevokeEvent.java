package dev.mehmet27.punishmanager.bukkit.events;

import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.managers.ConfigManager;
import dev.mehmet27.punishmanager.objects.PunishmentRevoke;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class PunishRevokeEvent extends Event implements Cancellable {
    private final PunishmentRevoke punishmentRevoke;
    private final String announceMessage;
    private static final HandlerList handlerList = new HandlerList();
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

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
