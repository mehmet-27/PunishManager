package dev.mehmet27.punishmanager.velocity.listeners;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.Player;
import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.managers.StorageManager;
import dev.mehmet27.punishmanager.objects.OfflinePlayer;
import dev.mehmet27.punishmanager.objects.Punishment;
import dev.mehmet27.punishmanager.utils.Utils;
import dev.mehmet27.punishmanager.velocity.PMVelocity;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

import static net.kyori.adventure.text.Component.text;

public class ConnectionListener {

    private final PMVelocity plugin;
    private final PunishManager punishManager = PunishManager.getInstance();
    private final StorageManager storageManager = punishManager.getStorageManager();

    public ConnectionListener(PMVelocity plugin) {
        this.plugin = plugin;
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onLogin(LoginEvent event) {
        Player connection = event.getPlayer();
        if (!connection.isActive()) return;
        OfflinePlayer player = new OfflinePlayer(
                connection.getUniqueId(),
                connection.getUsername(),
                Objects.requireNonNull(connection.getRemoteAddress()).toString().substring(1).split(":")[0],
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
            plugin.getCommandManager().setIssuerLocale(connection,
                    punishManager.getOfflinePlayers().get(player.getUniqueId()).getLocale());
        }

        if (punishManager.getBannedIps().contains(player.getPlayerIp())) {
            Punishment ipBan = storageManager.getIpBan(UUID.nameUUIDFromBytes(player.getPlayerIp().getBytes(StandardCharsets.UTF_8)));
            punishManager.debug("This player's IP address is banned: " + player.getName() + " IP: " + player.getPlayerIp());

            if (ipBan != null) {
                connection.disconnect(text(Utils.getLayout(ipBan)));
                return;
            }
        }

        Punishment punishment = storageManager.getBan(player.getUniqueId());
        if (punishment == null) return;
        if (punishment.isExpired()) {
            storageManager.unPunishPlayer(punishment);
            return;
        }

        connection.disconnect(text(Utils.getLayout(punishment)));
    }

    @Subscribe
    public void onPostLogin(PostLoginEvent event) {
        plugin.getCommandManager().setIssuerLocale(event.getPlayer(),
                punishManager.getOfflinePlayers().get(event.getPlayer().getUniqueId()).getLocale());
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        Punishment punishment = storageManager.getPunishment(event.getPlayer().getUniqueId());
        if (punishment == null) return;
        if (punishment.isExpired()) {
            storageManager.unPunishPlayer(punishment);
        }
        storageManager.updatePlayerLastLogin(event.getPlayer().getUniqueId());
    }
}
