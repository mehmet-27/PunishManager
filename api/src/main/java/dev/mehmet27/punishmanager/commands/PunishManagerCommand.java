package dev.mehmet27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.objects.OfflinePlayer;
import dev.mehmet27.punishmanager.objects.Punishment;
import dev.mehmet27.punishmanager.objects.PunishmentRevoke;
import dev.mehmet27.punishmanager.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.UUID;
import java.util.regex.Matcher;

@CommandAlias("%punishmanager")
@Description("The main command of the plugin.")
public class PunishManagerCommand extends BaseCommand {

    @Dependency
    private PunishManager punishManager;

    public static void handleCommands(@NotNull CommandIssuer sender, @NotNull String target,
                                      @Nullable String reason, @Nullable String errorMsg,
                                      @Nullable Punishment.PunishType punishType,
                                      @Nullable PunishmentRevoke.RevokeType revokeType) {
        handleCommands(sender, target, reason, errorMsg, punishType, revokeType, new Timestamp(System.currentTimeMillis()).getTime(), -1);
    }

    /**
     * Handles various punishment actions, such as banning, muting, and unbanning of a target entity.
     * Depending on the specified parameters, this method can apply punishments (ban, mute) or revoke existing punishments (unban, unmute).
     *
     * @param sender      The command issuer who triggered the punishment action.
     * @param target      The target of the punishment action, which could be an IP address, UUID, or player name.
     * @param reason      The reason for the punishment action. It can be null if no specific reason is provided.
     * @param errorMsg    The error message to be displayed if the target cannot be found. Used for informing the sender about errors.
     * @param punishType  The type of punishment to apply (ban, mute, none for revoke actions).
     * @param revokeType  The type of punishment revocation (unban, unmute). It can be null for other punishment actions.
     * @param start       The start time of the punishment.
     * @param end         The end time of the punishment.
     */
    public static void handleCommands(@NotNull CommandIssuer sender, @NotNull String target,
                                      @Nullable String reason, @Nullable String errorMsg,
                                      @Nullable Punishment.PunishType punishType,
                                      @Nullable PunishmentRevoke.RevokeType revokeType,
                                      long start, long end) {
        String server = "ALL";
        UUID targetUuid;
        String targetName = target;
        String targetIp = target;

        String operator = "CONSOLE";
        UUID operatorUuid = null;
        if (sender.isPlayer()) {
            operatorUuid = sender.getUniqueId();
            operator = PunishManager.getInstance().getStorageManager().getOfflinePlayer(operatorUuid).getName();
        }

        // Is `target` an IP address?
        Matcher matcher = Utils.IPV4_PATTERN.matcher(target);
        OfflinePlayer player;
        if (matcher.matches()) {
            targetUuid = UUID.nameUUIDFromBytes(target.getBytes(StandardCharsets.UTF_8));
        } else {
            // `target` might be a UUID or player name then
            try {
                targetUuid = UUID.fromString(target);
                player = PunishManager.getInstance().getOfflinePlayers().get(targetUuid);
            } catch (IllegalArgumentException ignored) {
                player = PunishManager.getInstance().getStorageManager().getOfflinePlayer(target);
            }

            if (player == null) {
                if (errorMsg != null) {
                    Utils.sendText(operatorUuid, errorMsg);
                }
                return;
            }

            if (punishType == Punishment.PunishType.IPBAN) {
                targetUuid = UUID.nameUUIDFromBytes(player.getPlayerIp().getBytes(StandardCharsets.UTF_8));
            } else {
                targetUuid = player.getUniqueId();
            }

            targetName = player.getName();
            targetIp = player.getPlayerIp();

            if (PunishManager.getInstance().getMethods().isOnline(targetUuid)) {
                server = PunishManager.getInstance().getMethods().getServer(targetUuid);
            }
        }

        if (punishType == Punishment.PunishType.NONE) {
            PunishmentRevoke punishmentRevoke = new PunishmentRevoke(targetName, targetUuid, revokeType, reason, operator, operatorUuid, System.currentTimeMillis(), -1);
            PunishManager.getInstance().getMethods().callPunishRevokeEvent(punishmentRevoke);
        } else {
            Punishment punishment = new Punishment(targetName, targetUuid, targetIp, punishType, reason, operator, operatorUuid, server, start, end, -1);
            PunishManager.getInstance().getMethods().callPunishEvent(punishment);
        }
    }


    @Default
    @Description("{@@punishmanager.description}")
    @Conditions("requireProtocolize")
    public void punishManager(CommandIssuer sender) {
        //Main GUI
        if (!sender.isPlayer()) return;
        Object player = punishManager.getMethods().getPlayer(sender.getUniqueId());
        punishManager.getMethods().openMainInventory(player);
    }

    @Subcommand("%about")
    public void about(CommandIssuer sender) {
        UUID uuid = sender.isPlayer() ? sender.getUniqueId() : null;
        PunishManager.getInstance().getMethods().sendMessage(uuid, "&a&m+                                                        +");
        PunishManager.getInstance().getMethods().sendMessage(uuid, String.format("&6&l%s", PunishManager.getInstance().getMethods().getPluginName()));
        PunishManager.getInstance().getMethods().sendMessage(uuid, "&eThe best punishment plugin for your server.");
        PunishManager.getInstance().getMethods().sendMessage(uuid, "&e");
        PunishManager.getInstance().getMethods().sendMessage(uuid, "&eDeveloper: &bMehmet_27");
        PunishManager.getInstance().getMethods().sendMessage(uuid, "&eVersion: &b" + PunishManager.getInstance().getMethods().getPluginVersion());
        PunishManager.getInstance().getMethods().sendMessage(uuid, "&ePlatform: &b" + PunishManager.getInstance().getMethods().getPlatform().getFriendlyName());
        PunishManager.getInstance().getMethods().sendMessage(uuid, "&eStorage Provider: &b" + PunishManager.getInstance().getStorageManager().getStorageProvider());
        PunishManager.getInstance().getMethods().sendMessage(uuid, "&e");
        PunishManager.getInstance().getMethods().sendMessage(uuid, "&eContributors: &7Minat0_, RoinujNosde");
        PunishManager.getInstance().getMethods().sendMessage(uuid, "&a&m+                                                        +");
        PunishManager.getInstance().getMethods().sendMessage(uuid, "&eUse &a/punishmanager help &efor help.");
    }

    @Subcommand("%help")
    @Description("{@@punishmanager.help.description}")
    public void help(CommandIssuer sender, CommandHelp help) {
        help.showHelp();
    }
}
