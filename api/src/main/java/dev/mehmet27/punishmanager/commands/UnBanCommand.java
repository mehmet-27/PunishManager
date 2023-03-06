package dev.mehmet27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.managers.StorageManager;
import dev.mehmet27.punishmanager.objects.OfflinePlayer;
import dev.mehmet27.punishmanager.objects.Punishment;
import dev.mehmet27.punishmanager.objects.PunishmentRevoke;
import dev.mehmet27.punishmanager.utils.Utils;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CommandAlias("%punishmanager")
@CommandPermission("punishmanager.command.unban")
public class UnBanCommand extends BaseCommand {

    private static final String IPV4_PATTERN = "^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$";
    private static final Pattern pattern = Pattern.compile(IPV4_PATTERN);

    @Dependency
    private StorageManager storageManager;

    @CommandCompletion("@players Reason")
    @Description("{@@unban.description}")
    @CommandAlias("%unban")
    public void unBan(CommandIssuer sender, @Name("Player|Ip") String target, @Optional @Name("Reason") @Default("none") String reason) {
        String playerName;
        UUID uuid;
        UUID operatorUuid = sender.isPlayer() ? sender.getUniqueId() : null;
        String operator = sender.isPlayer() ? storageManager.getOfflinePlayer(sender.getUniqueId()).getName() : "CONSOLE";
        OfflinePlayer player;
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
            playerName = player.getName();
        } else {
            Matcher matcher = pattern.matcher(target);
            if (!matcher.matches()) {
                Utils.sendText(operatorUuid, target, Punishment.PunishType.IPBAN.toString().toLowerCase(Locale.ENGLISH) + ".notPunished");
                return;
            }
            uuid = UUID.nameUUIDFromBytes(target.getBytes(StandardCharsets.UTF_8));
            playerName = target;
        }
        PunishmentRevoke punishmentRevoke = new PunishmentRevoke(playerName, uuid, PunishmentRevoke.RevokeType.UNBAN, reason, operator, operatorUuid, System.currentTimeMillis(), -1);
        PunishManager.getInstance().getMethods().callPunishRevokeEvent(punishmentRevoke);
    }
}
