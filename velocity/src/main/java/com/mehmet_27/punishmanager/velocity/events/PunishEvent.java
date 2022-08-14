package com.mehmet_27.punishmanager.velocity.events;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.velocitypowered.api.event.ResultedEvent;

import java.util.Locale;
import java.util.Objects;

public class PunishEvent implements ResultedEvent<ResultedEvent.GenericResult> {

    private final Punishment punishment;
    private final String announceMessage;

    private GenericResult result = GenericResult.allowed();

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
    public GenericResult getResult() {
        return result;
    }

    @Override
    public void setResult(GenericResult result) {
        this.result = Objects.requireNonNull(result);
    }
}
