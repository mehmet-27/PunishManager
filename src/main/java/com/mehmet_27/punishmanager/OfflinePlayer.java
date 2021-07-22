package com.mehmet_27.punishmanager;

public class OfflinePlayer {
    private String playerName;
    private String uuid;
    private String playerIp;

    public OfflinePlayer(String playerName, String uuid, String playerIp) {
        this.playerName = playerName;
        this.playerIp = playerIp;
        this.uuid = uuid;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPlayerIp() {
        return playerIp;
    }

    public void setPlayerIp(String playerIp) {
        this.playerIp = playerIp;
    }
}
