package com.mehmet_27.punishmanager.objects;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.ConfigManager;

import java.sql.Timestamp;
import java.util.UUID;

public class Punishment {
    private static final int MONTH = 60 * 60 * 24 * 7 * 4;
    private static final int WEEK = 60 * 60 * 24 * 7;
    private static final int DAY = 60 * 60 * 24;
    private static final int HOUR = 60 * 60;
    private static final int MINUTE = 60;
    private final ConfigManager configManager = PunishManager.getInstance().getConfigManager();
    private final long start, end;
    private String playerName, ip, reason, operator;
    private PunishType punishType;
    private UUID uuid;

    public Punishment(String playerName, UUID uuid, String ip, PunishType punishType, String reason, String operator) {
        this(playerName, uuid, ip, punishType, reason, operator, new Timestamp(System.currentTimeMillis()).getTime(), -1);
    }

    public Punishment(String playerName, UUID uuid, String ip, PunishType punishType, String reason, String operator, long start, long end) {
        this.playerName = playerName;
        this.uuid = uuid;
        this.ip = ip;
        this.punishType = punishType;
        this.reason = reason;
        this.operator = operator;
        this.start = start;
        this.end = end;
    }

    public PunishType getPunishType() {
        return punishType;
    }

    public void setPunishType(PunishType punishType) {
        this.punishType = punishType;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getReason() {
        if (reason == null) {
            reason = configManager.getMessage("main.defaultReason", playerName);
        }
        return reason;
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

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public boolean isBanned() {
        if (punishType.name().contains("BAN")) {
            return !isExpired();
        }
        return false;
    }

    public boolean isMuted() {
        if (punishType.name().contains("MUTE")) {
            return !isExpired();
        }
        return false;
    }

    public boolean isExpired() {
        if (getEnd() == -1) {
            return false;
        } else return getEnd() <= System.currentTimeMillis();
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public String getDuration() {
        long currentTime = new Timestamp(System.currentTimeMillis()).getTime();
        long diff = (getEnd() - currentTime) / 1000 + 1;
        //Getting time formats
        String monthFormat = configManager.getMessage("main.timelayout.month", this.playerName);
        String weekFormat = configManager.getMessage("main.timelayout.week", this.playerName);
        String dayFormat = configManager.getMessage("main.timelayout.day", this.playerName);
        String hourFormat = configManager.getMessage("main.timelayout.hour", this.playerName);
        String minuteFormat = configManager.getMessage("main.timelayout.minute", this.playerName);
        String secondFormat = configManager.getMessage("main.timelayout.second", this.playerName);

        String months = String.valueOf(diff / 60 / 60 / 24 / 7 / 4);
        String weeks = String.valueOf(diff / 60 / 60 / 24 / 7 % 4);
        String days = String.valueOf(diff / 60 / 60 / 24 % 7);
        String hours = String.valueOf(diff / 60 / 60 % 24);
        String minutes = String.valueOf(diff / 60 % 60);
        String seconds = String.valueOf(diff % 60);
        if (getEnd() == -1) {
            return "permanent";
        }
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

    public enum PunishType {
        BAN, KICK, MUTE, TEMPMUTE, TEMPBAN, IPBAN, NONE;

        public boolean isTemp() {
            return name().contains("TEMP");
        }

        public boolean isMute() {
            return name().contains("MUTE");
        }

        public boolean isBan() {
            return name().contains("BAN");
        }
    }
}
