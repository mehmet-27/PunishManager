package com.mehmet_27.punishmanager.bungee.listeners;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.bungee.PMBungee;
import com.mehmet_27.punishmanager.bungee.Utils.Utils;
import com.mehmet_27.punishmanager.managers.StorageManager;
import com.mehmet_27.punishmanager.objects.OfflinePlayer;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.utils.UtilsCore;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;

public class ConnectionListener implements Listener {

    private final PMBungee plugin;
    private final PunishManager punishManager = PunishManager.getInstance();
    private final StorageManager storageManager = punishManager.getStorageManager();

    private final List<String> bannedIps = punishManager.getBannedIps();

    public ConnectionListener(PMBungee plugin) {
        this.plugin = plugin;
    }

    //TODO: punishManager.getOfflinePlayers().replace(player.getUniqueId().toString(), new OfflinePlayer(player));
    @EventHandler
    public void onLogin(LoginEvent event) {
        PendingConnection connection = event.getConnection();
        if (!connection.isConnected()) return;
        OfflinePlayer player = new OfflinePlayer(
                connection.getUniqueId(),
                connection.getName(),
                connection.getSocketAddress().toString().substring(1).split(":")[0],
                plugin.getConfigManager().getDefaultLocale());

        // If the player is entering the server for the first time, save it
        if (!punishManager.getOfflinePlayers().containsKey(player.getName())) {
            UtilsCore.debug(String.format("%s is entering the server for the first time.", player.getName()));
            storageManager.addPlayer(player);
            punishManager.getOfflinePlayers().put(player.getName(), player);
            if (!punishManager.getAllPlayerNames().contains(player.getName())) {
                punishManager.getAllPlayerNames().add(player.getName());
            }
        } else {
            storageManager.updatePlayerName(player);
            storageManager.updatePlayerIp(player);
        }

        Punishment punishment = storageManager.getBan(player.getUniqueId());
        if (punishment == null) return;
        if (punishment.isExpired()) {
            storageManager.unPunishPlayer(punishment);
            return;
        }
        if (bannedIps.contains(player.getPlayerIp())) {
            UtilsCore.debug("This player's IP address is banned: " + player.getName() + " IP: " + player.getPlayerIp());
            event.setCancelReason(Utils.getLayout(punishment));
            event.setCancelled(true);
            return;
        }
        event.setCancelReason(Utils.getLayout(punishment));
        event.setCancelled(true);
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        Punishment punishment = storageManager.getBan(event.getPlayer().getUniqueId());
        if (punishment == null) return;
        if (punishment.isExpired()) {
            storageManager.unPunishPlayer(punishment);
        }
    }
}
