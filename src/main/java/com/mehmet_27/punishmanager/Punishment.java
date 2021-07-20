package com.mehmet_27.punishmanager;

public class Punishment {
    private String playerName, uuid;
    private PunishType punishType;
    private String operator;
    private String reason;

    public Punishment(String playerName, String uuid, PunishType punishType, String reason, String operator) {
        this.playerName = playerName;
        this.uuid = uuid;
        this.punishType = punishType;
        this.reason = reason;
        this.operator = operator;
    }

    public enum PunishType {
        BAN, KICK, MUTE, TEMPMUTE, TEMPBAN, IPBAN, NONE;
    }

    public PunishType getPunishType() {
        return punishType;
    }

    public void setPunishType(PunishType punishType) {
        this.punishType = punishType;
    }

    public String getOperator() {
        if (operator != null) {
            return operator;
        } else {
            return "none";
        }
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getReason() {
        if (reason != null) {
            return reason;
        } else {
            return "null";
        }
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String player) {
        this.playerName = player;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean playerIsBanned() {
        if (punishType.equals(PunishType.BAN) ||
                punishType.equals(PunishType.TEMPBAN) ||
                punishType.equals(PunishType.IPBAN)) {
            return true;
        }
        return false;
    }

    public boolean playerIsMuted() {
        if (punishType.equals(PunishType.MUTE) || punishType.equals(PunishType.TEMPMUTE)) {
            return true;
        }
        return false;
    }
}
