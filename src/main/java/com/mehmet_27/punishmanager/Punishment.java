package com.mehmet_27.punishmanager;

public class Punishment {
    private PunishType punishType;
    private String operator = "";
    private String reason = "none";

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

    public PunishType getPunishType() {
        return punishType;
    }

    public void setPunishType(PunishType punishType) {
        this.punishType = punishType;
    }

    public String getOperator() {
        if (reason.length() == 0) {
            return "null";
        }
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getReason() {
        if (reason.length() == 0) {
            return "null";
        }
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
