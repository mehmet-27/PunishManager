package com.mehmet_27.punishmanager.bungee;

import com.mehmet_27.punishmanager.MethodInterface;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.bungee.Utils.Utils;
import com.mehmet_27.punishmanager.bungee.events.PunishEvent;
import com.mehmet_27.punishmanager.bungee.managers.BungeeConfigManager;
import com.mehmet_27.punishmanager.managers.StorageManager;
import com.mehmet_27.punishmanager.objects.Platform;
import com.mehmet_27.punishmanager.objects.Punishment;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bstats.bungeecord.Metrics;
import org.bstats.charts.SingleLineChart;

import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class BungeeMethods implements MethodInterface {

    @Override
    public PMBungee getPlugin() {
        return PMBungee.getInstance();
    }

    @Override
    public Platform getPlatform() {
        return Platform.BUNGEECORD;
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
        return getPlugin().getProxy().getPluginsFolder().toPath();
    }

    @Override
    public void setupMetrics() {
        Metrics metrics = new Metrics(getPlugin(), 14772);
        metrics.addCustomChart(new SingleLineChart("punishments", () -> PunishManager.getInstance().getStorageManager().getPunishmentsCount()));
    }

    @Override
    public ProxiedPlayer getPlayer(String name) {
        return getPlugin().getProxy().getPlayer(name);
    }

    @Override
    public ProxiedPlayer getPlayer(UUID uuid) {
        return getPlugin().getProxy().getPlayer(uuid);
    }

    @Override
    public String getPlayerIp(UUID uuid) {
        ProxiedPlayer player = getPlayer(uuid);
        StorageManager storageManager = PunishManager.getInstance().getStorageManager();
        return player != null && player.isConnected() ? player.getSocketAddress().toString().substring(1).split(":")[0] : storageManager.getOfflinePlayer(uuid).getPlayerIp();
    }

    @Override
    public BungeeConfigManager getConfigManager() {
        return getPlugin().getConfigManager();
    }

    @Override
    public BungeeConfiguration getConfig() {
        return getConfigManager().getConfig();
    }

    @Override
    public void sendMessage(Object player, String message) {
        ((CommandSender) player).sendMessage(new TextComponent(Utils.color(message)));
    }

    @Override
    public Path getDataFolder() {
        return getPlugin().getDataFolder().toPath();
    }

    @Override
    public boolean isOnline(String name) {
        return getPlugin().getProxy().getPlayer(name).isConnected();
    }

    @Override
    public void callPunishEvent(Punishment punishment) {
        getPlugin().getProxy().getPluginManager().callEvent(new PunishEvent(punishment));
    }

    @Override
    public Logger getLogger() {
        return getPlugin().getLogger();
    }

    @Override
    public void scheduleAsync(Runnable task, long delay, long period, TimeUnit unit) {
        getPlugin().getProxy().getScheduler().schedule(getPlugin(), task, delay, period, unit);
    }
}
