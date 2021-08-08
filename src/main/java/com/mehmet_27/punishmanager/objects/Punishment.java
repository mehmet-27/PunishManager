package com.mehmet_27.punishmanager.objects;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.MessageManager;
import com.mehmet_27.punishmanager.managers.PunishmentManager;
import net.md_5.bungee.config.Configuration;

import java.sql.Timestamp;

public class Punishment {
    private final PunishmentManager punishmentManager = PunishManager.getInstance().getPunishmentManager();
    private final MessageManager messageManager = PunishManager.getInstance().getMessageManager();
    private String playerName, uuid, ip, reason, operator;
    private PunishType punishType;
    private final long start, end;

    private static final int MONTH = 60 * 60 * 24 * 7 * 4;
    private static final int WEEK = 60 * 60 * 24 * 7;
    private static final int DAY = 60 * 60 * 24;
    private static final int HOUR = 60 * 60;
    private static final int MINUTE = 60;

    public Punishment(String playerName, String uuid, String ip, PunishType punishType, String reason, String operator) {
        this(playerName, uuid, ip, punishType, reason, operator, new Timestamp(System.currentTimeMillis()).getTime(), -1);
    }

    public Punishment(String playerName, String uuid, String ip, PunishType punishType, String reason, String operator, long start, long end) {
        this.playerName = playerName;
        this.uuid = uuid;
        this.ip = ip;
        this.punishType = punishType;
        this.reason = reason;
        this.operator = operator;
        this.start = start;
        this.end = end;
    }

    public enum PunishType {
        BAN, KICK, MUTE, TEMPMUTE, TEMPBAN, IPBAN, NONE;

        public boolean isTemp() {
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

    public String getIp() {
        return ip;
    }

    public void setIp() {
        this.ip = ip;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean playerIsBanned() {
        if (punishType.name().contains("BAN")) {
            if (!isStillPunished()) {
                punishmentManager.unPunishPlayer(this);
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public boolean playerIsMuted() {
        if (punishType.name().contains("MUTE")) {
            if (!isStillPunished()) {
                punishmentManager.unPunishPlayer(this);
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public boolean isStillPunished() {
        if (getEnd() == -1) {
            return true;
        } else return getEnd() >= System.currentTimeMillis();
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
        String monthFormat = messageManager.getMessage("main.timelayout.month", this.playerName);
        String weekFormat = messageManager.getMessage("main.timelayout.week", this.playerName);
        String dayFormat = messageManager.getMessage("main.timelayout.day", this.playerName);
        String hourFormat = messageManager.getMessage("main.timelayout.hour", this.playerName);
        String minuteFormat = messageManager.getMessage("main.timelayout.minute", this.playerName);
        String secondFormat = messageManager.getMessage("main.timelayout.second", this.playerName);

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
}
