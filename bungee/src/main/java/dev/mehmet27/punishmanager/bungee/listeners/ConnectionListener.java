package dev.mehmet27.punishmanager.bungee.listeners;

import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.bungee.PMBungee;
import dev.mehmet27.punishmanager.managers.StorageManager;
import dev.mehmet27.punishmanager.objects.OfflinePlayer;
import dev.mehmet27.punishmanager.objects.Punishment;
import dev.mehmet27.punishmanager.utils.Utils;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ConnectionListener implements Listener {

    private final PMBungee plugin;
    private final PunishManager punishManager = PunishManager.getInstance();
    private final StorageManager storageManager = punishManager.getStorageManager();

    public ConnectionListener(PMBungee plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(LoginEvent event) {
        PendingConnection connection = event.getConnection();
        if (!connection.isConnected()) return;
        OfflinePlayer player = new OfflinePlayer(
                connection.getUniqueId(),
                connection.getName(),
                connection.getSocketAddress().toString().substring(1).split(":")[0],
                plugin.getConfigManager().getDefaultLocale());

        // If the player is entering the server for the first time, save it
        if (!punishManager.getOfflinePlayers().containsKey(player.getUniqueId())) {
            PunishManager.getInstance().debug(String.format("%s is entering the server for the first time.", player.getName()));
            storageManager.addPlayer(player);
            punishManager.getOfflinePlayers().put(player.getUniqueId(), player);
            if (!punishManager.getAllPlayerNames().contains(player.getName())) {
                punishManager.getAllPlayerNames().add(player.getName());
            }
        } else {
            punishManager.getOfflinePlayers().replace(player.getUniqueId(), player);
            storageManager.updatePlayerName(player);
            storageManager.updatePlayerIp(player);
        }
        if (punishManager.getBannedIps().contains(player.getPlayerIp())) {
            Punishment ipBan = storageManager.getBan(UUID.nameUUIDFromBytes(player.getPlayerIp().getBytes(StandardCharsets.UTF_8)));
            punishManager.debug("This player's IP address is banned: " + player.getName() + " IP: " + player.getPlayerIp());
            event.setCancelReason(TextComponent.fromLegacyText(Utils.getLayout(ipBan)));
            event.setCancelled(true);
            return;
        }
        Punishment punishment = storageManager.getBan(player.getUniqueId());
        if (punishment == null) return;
        if (punishment.isExpired()) {
            storageManager.unPunishPlayer(punishment);
            return;
        }
        event.setCancelReason(TextComponent.fromLegacyText(Utils.getLayout(punishment)));
        event.setCancelled(true);
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        plugin.getCommandManager().setIssuerLocale(event.getPlayer(),
                punishManager.getOfflinePlayers().get(event.getPlayer().getUniqueId()).getLocale());
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        Punishment punishment = storageManager.getPunishment(event.getPlayer().getUniqueId());
        if (punishment == null) return;
        if (punishment.isExpired()) {
            storageManager.unPunishPlayer(punishment);
        }
        storageManager.updatePlayerLastLogin(event.getPlayer().getUniqueId());
    }
}
