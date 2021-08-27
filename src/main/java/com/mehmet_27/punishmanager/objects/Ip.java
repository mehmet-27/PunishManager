package com.mehmet_27.punishmanager.objects;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.DataBaseManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Ip {
    private final String playerIp;

    public Ip(String playerName) {
        ProxiedPlayer player = PunishManager.getInstance().getProxy().getPlayer(playerName);
        DataBaseManager dataBaseManager = PunishManager.getInstance().getDataBaseManager();
        this.playerIp = (player != null && player.isConnected()) ? player.getSocketAddress().toString().substring(1).split(":")[0] : dataBaseManager.getOfflinePlayer(playerName).getPlayerIp();
    }

    public String getPlayerIp() {
        return playerIp;
    }
}
