package com.mehmet_27.punishmanager.objects;

public class OfflinePlayer {
    private String playerName, uuid, playerIp, language;

    public OfflinePlayer(String uuid, String playerName, String playerIp, String language) {
        this.uuid = uuid;
        this.playerName = playerName;
        this.playerIp = playerIp;
        this.language = language;
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

    public String getLanguage() {
        return language.split("_")[0];
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
