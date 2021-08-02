package com.mehmet_27.punishmanager.objects;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.PunishmentManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Ip {
    private final String playerIp;

    public Ip(String playerName) {
        ProxiedPlayer player = PunishManager.getInstance().getProxy().getPlayer(playerName);
        PunishmentManager punishmentManager = PunishManager.getInstance().getPunishmentManager();
        this.playerIp = (player != null && player.isConnected()) ? player.getSocketAddress().toString().substring(1).split(":")[0] : punishmentManager.getOfflinePlayer(playerName).getPlayerIp();
    }

    public String getPlayerIp() {
        return playerIp;
    }
}
