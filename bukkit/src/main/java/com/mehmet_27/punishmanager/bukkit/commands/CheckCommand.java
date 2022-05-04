package com.mehmet_27.punishmanager.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.bukkit.PMBukkit;
import com.mehmet_27.punishmanager.bukkit.managers.BukkitConfigManager;
import com.mehmet_27.punishmanager.bukkit.utils.Utils;
import com.mehmet_27.punishmanager.managers.StorageManager;
import com.mehmet_27.punishmanager.objects.OfflinePlayer;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.utils.UtilsCore;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

@CommandAlias("punishmanager")
@CommandPermission("punishmanager.command.check")
public class CheckCommand extends BaseCommand {

    @Dependency
    private BukkitConfigManager configManager;

    @Dependency
    private StorageManager storageManager;

    @CommandCompletion("@players")
    @Description("{@@check.description}")
    @CommandAlias("check")
    public void check(CommandSender sender, @Name("Player") OfflinePlayer player) {
        String playerName = player.getName();
        UUID uuid = player.getUniqueId();
        Player onlinePlayer = PMBukkit.getInstance().getServer().getPlayer(playerName);
        String online = (onlinePlayer != null && onlinePlayer.isOnline()) ? configManager.getMessage("main.online") : configManager.getMessage("main.offline");
        Utils.sendText(sender, "check.checking", message -> message
                .replace("%player%", playerName)
                .replace("%online%", online));
        if (!storageManager.isLoggedServer(uuid)) {
            Utils.sendText(sender, playerName, "check.playerNotFound");
            return;
        }

        String ip = PunishManager.getInstance().getMethods().getPlayerIp(uuid).replaceAll("\\s+", "");
        Punishment ban = storageManager.getBan(uuid);
        Punishment mute = storageManager.getMute(uuid);

        String banStatus = (ban != null && ban.isBanned() && !ban.isExpired()) ? ban.getDuration() : PunishManager.getInstance().getConfigManager().getMessage("check.notPunished", sender.getName());
        String muteStatus = (mute != null && mute.isMuted() && !mute.isExpired()) ? mute.getDuration() : PunishManager.getInstance().getConfigManager().getMessage("check.notPunished", sender.getName());

        Utils.sendText(sender, "check.uuid", message -> message.replace("%uuid%", uuid.toString()));
        if (PunishManager.getInstance().getConfigManager().getConfig().getBoolean("check-command-show-ip-require-perm", false)) {
            if (sender.hasPermission("punishmanager.command.check.ip")) {
                Utils.sendText(sender, "check.ip", message -> message.replace("%ip%", ip));
            }
        } else {
            Utils.sendText(sender, "check.ip", message -> message.replace("%ip%", ip));
        }
        String country = UtilsCore.getValueFromUrlJson(ip);
        Utils.sendText(sender, "check.country", message -> message.replace("%country%", country));
        String language = storageManager.getOfflinePlayer(uuid).getLocale().toString();
        Utils.sendText(sender, "check.language", message -> message.replace("%language%", language));
        Utils.sendText(sender, "check.banStatus", message -> message.replace("%status%", banStatus));

        if (ban != null && ban.isBanned()) {
            Utils.sendText(sender, "check.punishId", message -> message.replace("%id%", ban.getId() + ""));
            Utils.sendText(sender, "check.banReason", message -> message.replace("%reason%", ban.getReason()));
            Utils.sendText(sender, "check.banOperator", message -> message.replace("%operator%", ban.getOperator()));
        }

        Utils.sendText(sender, "check.muteStatus", message -> message.replace("%status%", muteStatus));
        if (mute != null && mute.isMuted()) {
            Utils.sendText(sender, "check.punishId", message -> message.replace("%id%", mute.getId() + ""));
            Utils.sendText(sender, "check.muteReason", message -> message.replace("%reason%", mute.getReason()));
            Utils.sendText(sender, "check.muteOperator", message -> message.replace("%operator%", mute.getOperator()));
        }
    }
}
