package com.mehmet_27.punishmanager;

import com.mehmet_27.punishmanager.managers.CommandManager;
import com.mehmet_27.punishmanager.objects.OfflinePlayer;
import com.mehmet_27.punishmanager.objects.Platform;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.objects.PunishmentRevoke;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public interface MethodProvider {

    Object getPlugin();

    Platform getPlatform();

    String getPluginVersion();

    String getPluginName();

    Path getPluginsFolder();

    void setupMetrics();

    Object getPlayer(String name);

    Object getPlayer(UUID uuid);

    String getPlayerIp(UUID uuid);

    void sendMessage(@Nullable UUID uuid, String message);

    Path getDataFolder();

    boolean isOnline(String name);

    void callPunishEvent(Punishment punishment);

    void callPunishRevokeEvent(PunishmentRevoke punishmentRevoke);

    Logger getLogger();

    void scheduleAsync(Runnable task, long delay, long period, TimeUnit unit);

    boolean isOnline(UUID uuid);

    String getServer(UUID uuid);

    void runAsync(Runnable task);

    CommandManager getCommandManager();

    void openMainInventory(Object player);

    void openPunishFrame(Object sender, OfflinePlayer player);

    void kickPlayer(UUID uuid, String message);
}
