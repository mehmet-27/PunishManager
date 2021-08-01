package com.mehmet_27.punishmanager.events;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.Punishment;
import com.mehmet_27.punishmanager.managers.MysqlManager;
import com.mehmet_27.punishmanager.managers.PunishmentManager;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;

public class PlayerLoginEvent implements Listener {
    List<String> bannedIps = PunishManager.getInstance().getBannedIps();

    @EventHandler
    public void onLogin(PostLoginEvent event) {
        PunishmentManager punishmentManager = PunishManager.getInstance().getPunishmentManager();
        MysqlManager mySQLManager = PunishManager.getInstance().getMySQLManager();
        ProxiedPlayer player = event.getPlayer();
        mySQLManager.addPlayer(player);
        Punishment punishment = punishmentManager.getPunishment(player.getName(), "ban");
        String playerIp = player.getSocketAddress().toString().substring(1).split(":")[0];
        if (bannedIps.contains(playerIp)){
            Utils.disconnectPlayer(punishment);
            return;
        }
        if (punishment == null || !punishment.playerIsBanned()) {
            return;
        }
        if (!punishment.isStillPunished()) return;
        Utils.disconnectPlayer(punishment);
    }
}
