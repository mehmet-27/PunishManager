package dev.mehmet27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.managers.ConfigManager;
import dev.mehmet27.punishmanager.managers.StorageManager;
import dev.mehmet27.punishmanager.objects.OfflinePlayer;
import dev.mehmet27.punishmanager.objects.Punishment;
import dev.mehmet27.punishmanager.utils.Utils;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CommandAlias("%punishmanager")
@CommandPermission("punishmanager.command.banip")
public class IpBanCommand extends BaseCommand {

    private static final String IPV4_PATTERN = "^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$";
    private static final Pattern pattern = Pattern.compile(IPV4_PATTERN);

    @Dependency
    private StorageManager storageManager;

    @Dependency
    private PunishManager punishManager;

    @Dependency
    private ConfigManager configManager;

    @CommandCompletion("Target Reason")
    @Description("{@@ipban.description}")
    @CommandAlias("%ipban")
    public void banIp(CommandIssuer sender, @Name("Target") String target, @Optional @Name("Reason") String reason) {
        String operator = sender.isPlayer() ? storageManager.getOfflinePlayer(sender.getUniqueId()).getName() : "CONSOLE";
        UUID operatorUuid = sender.isPlayer() ? sender.getUniqueId() : null;
        String playerName;
        String ip;
        String server = "ALL";
        Punishment punishment;
        OfflinePlayer player;
        UUID uuid;
        try {
            uuid = UUID.fromString(target);
        } catch (IllegalArgumentException e) {
            uuid = null;
        }
        if (uuid != null) {
            player = PunishManager.getInstance().getOfflinePlayers().get(uuid);
        } else {
            player = PunishManager.getInstance().getStorageManager().getOfflinePlayer(target);
        }
        if (player != null) {
            uuid = player.getUniqueId();
            ip = player.getPlayerIp();
            playerName = player.getName();
            if (punishManager.getMethods().isOnline(uuid)) {
                server = punishManager.getMethods().getServer(uuid);
            }
            punishment = new Punishment(playerName, uuid, ip, Punishment.PunishType.IPBAN, reason, operator, operatorUuid, server, -1);
        } else {
            Matcher matcher = pattern.matcher(target);
            if (!matcher.matches()) {
                Utils.sendText(operatorUuid, Punishment.PunishType.IPBAN.toString().toLowerCase(Locale.ENGLISH) + ".invalidIp");
                return;
            }
            uuid = UUID.nameUUIDFromBytes(target.getBytes(StandardCharsets.UTF_8));
            ip = target;
            punishment = new Punishment(target, uuid, ip, Punishment.PunishType.IPBAN, reason, operator, operatorUuid, server, -1);
        }
        PunishManager.getInstance().getMethods().callPunishEvent(punishment);
    }
}
