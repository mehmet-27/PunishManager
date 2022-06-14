package com.mehmet_27.punishmanager.bungee.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.bungee.PMBungee;
import com.mehmet_27.punishmanager.bungee.Utils.BungeeUtils;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.managers.StorageManager;
import com.mehmet_27.punishmanager.objects.OfflinePlayer;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

@CommandAlias("punishmanager")
@CommandPermission("punishmanager.command.check")
public class CheckCommand extends BaseCommand {

    @Dependency
    private ConfigManager configManager;

    @Dependency
    private StorageManager storageManager;

    @CommandCompletion("@players")
    @Description("{@@check.description}")
    @CommandAlias("check")
    public void check(CommandSender sender, @Name("Player") OfflinePlayer player) {
        String playerName = player.getName();
        UUID uuid = player.getUniqueId();
        ProxiedPlayer onlinePlayer = PMBungee.getInstance().getProxy().getPlayer(playerName);
        String online = (onlinePlayer != null && onlinePlayer.isConnected()) ? configManager.getMessage("main.online") : configManager.getMessage("main.offline");
        BungeeUtils.sendText(sender, "check.checking", message -> message
                .replace("%player%", playerName)
                .replace("%online%", online));
        if (!storageManager.isLoggedServer(uuid)) {
            BungeeUtils.sendText(sender, playerName, "check.playerNotFound");
            return;
        }

        String ip = PunishManager.getInstance().getMethods().getPlayerIp(uuid).replaceAll("\\s+", "");
        Punishment ban = storageManager.getBan(uuid);
        Punishment mute = storageManager.getMute(uuid);

        String banStatus = (ban != null && ban.isBanned() && !ban.isExpired()) ? ban.getDuration() : PunishManager.getInstance().getConfigManager().getMessage("check.notPunished", sender.getName());
        String muteStatus = (mute != null && mute.isMuted() && !mute.isExpired()) ? mute.getDuration() : PunishManager.getInstance().getConfigManager().getMessage("check.notPunished", sender.getName());

        BungeeUtils.sendText(sender, "check.uuid", message -> message.replace("%uuid%", uuid.toString()));
        if (PunishManager.getInstance().getConfigManager().getConfig().getBoolean("check-command-show-ip-require-perm", false)) {
            if (sender.hasPermission("punishmanager.command.check.ip")) {
                BungeeUtils.sendText(sender, "check.ip", message -> message.replace("%ip%", ip));
            }
        } else {
            BungeeUtils.sendText(sender, "check.ip", message -> message.replace("%ip%", ip));
        }
        String country = Utils.getValueFromUrlJson(ip);
        BungeeUtils.sendText(sender, "check.country", message -> message.replace("%country%", country));
        String language = storageManager.getOfflinePlayer(uuid).getLocale().toString();
        BungeeUtils.sendText(sender, "check.language", message -> message.replace("%language%", language));
        BungeeUtils.sendText(sender, "check.banStatus", message -> message.replace("%status%", banStatus));

        if (ban != null && ban.isBanned()) {
            BungeeUtils.sendText(sender, "check.punishId", message -> message.replace("%id%", ban.getId() + ""));
            BungeeUtils.sendText(sender, "check.banReason", message -> message.replace("%reason%", ban.getReason()));
            BungeeUtils.sendText(sender, "check.banOperator", message -> message.replace("%operator%", ban.getOperator()));
            BungeeUtils.sendText(sender, "check.banServer", message -> message.replace("%server%", ban.getServer()));
        }

        BungeeUtils.sendText(sender, "check.muteStatus", message -> message.replace("%status%", muteStatus));
        if (mute != null && mute.isMuted()) {
            BungeeUtils.sendText(sender, "check.punishId", message -> message.replace("%id%", mute.getId() + ""));
            BungeeUtils.sendText(sender, "check.muteReason", message -> message.replace("%reason%", mute.getReason()));
            BungeeUtils.sendText(sender, "check.muteOperator", message -> message.replace("%operator%", mute.getOperator()));
            BungeeUtils.sendText(sender, "check.muteServer", message -> message.replace("%server%", mute.getServer()));
        }
    }
}
