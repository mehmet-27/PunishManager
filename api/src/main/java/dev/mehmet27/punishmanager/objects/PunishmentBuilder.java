package dev.mehmet27.punishmanager.objects;

import co.aikar.commands.CommandIssuer;
import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.UUID;
import java.util.regex.Matcher;

import static dev.mehmet27.punishmanager.utils.Utils.IPV4_PATTERN;

public class PunishmentBuilder {
    private final PunishManager manager;
    private @Nullable OfflinePlayer target;
    private OfflinePlayer operator = new OfflinePlayer(null, "CONSOLE", null, null);
    private String server = "ALL";
    private long start = new Timestamp(System.currentTimeMillis()).getTime();
    private long end = -1;
    private String reason;
    private String errorMsg;

    public PunishmentBuilder(PunishManager manager) {
        this.manager = manager;
    }

    public PunishmentBuilder operator(CommandIssuer sender) {
        if (sender.isPlayer()) {
            UUID operatorUuid = sender.getUniqueId();
            operator = manager.getStorageManager().getOfflinePlayer(operatorUuid);
        }

        return this;
    }

    public PunishmentBuilder target(String target) {
        // Is `target` an IP address?
        Matcher matcher = IPV4_PATTERN.matcher(target);
        OfflinePlayer player;
        UUID targetUuid;

        if (matcher.matches()) {
            targetUuid = UUID.nameUUIDFromBytes(target.getBytes(StandardCharsets.UTF_8));
            player = new OfflinePlayer(targetUuid, target, target, null);
        } else {
            // `target` might be a UUID or player name then
            try {
                targetUuid = UUID.fromString(target);
                player = manager.getOfflinePlayers().get(targetUuid);
            } catch (IllegalArgumentException ignored) {
                player = manager.getStorageManager().getOfflinePlayer(target);
            }
        }

        this.target = player;
        return this;
    }

    public PunishmentBuilder target(OfflinePlayer player) {
        this.target = player;
        return this;
    }

    public PunishmentBuilder reason(String reason) {
        this.reason = reason;
        return this;
    }

    public PunishmentBuilder error(String errorMsg) {
        this.errorMsg = errorMsg;
        return this;
    }

    public PunishmentBuilder start(long start) {
        this.start = start;
        return this;
    }

    public PunishmentBuilder end(long end) {
        this.end = end;
        return this;
    }


    public PunishmentBuilder build() {
        if (target == null) {
            if (errorMsg != null) {
                Utils.sendText(operator.getUniqueId(), errorMsg);
            }
        } else if (manager.getMethods().isOnline(target.getUniqueId())) {
            server = manager.getMethods().getServer(target.getUniqueId());
        }

        return this;
    }

    public void punish(@NotNull Punishment.PunishType type) {
        if (target == null || operator == null) {
            return;
        }

        if (type == Punishment.PunishType.IPBAN) {
            target.setUniqueId(UUID.nameUUIDFromBytes(target.getName().getBytes(StandardCharsets.UTF_8)));
        }

        Punishment punishment = new Punishment(target.getName(), target.getUniqueId(), target.getPlayerIp(),
                type, reason, operator.getName(), operator.getUniqueId(), server, start, end, -1);

        if (type == Punishment.PunishType.KICK) {
            manager.getMethods().kickPlayer(target.getUniqueId(), Utils.getLayout(punishment));
            Utils.sendText(operator.getUniqueId(), target.getName(), "kick.punished");
            return;
        }

        manager.getMethods().callPunishEvent(punishment);
    }

    public void revoke(@NotNull PunishmentRevoke.RevokeType type) {
        if (target == null || operator == null) {
            return;
        }

        PunishmentRevoke punishmentRevoke = new PunishmentRevoke(target.getName(), target.getUniqueId(),
                type, reason, operator.getName(), operator.getUniqueId(), System.currentTimeMillis(), -1);
        manager.getMethods().callPunishRevokeEvent(punishmentRevoke);
    }
}
