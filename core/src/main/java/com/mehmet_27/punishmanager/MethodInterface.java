package com.mehmet_27.punishmanager;

import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.objects.Platform;
import com.mehmet_27.punishmanager.objects.Punishment;

import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public interface MethodInterface {

    Object getPlugin();

    Platform getPlatform();

    String getPluginVersion();

    String getPluginName();

    Path getPluginsFolder();

    void setupMetrics();

    Object getPlayer(String name);

    Object getPlayer(UUID uuid);

    String getPlayerIp(UUID uuid);

    ConfigManager getConfigManager();

    ConfigurationAdapter getConfig();

    void sendMessage(Object player, String message);

    Path getDataFolder();

    boolean isOnline(String name);

    void callPunishEvent(Punishment punishment);

    Logger getLogger();

    void scheduleAsync(Runnable task, long delay, long period, TimeUnit unit);

    void runAsync(Runnable task);
}
