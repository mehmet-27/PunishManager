package com.mehmet_27.punishmanager;

public class Punishment {
    private static PunishType punishType;
    private static String operator;
    private static String reason;

    public Punishment(PunishType punishType) {
        this.punishType = punishType;
    }
    public Punishment(PunishType punishType, String reason) {
        this.punishType = punishType;
        this.reason = reason;
    }
    public Punishment(PunishType punishType, String reason, String operator) {
        this.punishType = punishType;
        this.reason = reason;
        this.operator = operator;
    }
    public enum PunishType {
        BAN, KICK, MUTE;
    }

    public static PunishType getPunishType() {
        return punishType;
    }

    public void setPunishType(PunishType punishType) {
        this.punishType = punishType;
    }

    public static String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public static String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
