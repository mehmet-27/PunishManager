package dev.mehmet27.punishmanager.objects;

import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.managers.ConfigManager;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;
import java.time.*;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Punishment {
    private static final int YEAR = 60 * 60 * 24 * 7 * 4 * 12;
    private static final int MONTH = 60 * 60 * 24 * 7 * 4;
    private static final int WEEK = 60 * 60 * 24 * 7;
    private static final int DAY = 60 * 60 * 24;
    private static final int HOUR = 60 * 60;
    private static final int MINUTE = 60;
    private final ConfigManager configManager = PunishManager.getInstance().getConfigManager();
    private final long start;
    private final long end;
    private @Nullable String playerName;
    private String reason;
    private String operator;
    private String server;
    private @Nullable
    String ip;
    private PunishType punishType;
    private UUID uuid;
    private @Nullable
    UUID operatorUUID;
    private final int id;

    public Punishment(@Nullable String playerName, UUID uuid, @Nullable String ip, PunishType punishType, String reason, String operator, @Nullable UUID operatorUUID, String server, int id) {
        this(playerName, uuid, ip, punishType, reason, operator, operatorUUID, server, new Timestamp(System.currentTimeMillis()).getTime(), -1, id);
    }

    public Punishment(@Nullable String playerName, UUID uuid, @Nullable String ip, PunishType punishType, String reason, String operator, @Nullable UUID operatorUUID, String server, long start, long end, int id) {
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

    public @Nullable String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(@Nullable String player) {
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

    public String getDuration(@Nullable UUID uuid) {
        if (getEnd() == -1) {
            return "permanent";
        }

        //Getting time formats
        String yearFormat = configManager.getMessage("main.timelayout.year", uuid);
        String monthFormat = configManager.getMessage("main.timelayout.month", uuid);
        String weekFormat = configManager.getMessage("main.timelayout.week", uuid);
        String dayFormat = configManager.getMessage("main.timelayout.day", uuid);
        String hourFormat = configManager.getMessage("main.timelayout.hour", uuid);
        String minuteFormat = configManager.getMessage("main.timelayout.minute", uuid);
        String secondFormat = configManager.getMessage("main.timelayout.second", uuid);

        Instant now = Instant.now();
        Instant past = Instant.ofEpochMilli(getEnd());

        LocalDate pastDate = past.atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate nowDate = now.atZone(ZoneId.systemDefault()).toLocalDate();

        Period period = Period.between(pastDate, nowDate);
        Duration duration = Duration.between(past, now);

        int years = period.getYears();
        int months = period.getMonths();
        int days = period.getDays();
        int weeks = days / 7;
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;

        StringBuilder durationFormat = new StringBuilder();
        if (years > 0) {
            durationFormat.append(yearFormat.replaceAll("%y%", String.valueOf(years))).append(" ");
        }
        if (months > 0) {
            durationFormat.append(monthFormat.replaceAll("%mo%", String.valueOf(months))).append(" ");
        }
        if (weeks > 0) {
            durationFormat.append(weekFormat.replaceAll("%w%", String.valueOf(weeks))).append(" ");
        }
        if (days > 0) {
            durationFormat.append(dayFormat.replaceAll("%d%", String.valueOf(days))).append(" ");
        }
        if (hours > 0) {
            durationFormat.append(hourFormat.replaceAll("%h%", String.valueOf(hours))).append(" ");
        }
        if (minutes > 0) {
            durationFormat.append(minuteFormat.replaceAll("%m%", String.valueOf(minutes))).append(" ");
        }
        if (seconds > 0) {
            durationFormat.append(secondFormat.replaceAll("%s%", String.valueOf(seconds)));
        }

        return durationFormat.toString();
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
