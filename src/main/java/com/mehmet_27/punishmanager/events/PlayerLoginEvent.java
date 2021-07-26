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

public class PlayerLoginEvent implements Listener {

    @EventHandler
    public void onLogin(PostLoginEvent event) {
        PunishmentManager punishmentManager = PunishManager.getInstance().getPunishmentManager();
        MysqlManager mySQLManager = PunishManager.getInstance().getMySQLManager();
        ProxiedPlayer player = event.getPlayer();
        mySQLManager.addPlayer(player);
        Punishment punishment = punishmentManager.getPunishment(player.getName(), "ban");

        if (punishment == null || !punishment.playerIsBanned()) {
            return;
        }

        switch (punishment.getPunishType()) {
            case TEMPBAN:
                if (punishment.getEnd() >= System.currentTimeMillis()) {
                    Utils.disconnectPlayer(punishment);
                } else {
                    //If the ban has expired, it removes the punish from the database.
                    punishmentManager.unPunishPlayer(punishment);
                }
                break;
            default:
                Utils.disconnectPlayer(punishment);
        }
    }
}
