package dev.mehmet27.punishmanager.bungee.events;

import dev.mehmet27.punishmanager.objects.PunishmentRevoke;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

public class PunishRevokeEvent extends Event implements Cancellable {
    private final PunishmentRevoke punishmentRevoke;
    private boolean isCancel;

    public PunishRevokeEvent(PunishmentRevoke punishmentRevoke) {
        this.punishmentRevoke = punishmentRevoke;
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
