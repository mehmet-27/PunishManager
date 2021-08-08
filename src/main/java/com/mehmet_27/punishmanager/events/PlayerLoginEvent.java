package com.mehmet_27.punishmanager.events;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.objects.Ip;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.managers.MysqlManager;
import com.mehmet_27.punishmanager.managers.PunishmentManager;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;

public class PlayerLoginEvent implements Listener {
    private final PunishmentManager punishmentManager = PunishManager.getInstance().getPunishmentManager();
    private final MysqlManager mySQLManager = PunishManager.getInstance().getMySQLManager();

    List<String> bannedIps = PunishManager.getInstance().getBannedIps();

    @EventHandler
    public void onLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        punishmentManager.addPlayer(player);
        Punishment punishment = punishmentManager.getPunishment(player.getName(), "ban");
        String playerIp = new Ip(player.getName()).getPlayerIp();
        if (bannedIps.contains(playerIp)) {
            Utils.disconnectPlayer(punishment);
            return;
        }
        if (punishment == null || !punishment.playerIsBanned()) {
            return;
        }
        if (!punishment.isExpired()) return;
        Utils.disconnectPlayer(punishment);
    }
}
