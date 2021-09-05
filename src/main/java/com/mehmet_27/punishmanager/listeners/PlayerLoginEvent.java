package com.mehmet_27.punishmanager.listeners;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.managers.DatabaseManager;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;

public class PlayerLoginEvent implements Listener {
    private final DatabaseManager dataBaseManager = PunishManager.getInstance().getDataBaseManager();

    List<String> bannedIps = PunishManager.getInstance().getBannedIps();

    @EventHandler
    public void onLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        dataBaseManager.addPlayer(player);
        Punishment punishment = dataBaseManager.getBan(player.getName());
        String playerIp = Utils.getPlayerIp(player.getName());
        if (bannedIps.contains(playerIp)) {
            Utils.sendLayout(punishment);
            return;
        }
        if (punishment == null) return;
        if (punishment.isExpired()) {
            dataBaseManager.unPunishPlayer(punishment);
            return;
        }
        Utils.sendLayout(punishment);
    }
}
