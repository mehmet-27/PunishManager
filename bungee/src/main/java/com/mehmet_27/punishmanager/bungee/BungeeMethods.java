package com.mehmet_27.punishmanager.bungee;

import com.mehmet_27.punishmanager.MethodProvider;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.bungee.events.PunishEvent;
import com.mehmet_27.punishmanager.bungee.inventory.InventoryDrawer;
import com.mehmet_27.punishmanager.bungee.inventory.inventories.MainFrame;
import com.mehmet_27.punishmanager.bungee.inventory.inventories.PunishFrame;
import com.mehmet_27.punishmanager.managers.CommandManager;
import com.mehmet_27.punishmanager.managers.StorageManager;
import com.mehmet_27.punishmanager.objects.OfflinePlayer;
import com.mehmet_27.punishmanager.objects.Platform;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bstats.bungeecord.Metrics;
import org.bstats.charts.SingleLineChart;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class BungeeMethods implements MethodProvider {

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
    public Path getPluginsFolder() {
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
    public void sendMessage(@Nullable UUID uuid, String message) {
        if (message.isEmpty()) return;
        if (uuid == null) {
            getPlugin().getProxy().getConsole().sendMessage(new TextComponent(Utils.color(message)));
        } else {
            ProxiedPlayer player = getPlayer(uuid);
            if (player != null && player.isConnected()) {
                player.sendMessage(new TextComponent(Utils.color(message)));
            }
        }
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

    @Override
    public boolean isOnline(UUID uuid) {
        ProxiedPlayer player = getPlayer(uuid);
        return player != null && player.isConnected();
    }

    @Override
    public String getServer(UUID uuid) {
        return getPlayer(uuid).getServer().getInfo().getName();
    }

    @Override
    public void runAsync(Runnable task) {
        getPlugin().getProxy().getScheduler().runAsync(getPlugin(), task);
    }

    @Override
    public CommandManager getCommandManager() {
        return getPlugin().getCommandManager();
    }

    @Override
    public void openMainInventory(Object player) {
        InventoryDrawer.open(new MainFrame((ProxiedPlayer) player));
    }

    @Override
    public void openPunishFrame(Object sender, OfflinePlayer player) {
        InventoryDrawer.open(new PunishFrame((ProxiedPlayer) sender, player));
    }

    @Override
    public void kickPlayer(UUID uuid, String message) {
        ProxiedPlayer onlinePlayer = getPlayer(uuid);
        if (onlinePlayer.isConnected()) {
            onlinePlayer.disconnect(TextComponent.fromLegacyText(message));
        }
    }
}
