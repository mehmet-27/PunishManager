package com.mehmet_27.punishmanager.events;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.Punishment;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.sql.Timestamp;

public class PlayerLoginEvent implements Listener {

    @EventHandler
    public void onLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        PunishManager.getInstance().getMySQLManager().addPlayer(player);
        Punishment punishment = PunishManager.getInstance().getPunishmentManager().getPunishment(player.getName(), "ban");
        if (punishment != null && punishment.playerIsBanned()) {
            if (punishment.getPunishType().equals(Punishment.PunishType.TEMPBAN)){
                long currentTime = System.currentTimeMillis();
                if (punishment.getEnd() >= currentTime){
                    Utils.disconnectPlayer(punishment);
                }
            }
            else{
                Utils.disconnectPlayer(punishment);
            }
        }
    }
}
