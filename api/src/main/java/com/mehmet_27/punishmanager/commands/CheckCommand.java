package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.managers.StorageManager;
import com.mehmet_27.punishmanager.objects.OfflinePlayer;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.utils.Utils;

import java.util.UUID;

@CommandAlias("%punishmanager")
@CommandPermission("punishmanager.command.check")
public class CheckCommand extends BaseCommand {

    @Dependency
    private PunishManager punishManager;

    @Dependency
    private ConfigManager configManager;

    @Dependency
    private StorageManager storageManager;

    @CommandCompletion("@players")
    @Description("{@@check.description}")
    @CommandAlias("%check")
    public void check(CommandIssuer sender, @Name("Player") OfflinePlayer player) {
        String playerName = player.getName();
        UUID uuid = player.getUniqueId();
        UUID operatorUuid = sender.isPlayer() ? sender.getUniqueId() : null;
        String online = (punishManager.getMethods().isOnline(uuid)) ? configManager.getMessage("main.online") : configManager.getMessage("main.offline");
        Utils.sendText(operatorUuid, "check.checking", message -> message
                .replace("%player%", playerName)
                .replace("%online%", online));
        if (!storageManager.isLoggedServer(uuid)) {
            Utils.sendText(operatorUuid, playerName, "check.playerNotFound");
            return;
        }

        String ip = PunishManager.getInstance().getMethods().getPlayerIp(uuid).replaceAll("\\s+", "");
        Punishment ban = storageManager.getBan(uuid);
        Punishment mute = storageManager.getMute(uuid);

        String banStatus = (ban != null && ban.isBanned() && !ban.isExpired()) ? ban.getDuration(operatorUuid) : PunishManager.getInstance().getConfigManager().getMessage("check.notPunished", operatorUuid);
        String muteStatus = (mute != null && mute.isMuted() && !mute.isExpired()) ? mute.getDuration(operatorUuid) : PunishManager.getInstance().getConfigManager().getMessage("check.notPunished", operatorUuid);

        Utils.sendText(operatorUuid, "check.uuid", message -> message.replace("%uuid%", uuid.toString()));
        if (PunishManager.getInstance().getConfigManager().getConfig().getBoolean("check-command-show-ip-require-perm", false)) {
            if (sender.hasPermission("punishmanager.command.check.ip")) {
                Utils.sendText(operatorUuid, "check.ip", message -> message.replace("%ip%", ip));
            }
        } else {
            Utils.sendText(operatorUuid, "check.ip", message -> message.replace("%ip%", ip));
        }
        String country = Utils.getValueFromUrlJson(ip);
        Utils.sendText(operatorUuid, "check.country", message -> message.replace("%country%", country));
        String language = storageManager.getOfflinePlayer(uuid).getLocale().toString();
        Utils.sendText(operatorUuid, "check.language", message -> message.replace("%language%", language));
        Utils.sendText(operatorUuid, "check.banStatus", message -> message.replace("%status%", banStatus));

        if (ban != null && ban.isBanned()) {
            Utils.sendText(operatorUuid, "check.punishId", message -> message.replace("%id%", ban.getId() + ""));
            Utils.sendText(operatorUuid, "check.banReason", message -> message.replace("%reason%", ban.getReason()));
            Utils.sendText(operatorUuid, "check.banOperator", message -> message.replace("%operator%", ban.getOperator()));
            Utils.sendText(operatorUuid, "check.banServer", message -> message.replace("%server%", ban.getServer()));
        }

        Utils.sendText(operatorUuid, "check.muteStatus", message -> message.replace("%status%", muteStatus));
        if (mute != null && mute.isMuted()) {
            Utils.sendText(operatorUuid, "check.punishId", message -> message.replace("%id%", mute.getId() + ""));
            Utils.sendText(operatorUuid, "check.muteReason", message -> message.replace("%reason%", mute.getReason()));
            Utils.sendText(operatorUuid, "check.muteOperator", message -> message.replace("%operator%", mute.getOperator()));
            Utils.sendText(operatorUuid, "check.muteServer", message -> message.replace("%server%", mute.getServer()));
        }
    }
}
