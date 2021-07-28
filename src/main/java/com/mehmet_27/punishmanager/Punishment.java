package com.mehmet_27.punishmanager;

import net.md_5.bungee.config.Configuration;

import java.sql.Timestamp;

public class Punishment {
    private String playerName, uuid, reason, operator;
    private PunishType punishType;
    private final long start, end;

    private static final int MONTH = 60 * 60 * 24 * 7 * 4;
    private static final int WEEK = 60 * 60 * 24 * 7;
    private static final int DAY = 60 * 60 * 24;
    private static final int HOUR = 60 * 60;
    private static final int MINUTE = 60;

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
        public boolean isTemp(){
            return name().contains("TEMP");
        }
    }

    public PunishType getPunishType() {
        return punishType;
    }

    public void setPunishType(PunishType punishType) {
        this.punishType = punishType;
    }

    public String getOperator() {
        return operator != null ? operator : "none";
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getReason() {
        return reason != null ? reason : "null";
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
        return punishType.name().contains("BAN");
    }

    public boolean playerIsMuted() {
        return punishType.name().contains("MUTE");
    }
    public boolean isStillPunished(){
        return getEnd() >= System.currentTimeMillis();
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public String getDuration() {
        Configuration messages = PunishManager.getInstance().getConfigManager().getMessages();
        long currentTime = new Timestamp(System.currentTimeMillis()).getTime();
        long diff = (getEnd() - currentTime) / 1000 + 1;
        //Getting time formats
        String monthFormat = messages.getString("main.timelayout.month");
        String weekFormat = messages.getString("main.timelayout.week");
        String dayFormat = messages.getString("main.timelayout.day");
        String hourFormat = messages.getString("main.timelayout.hour");
        String minuteFormat = messages.getString("main.timelayout.minute");
        String secondFormat = messages.getString("main.timelayout.second");

        String months = String.valueOf(diff / 60 / 60 / 24 / 7 / 4);
        String weeks = String.valueOf(diff / 60 / 60 / 24 / 7 % 4);
        String days = String.valueOf(diff / 60 / 60 / 24 % 7);
        String hours = String.valueOf(diff / 60 / 60 % 24);
        String minutes = String.valueOf(diff / 60 % 60);
        String seconds = String.valueOf(diff % 60);
        // months
        if (diff > MONTH) {
            return String.format("%s %s %s %s %s %s", monthFormat, weekFormat, dayFormat, hourFormat, minuteFormat, secondFormat).
                    replaceAll("%mo%", months).
                    replaceAll("%w%", weeks).
                    replaceAll("%d%", days).
                    replaceAll("%h%", hours).
                    replaceAll("%m%", minutes).
                    replaceAll("%s%", seconds);
        }
        // weeks
        else if (diff > WEEK) {
            return String.format("%s %s %s %s %s", weekFormat, dayFormat, hourFormat, minuteFormat, secondFormat).
                    replaceAll("%w%", weeks).
                    replaceAll("%d%", days).
                    replaceAll("%h%", hours).
                    replaceAll("%m%", minutes).
                    replaceAll("%s%", seconds);
        }
        // days
        else if (diff > DAY) {
            return String.format("%s %s %s %s", dayFormat, hourFormat, minuteFormat, secondFormat).
                    replaceAll("%d%", days).
                    replaceAll("%h%", hours).
                    replaceAll("%m%", minutes).
                    replaceAll("%s%", seconds);
        }
        // hours
        else if (diff > HOUR) {
            return String.format("%s %s %s", hourFormat, minuteFormat, secondFormat).
                    replaceAll("%h%", hours).
                    replaceAll("%m%", minutes).
                    replaceAll("%s%", seconds);
        }
        // minutes
        else if (diff > MINUTE) {
            return String.format("%s %s", minuteFormat, secondFormat).
                    replaceAll("%m%", minutes).
                    replaceAll("%s%", seconds);
        }
        // seconds
        else {
            return String.format("%s", secondFormat).
                    replaceAll("%s%", seconds);
        }
    }
}
