package com.mehmet_27.punishmanager;

import java.sql.Timestamp;

public class Punishment {
    private String playerName, uuid, reason, operator;
    private PunishType punishType;
    private final long start, end;

    public Punishment(String playerName, String uuid, PunishType punishType, String reason, String operator) {
        this(playerName, uuid, punishType, reason, operator, new Timestamp(System.currentTimeMillis()).getTime(), -1);
    }

    public Punishment(String playerName, String uuid, PunishType punishType, String reason, String operator, long start, long end) {
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

    public String getDuration() {
        long currentTime = new Timestamp(System.currentTimeMillis()).getTime();
        long diff = (getEnd() - currentTime) / 1000;
        //Getting time formats
        String monthFormat = PunishManager.getInstance().getConfigManager().getMessages().getString("main.timelayout.month");
        String weekFormat = PunishManager.getInstance().getConfigManager().getMessages().getString("main.timelayout.week");
        String dayFormat = PunishManager.getInstance().getConfigManager().getMessages().getString("main.timelayout.day");
        String hourFormat = PunishManager.getInstance().getConfigManager().getMessages().getString("main.timelayout.hour");
        String minuteFormat = PunishManager.getInstance().getConfigManager().getMessages().getString("main.timelayout.minute");
        String secondFormat = PunishManager.getInstance().getConfigManager().getMessages().getString("main.timelayout.second");

        String months = String.valueOf(diff / 60 / 60 / 24 / 7 / 4);
        String weeks = String.valueOf(diff / 60 / 60 / 24 / 7 % 4);
        String days = String.valueOf(diff / 60 / 60 / 24 % 7);
        String hours = String.valueOf(diff / 60 / 60 % 24);
        String minutes = String.valueOf(diff / 60 % 60);
        String seconds = String.valueOf(diff % 60);
        // months
        if (diff > 60 * 60 * 24 * 7 * 4) {
            return (monthFormat + " " + weekFormat + " " + dayFormat + " " + hourFormat + " " + minuteFormat + " " + secondFormat).replaceAll("%mo%", months).replaceAll("%w%", weeks).replaceAll("%d%", days).replaceAll("%h%", hours).replaceAll("%m%", minutes).replaceAll("%s%", seconds);
        }
        // weeks
        else if (diff > 60 * 60 * 24 * 7) {
            return (weekFormat + " " + dayFormat + " " + hourFormat + " " + minuteFormat + " " + secondFormat).replaceAll("%w%", weeks).replaceAll("%d%", days).replaceAll("%h%", hours).replaceAll("%m%", minutes).replaceAll("%s%", seconds);
        }
        // days
        else if (diff > 60 * 60 * 24) {
            return (dayFormat + " " + hourFormat + " " + minuteFormat + " " + secondFormat).replaceAll("%d%", days).replaceAll("%h%", hours).replaceAll("%m%", minutes).replaceAll("%s%", seconds);
        }
        // hours
        else if (diff > 60 * 60) {
            return (hourFormat + " " + minuteFormat + " " + secondFormat).replaceAll("%h%", hours).replaceAll("%m%", minutes).replaceAll("%s%", seconds);
        }
        // minutes
        else if (diff > 60) {
            return (minuteFormat + " " + secondFormat).replaceAll("%m%", minutes).replaceAll("%s%", seconds);
        }
        // seconds
        else {
            return (secondFormat).replaceAll("%s%", seconds);
        }
    }
}
