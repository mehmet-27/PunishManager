package dev.mehmet27.punishmanager.bukkit.listeners;

import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.bukkit.PMBukkit;
import dev.mehmet27.punishmanager.managers.StorageManager;
import dev.mehmet27.punishmanager.objects.OfflinePlayer;
import dev.mehmet27.punishmanager.objects.Punishment;
import dev.mehmet27.punishmanager.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

public class ConnectionListener implements Listener {

    private final PMBukkit plugin;
    private final PunishManager punishManager = PunishManager.getInstance();
    private final StorageManager storageManager = punishManager.getStorageManager();

    public ConnectionListener(PMBukkit plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(PlayerJoinEvent event) {
        Player connection = event.getPlayer();
        if (!connection.isOnline()) return;
        OfflinePlayer player = new OfflinePlayer(
                connection.getUniqueId(),
                connection.getName(),
                Objects.requireNonNull(connection.getAddress()).toString().substring(1).split(":")[0],
                plugin.getConfigManager().getDefaultLocale());

        // If the player is entering the server for the first time, save it
        if (!punishManager.getOfflinePlayers().containsKey(player.getUniqueId())) {
            punishManager.debug(String.format("%s is entering the server for the first time.", player.getName()));
            storageManager.addPlayer(player);
            punishManager.getOfflinePlayers().put(player.getUniqueId(), player);
            if (!punishManager.getAllPlayerNames().contains(player.getName())) {
                punishManager.getAllPlayerNames().add(player.getName());
            }
        } else {
            punishManager.getOfflinePlayers().replace(player.getUniqueId(), player);
            storageManager.updatePlayerName(player);
            storageManager.updatePlayerIp(player);
            plugin.getCommandManager().setPlayerLocale(connection,
                    punishManager.getOfflinePlayers().get(player.getUniqueId()).getLocale());
        }
        if (punishManager.getBannedIps().contains(player.getPlayerIp())) {
            Punishment ipBan = storageManager.getBan(UUID.nameUUIDFromBytes(player.getPlayerIp().getBytes(StandardCharsets.UTF_8)));
            punishManager.debug("This player's IP address is banned: " + player.getName() + " IP: " + player.getPlayerIp());
            connection.kickPlayer(Utils.getLayout(ipBan));
            return;
        }
        Punishment punishment = storageManager.getBan(player.getUniqueId());
        if (punishment == null) return;
        if (punishment.isExpired()) {
            storageManager.unPunishPlayer(punishment);
            return;
        }
        connection.kickPlayer(Utils.getLayout(punishment));
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        Punishment punishment = storageManager.getPunishment(event.getPlayer().getUniqueId());
        if (punishment == null) return;
        if (punishment.isExpired()) {
            storageManager.unPunishPlayer(punishment);
        }
        storageManager.updatePlayerLastLogin(event.getPlayer().getUniqueId());
    }
}
