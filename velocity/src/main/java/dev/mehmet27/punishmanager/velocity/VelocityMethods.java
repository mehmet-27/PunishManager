package dev.mehmet27.punishmanager.velocity;

import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import dev.mehmet27.punishmanager.MethodProvider;
import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.managers.CommandManager;
import dev.mehmet27.punishmanager.managers.StorageManager;
import dev.mehmet27.punishmanager.objects.OfflinePlayer;
import dev.mehmet27.punishmanager.objects.Platform;
import dev.mehmet27.punishmanager.objects.Punishment;
import dev.mehmet27.punishmanager.objects.PunishmentRevoke;
import dev.mehmet27.punishmanager.utils.Utils;
import dev.mehmet27.punishmanager.velocity.events.PunishEvent;
import dev.mehmet27.punishmanager.velocity.events.PunishRevokeEvent;
import dev.mehmet27.punishmanager.velocity.inventory.InventoryDrawer;
import dev.mehmet27.punishmanager.velocity.inventory.inventories.MainFrame;
import dev.mehmet27.punishmanager.velocity.inventory.inventories.PunishFrame;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class VelocityMethods implements MethodProvider {

    @Override
    public PMVelocity getPlugin() {
        return PMVelocity.getInstance();
    }

    @Override
    public Platform getPlatform() {
        return Platform.VELOCITY;
    }

    @Override
    public String getPluginVersion() {
        Optional<PluginContainer> plugin = getPlugin().getServer().getPluginManager().getPlugin("punishmanager");
        return plugin.map(pluginContainer -> pluginContainer.getDescription().getVersion().orElse("1.0.0")).orElse("1.0.0");
    }

    @Override
    public String getPluginName() {
        Optional<PluginContainer> plugin = getPlugin().getServer().getPluginManager().getPlugin("punishmanager");
        return plugin.map(pluginContainer -> pluginContainer.getDescription().getName().orElse("PunishManager")).orElse("PunishManager");
    }

    @Override
    public Path getPluginsFolder() {
        return getPlugin().getDataDirectory().getParent();
    }

    @Override
    public void setupMetrics() {
        // No need
    }

    @Override
    public Player getPlayer(String name) {
        return getPlugin().getServer().getPlayer(name).orElse(null);
    }

    @Override
    public Player getPlayer(UUID uuid) {
        return getPlugin().getServer().getPlayer(uuid).orElse(null);
    }

    @Override
    public String getPlayerIp(UUID uuid) {
        Player player = getPlayer(uuid);
        StorageManager storageManager = PunishManager.getInstance().getStorageManager();
        return player != null && player.isActive() ? player.getRemoteAddress().toString().substring(1).split(":")[0] : storageManager.getOfflinePlayer(uuid).getPlayerIp();
    }

    @Override
    public void sendMessage(@Nullable UUID uuid, String message) {
        if (message.isEmpty()) return;
        if (uuid == null) {
            getPlugin().getServer().getConsoleCommandSource().sendMessage(Component.text(Utils.color(message)));
        } else {
            Player player = getPlayer(uuid);
            if (player != null && player.isActive()) {
                player.sendMessage(Component.text(Utils.color(message)));
            }
        }
    }

    @Override
    public Path getDataFolder() {
        return getPlugin().getDataDirectory();
    }

    @Override
    public boolean isOnline(String name) {
        return getPlayer(name).isActive();
    }

    @Override
    public void callPunishEvent(Punishment punishment) {
        getPlugin().getServer().getEventManager().fire(new PunishEvent(punishment));
    }

    @Override
    public void callPunishRevokeEvent(PunishmentRevoke punishmentRevoke) {
        getPlugin().getServer().getEventManager().fire(new PunishRevokeEvent(punishmentRevoke));
    }

    @Override
    public Logger getLogger() {
        return getPlugin().getLogger();
    }

    @Override
    public void scheduleAsync(Runnable task, long delay, long period, TimeUnit unit) {
        getPlugin().getServer().getScheduler().buildTask(getPlugin(), task)
                .delay(delay, unit)
                .repeat(period, unit)
                .schedule();
    }

    @Override
    public boolean isOnline(UUID uuid) {
        Player player = getPlayer(uuid);
        return player != null && player.isActive();
    }

    @Override
    public String getServer(UUID uuid) {
        Optional<ServerConnection> server = getPlayer(uuid).getCurrentServer();
        if (server.isPresent()) {
            return server.get().getServerInfo().getName();
        } else {
            return "ALL";
        }
    }

    @Override
    public void runAsync(Runnable task) {
        getPlugin().getServer().getScheduler().buildTask(getPlugin(), task).schedule();
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
        getPlayer(uuid).disconnect(Component.text(message));
    }
}