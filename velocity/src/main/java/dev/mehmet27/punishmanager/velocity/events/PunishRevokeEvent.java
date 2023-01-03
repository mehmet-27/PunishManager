package dev.mehmet27.punishmanager.velocity.events;

import com.velocitypowered.api.event.ResultedEvent;
import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.managers.ConfigManager;
import dev.mehmet27.punishmanager.objects.PunishmentRevoke;

import java.util.Locale;
import java.util.Objects;

public class PunishRevokeEvent implements ResultedEvent<ResultedEvent.GenericResult> {
    private final PunishmentRevoke punishmentRevoke;
    private final String announceMessage;
    private GenericResult result = GenericResult.allowed();

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
    public GenericResult getResult() {
        return result;
    }

    @Override
    public void setResult(GenericResult result) {
        this.result = Objects.requireNonNull(result);
    }
}
