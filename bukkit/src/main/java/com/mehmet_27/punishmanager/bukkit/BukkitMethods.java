package com.mehmet_27.punishmanager.bukkit;

import com.mehmet_27.punishmanager.MethodProvider;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.bukkit.events.PunishEvent;
import com.mehmet_27.punishmanager.bukkit.events.PunishRevokeEvent;
import com.mehmet_27.punishmanager.bukkit.inventory.InventoryDrawer;
import com.mehmet_27.punishmanager.bukkit.inventory.inventories.MainFrame;
import com.mehmet_27.punishmanager.bukkit.inventory.inventories.PunishFrame;
import com.mehmet_27.punishmanager.managers.CommandManager;
import com.mehmet_27.punishmanager.managers.StorageManager;
import com.mehmet_27.punishmanager.objects.OfflinePlayer;
import com.mehmet_27.punishmanager.objects.Platform;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.objects.PunishmentRevoke;
import com.mehmet_27.punishmanager.utils.Utils;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SingleLineChart;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class BukkitMethods implements MethodProvider {

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
    public Path getPluginsFolder() {
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
    public void sendMessage(@Nullable UUID uuid, String message) {
        if (message.isEmpty()) return;
        if (uuid == null) {
            getPlugin().getServer().getConsoleSender().sendMessage(Utils.color(message));
        } else {
            Player player = getPlayer(uuid);
            if (player != null && player.isOnline()) {
                player.sendMessage(Utils.color(message));
            }
        }
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
    public void callPunishRevokeEvent(PunishmentRevoke punishmentRevoke) {
        getPlugin().getServer().getPluginManager().callEvent(new PunishRevokeEvent(punishmentRevoke));
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
    public boolean isOnline(UUID uuid) {
        Player player = getPlayer(uuid);
        return player != null && player.isOnline();
    }

    @Override
    public String getServer(UUID uuid) {
        return getPlayer(uuid).getServer().getName();
    }

    @Override
    public void runAsync(Runnable task) {
        getPlugin().getServer().getScheduler().runTaskAsynchronously(getPlugin(), task);
    }

    @Override
    public CommandManager getCommandManager() {
        return getPlugin().getCommandManager();
    }

    @Override
    public void openMainInventory(Object player) {
        InventoryDrawer.open(new MainFrame((Player) player));
    }

    @Override
    public void openPunishFrame(Object sender, OfflinePlayer player) {
        InventoryDrawer.open(new PunishFrame((Player) sender, player));
    }

    @Override
    public void kickPlayer(UUID uuid, String message) {
        Player onlinePlayer = getPlayer(uuid);
        if (onlinePlayer.isOnline()) {
            onlinePlayer.kickPlayer(message);
        }
    }
}
