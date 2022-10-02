package com.mehmet_27.punishmanager.objects;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.ConfigManager;

import javax.annotation.Nullable;
import java.sql.Timestamp;
import java.util.UUID;

public class Punishment {
    private static final int YEAR = 60 * 60 * 24 * 7 * 4 * 12;
    private static final int MONTH = 60 * 60 * 24 * 7 * 4;
    private static final int WEEK = 60 * 60 * 24 * 7;
    private static final int DAY = 60 * 60 * 24;
    private static final int HOUR = 60 * 60;
    private static final int MINUTE = 60;
    private final ConfigManager configManager = PunishManager.getInstance().getConfigManager();
    private final long start, end;
    private String playerName, reason, operator, server;
    private @Nullable
    String ip;
    private PunishType punishType;
    private UUID uuid;
    private @Nullable
    UUID operatorUUID;
    private final int id;

    public Punishment(String playerName, UUID uuid, @Nullable String ip, PunishType punishType, String reason, String operator, @Nullable UUID operatorUUID, String server, int id) {
        this(playerName, uuid, ip, punishType, reason, operator, operatorUUID, server, new Timestamp(System.currentTimeMillis()).getTime(), -1, id);
    }

    public Punishment(String playerName, UUID uuid, @Nullable String ip, PunishType punishType, String reason, String operator, @Nullable UUID operatorUUID, String server, long start, long end, int id) {
        this.playerName = playerName;
        this.uuid = uuid;
        this.ip = ip;
        this.punishType = punishType;
        this.reason = reason;
        this.operator = operator;
        this.operatorUUID = operatorUUID;
        this.server = server;
        this.start = start;
        this.end = end;
        this.id = id;
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

    public @Nullable
    UUID getOperatorUUID() {
        return operatorUUID;
    }

    public void setOperatorUUID(@Nullable UUID operatorUUID) {
        this.operatorUUID = operatorUUID;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getReason() {
        if (reason == null) {
            reason = configManager.getMessage("main.defaultReason", uuid);
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

    public @Nullable
    String getIp() {
        return ip;
    }

    public void setIp(@Nullable String ip) {
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

    public String getDuration(UUID uuid) {
        if (getEnd() == -1) {
            return "permanent";
        }
        long currentTime = new Timestamp(System.currentTimeMillis()).getTime();
        long diff = (getEnd() - currentTime) / 1000 + 1;
        //Getting time formats
        String yearFormat = configManager.getMessage("main.timelayout.year", uuid);
        String monthFormat = configManager.getMessage("main.timelayout.month", uuid);
        String weekFormat = configManager.getMessage("main.timelayout.week", uuid);
        String dayFormat = configManager.getMessage("main.timelayout.day", uuid);
        String hourFormat = configManager.getMessage("main.timelayout.hour", uuid);
        String minuteFormat = configManager.getMessage("main.timelayout.minute", uuid);
        String secondFormat = configManager.getMessage("main.timelayout.second", uuid);

        String years = String.valueOf(diff / 60 / 60 / 24 / 7 / 4 / 12);
        String months = String.valueOf(diff / 60 / 60 / 24 / 7 / 4 % 12);
        String weeks = String.valueOf(diff / 60 / 60 / 24 / 7 % 4);
        String days = String.valueOf(diff / 60 / 60 / 24 % 7);
        String hours = String.valueOf(diff / 60 / 60 % 24);
        String minutes = String.valueOf(diff / 60 % 60);
        String seconds = String.valueOf(diff % 60);

        // show short
        if (diff % YEAR == 0) {
            return String.format("%s", yearFormat).
                    replaceAll("%y%", years);
        }
        // years
        if (diff > YEAR) {
            return String.format("%s %s %s %s %s %s %s", yearFormat, monthFormat, weekFormat, dayFormat, hourFormat, minuteFormat, secondFormat).
                    replaceAll("%y%", years).
                    replaceAll("%mo%", months).
                    replaceAll("%w%", weeks).
                    replaceAll("%d%", days).
                    replaceAll("%h%", hours).
                    replaceAll("%m%", minutes).
                    replaceAll("%s%", seconds);
        }
        // show short
        else if (diff % MONTH == 0) {
            return String.format("%s", monthFormat).
                    replaceAll("%mo%", months);
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
        // show short
        else if (diff % WEEK == 0) {
            return String.format("%s", weekFormat).
                    replaceAll("%w%", weeks);
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
        // show short
        else if (diff % DAY == 0) {
            return String.format("%s", dayFormat).
                    replaceAll("%d%", days);
        }
        // days
        else if (diff > DAY) {
            return String.format("%s %s %s %s", dayFormat, hourFormat, minuteFormat, secondFormat).
                    replaceAll("%d%", days).
                    replaceAll("%h%", hours).
                    replaceAll("%m%", minutes).
                    replaceAll("%s%", seconds);
        }
        // show short
        else if (diff % HOUR == 0) {
            return String.format("%s", hourFormat).
                    replaceAll("%h%", hours);
        }
        // hours
        else if (diff > HOUR) {
            return String.format("%s %s %s", hourFormat, minuteFormat, secondFormat).
                    replaceAll("%h%", hours).
                    replaceAll("%m%", minutes).
                    replaceAll("%s%", seconds);
        }
        // show short
        else if (diff % MINUTE == 0) {
            return String.format("%s", minuteFormat).
                    replaceAll("%m%", minutes);
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

    public String getDuration() {
        return getDuration(null);
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

    public int getId() {
        return id;
    }
}
