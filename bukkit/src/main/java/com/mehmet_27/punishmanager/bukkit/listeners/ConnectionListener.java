package com.mehmet_27.punishmanager.bukkit.listeners;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.bukkit.PMBukkit;
import com.mehmet_27.punishmanager.bukkit.utils.Utils;
import com.mehmet_27.punishmanager.managers.StorageManager;
import com.mehmet_27.punishmanager.objects.OfflinePlayer;
import com.mehmet_27.punishmanager.objects.Punishment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.Objects;

public class ConnectionListener implements Listener {

    private final PMBukkit plugin;
    private final PunishManager punishManager = PunishManager.getInstance();
    private final StorageManager storageManager = punishManager.getStorageManager();

    private final List<String> bannedIps = punishManager.getBannedIps();

    public ConnectionListener(PMBukkit plugin){
        this.plugin = plugin;
    }

    //TODO: punishManager.getOfflinePlayers().replace(player.getUniqueId().toString(), new OfflinePlayer(player));
    @EventHandler
    public void onLogin(PlayerJoinEvent event) {
        Player connection = event.getPlayer();
        if (!connection.isOnline()) return;
        connection.getAddress();
        OfflinePlayer player = new OfflinePlayer(
                connection.getUniqueId(),
                connection.getName(),
                Objects.requireNonNull(connection.getAddress()).toString().substring(1).split(":")[0],
                plugin.getConfigManager().getDefaultLocale());

        // If the player is entering the server for the first time, save it
        if (!punishManager.getOfflinePlayers().containsKey(player.getName())) {
            punishManager.debug(String.format("%s is entering the server for the first time.", player.getName()));
            storageManager.addPlayer(player);
            punishManager.getOfflinePlayers().put(player.getName(), player);
            if (!punishManager.getAllPlayerNames().contains(player.getName())) {
                punishManager.getAllPlayerNames().add(player.getName());
            }
        } else {
            storageManager.updatePlayerName(player);
            storageManager.updatePlayerIp(player);
            plugin.getCommandManager().setPlayerLocale(connection,
                    punishManager.getOfflinePlayers().get(player.getName()).getLocale());
        }

        Punishment punishment = storageManager.getBan(player.getUniqueId());
        if (punishment == null) return;
        if (punishment.isExpired()) {
            storageManager.unPunishPlayer(punishment);
            return;
        }
        if (bannedIps.contains(player.getPlayerIp())) {
            punishManager.debug("This player's IP address is banned: " + player.getName() + " IP: " + player.getPlayerIp());
            connection.kickPlayer(Utils.getLayout(punishment));
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
