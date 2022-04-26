package com.mehmet_27.punishmanager.bukkit;

import com.mehmet_27.punishmanager.ConfigurationAdapter;
import com.mehmet_27.punishmanager.MethodInterface;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.bukkit.events.PunishEvent;
import com.mehmet_27.punishmanager.bukkit.utils.Utils;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.managers.StorageManager;
import com.mehmet_27.punishmanager.objects.Platform;
import com.mehmet_27.punishmanager.objects.Punishment;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SingleLineChart;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class BukkitMethods implements MethodInterface {

    @Override
    public PMBukkit getPlugin() {
        return PMBukkit.getInstance();
    }

    @Override
    public Platform getPlatform() {
        return Platform.BUKKIT_SPIGOT;
    }

    @Override
    public String getPluginVersion() {
        return getPlugin().getDescription().getVersion();
    }

    @Override
    public String getPluginName() {
        return getPlugin().getDescription().getName();
    }

    @Override
    public Path getPluginsFolder(){
        return getDataFolder().getParent();
    }

    @Override
    public void setupMetrics() {
        Metrics metrics = new Metrics(getPlugin(), 14913);
        metrics.addCustomChart(new SingleLineChart("punishments", () -> PunishManager.getInstance().getStorageManager().getPunishmentsCount()));
    }

    @Override
    public Player getPlayer(String name) {
        return getPlugin().getServer().getPlayer(name);
    }

    @Override
    public Player getPlayer(UUID uuid) {
        return getPlugin().getServer().getPlayer(uuid);
    }

    @Override
    public String getPlayerIp(UUID uuid) {
        Player player = getPlayer(uuid);
        StorageManager storageManager = PunishManager.getInstance().getStorageManager();
        return player != null && player.isOnline() ? Objects.requireNonNull(player.getAddress()).getHostName() : storageManager.getOfflinePlayer(uuid).getPlayerIp();
    }

    @Override
    public ConfigManager getConfigManager() {
        return getPlugin().getConfigManager();
    }

    @Override
    public ConfigurationAdapter getConfig() {
        return getConfigManager().getConfig();
    }

    @Override
    public void sendMessage(Object player, String message) {
        ((CommandSender) player).sendMessage(Utils.color(message));
    }

    @Override
    public Path getDataFolder() {
        return getPlugin().getDataFolder().toPath();
    }

    @Override
    public boolean isOnline(String name) {
        Player player = getPlayer(name);
        return player != null && player.isOnline();
    }

    @Override
    public void callPunishEvent(Punishment punishment) {
        getPlugin().getServer().getPluginManager().callEvent(new PunishEvent(punishment));
    }

    @Override
    public Logger getLogger() {
        return getPlugin().getLogger();
    }

    @Override
    public void scheduleAsync(Runnable task, long delay, long period, TimeUnit unit) {
        getPlugin().getServer().getScheduler().runTaskTimerAsynchronously(getPlugin(), task, unit.toMillis(delay), unit.toMillis(period));
    }

    @Override
    public void runAsync(Runnable task) {
        getPlugin().getServer().getScheduler().runTaskAsynchronously(getPlugin(), task);
    }
}
