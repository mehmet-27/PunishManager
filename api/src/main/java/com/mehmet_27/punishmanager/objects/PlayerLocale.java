package com.mehmet_27.punishmanager.objects;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.UUID;

public class PlayerLocale {
    private final ConfigManager configManager = PunishManager.getInstance().getConfigManager();
    private final @Nullable
    UUID uuid;

    public PlayerLocale(@Nullable UUID uuid) {
        this.uuid = uuid;
    }

    public Locale getLocale() {
        if (uuid != null) {
            OfflinePlayer offlinePlayer = PunishManager.getInstance().getOfflinePlayers().get(uuid);
            return offlinePlayer != null ? offlinePlayer.getLocale() : configManager.getDefaultLocale();
        } else {
            return configManager.getDefaultLocale();
        }
    }
}
