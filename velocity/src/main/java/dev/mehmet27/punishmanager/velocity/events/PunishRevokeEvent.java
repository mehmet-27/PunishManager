package dev.mehmet27.punishmanager.velocity.events;

import com.velocitypowered.api.event.ResultedEvent;
import dev.mehmet27.punishmanager.objects.PunishmentRevoke;

import java.util.Objects;

public class PunishRevokeEvent implements ResultedEvent<ResultedEvent.GenericResult> {
    private final PunishmentRevoke punishmentRevoke;
    private GenericResult result = GenericResult.allowed();

    public PunishRevokeEvent(PunishmentRevoke punishmentRevoke) {
        this.punishmentRevoke = punishmentRevoke;
    }

    public PunishmentRevoke getPunishmentRevoke() {
        return punishmentRevoke;
    }

    @Override
    public GenericResult getResult() {
        return result;
    }

    @Override
    public void setResult(GenericResult result) {
        this.result = Objects.requireNonNull(result);
    }
}
