package com.mehmet_27.punishmanager.objects;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PunishmentRevoke {
    private final ConfigManager configManager = PunishManager.getInstance().getConfigManager();
    private final long time;
    private String playerName, reason, operator;
    private PunishmentRevoke.RevokeType revokeType;
    private UUID uuid;
    private @Nullable
    UUID operatorUUID;
    private final int id;

    public PunishmentRevoke(String playerName, UUID uuid, PunishmentRevoke.RevokeType revokeType, String reason, String operator, @Nullable UUID operatorUUID, long time, int id) {
        this.playerName = playerName;
        this.uuid = uuid;
        this.revokeType = revokeType;
        this.reason = reason;
        this.operator = operator;
        this.operatorUUID = operatorUUID;
        this.time = time;
        this.id = id;
    }

    public PunishmentRevoke.RevokeType getRevokeType() {
        return revokeType;
    }

    public void setRevokeType(PunishmentRevoke.RevokeType revokeType) {
        this.revokeType = revokeType;
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

    public long getTime() {
        return time;
    }

    public enum RevokeType {
        UNBAN, UNMUTE, UNPUNISH;
    }

    public int getId() {
        return id;
    }
}
