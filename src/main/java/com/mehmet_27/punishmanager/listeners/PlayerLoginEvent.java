package com.mehmet_27.punishmanager.listeners;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.StorageManager;
import com.mehmet_27.punishmanager.objects.OfflinePlayer;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;

import static com.mehmet_27.punishmanager.utils.Utils.debug;

public class PlayerLoginEvent implements Listener {
    private final PunishManager punishManager = PunishManager.getInstance();
    private final StorageManager storageManager = PunishManager.getInstance().getStorageManager();

    List<String> bannedIps = PunishManager.getInstance().getBannedIps();

    @EventHandler
    public void onLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        storageManager.updatePlayerName(player);
        //TODO: punishManager.getOfflinePlayers().replace(player.getUniqueId().toString(), new OfflinePlayer(player));
        // If the player is entering the server for the first time, save it
        if (!punishManager.getOfflinePlayers().containsKey(player.getName())) {
            debug(String.format("%s is entering the server for the first time.", player.getName()));
            storageManager.addPlayer(player);
            punishManager.getOfflinePlayers().put(player.getName(), new OfflinePlayer(player));
            if (!punishManager.getAllPlayerNames().contains(player.getName())) {
                punishManager.getAllPlayerNames().add(player.getName());
            }
        }
        Punishment punishment = storageManager.getBan(player.getName());
        String playerIp = Utils.getPlayerIp(player.getName());
        if (bannedIps.contains(playerIp)) {
            debug("This player's IP address is banned: " + player.getName() + " IP: " + playerIp);
            Utils.sendLayout(punishment);
            return;
        }
        if (punishment == null) return;
        if (punishment.isExpired()) {
            storageManager.unPunishPlayer(punishment);
            return;
        }
        Utils.sendLayout(punishment);
    }
}
