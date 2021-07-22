package com.mehmet_27.punishmanager;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class Punishment {
    private String playerName, uuid, reason, operator;
    private PunishType punishType;
    private final long start, end;

    public Punishment(String playerName, String uuid, PunishType punishType, String reason, String operator) {
        this.playerName = playerName;
        this.uuid = uuid;
        this.punishType = punishType;
        this.reason = reason;
        this.operator = operator;
        this.start = new Timestamp(System.currentTimeMillis()).getTime();
        this.end = Long.parseLong("-1");
    }
    public Punishment(String playerName, String uuid, PunishType punishType, String reason, String operator, Long start, Long end) {
        this.playerName = playerName;
        this.uuid = uuid;
        this.punishType = punishType;
        this.reason = reason;
        this.operator = operator;
        this.start = start;
        this.end = end;
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
        return punishType.equals(PunishType.BAN) ||
                punishType.equals(PunishType.TEMPBAN) ||
                punishType.equals(PunishType.IPBAN);
    }

    public boolean playerIsMuted() {
        return punishType.equals(PunishType.MUTE) || punishType.equals(PunishType.TEMPMUTE);
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }
    public Map getReaminingTime() {
        int currentTime = (int) new Timestamp(System.currentTimeMillis()).getTime();
        int difference = (int) (getEnd() - currentTime);
        if (difference <= 0) return null;
        int seconds = difference % 60;
        int minutes = difference / 60;
        int hours = minutes / 60;
        int days = hours / 24;
        int weeks = days / 7;
        int years = weeks / 52;
        Map<String, Integer> times = new HashMap();
        times.put("seconds", seconds);
        times.put("minutes", minutes);
        times.put("hours", hours);
        times.put("days", days);
        times.put("weeks", weeks);
        times.put("years", years);
        return times;
    }
}
