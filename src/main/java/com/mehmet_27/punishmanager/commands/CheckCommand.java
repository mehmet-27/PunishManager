package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.managers.StorageManager;
import com.mehmet_27.punishmanager.objects.OfflinePlayer;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.api.CommandSender;

import static com.mehmet_27.punishmanager.utils.Utils.sendTextComponent;

@CommandAlias("punishmanager")
@CommandPermission("punishmanager.command.check")
public class CheckCommand extends BaseCommand {

    @Dependency
    private StorageManager storageManager;
    @Dependency
    private ConfigManager configManager;

    @CommandCompletion("@players")
    @Description("{@@check.description}")
    @CommandAlias("check")
    public void check(CommandSender sender, @Name("Player") OfflinePlayer player) {
        String playerName = player.getPlayerName();
        sendTextComponent(sender, playerName, "check.checking");
        if (!storageManager.isLoggedServer(playerName)) {
            sendTextComponent(sender, playerName, "check.playerNotFound");
            return;
        }

        String ip = Utils.getPlayerIp(playerName);
        Punishment ban = storageManager.getBan(playerName);
        Punishment mute = storageManager.getMute(playerName);

        String uuid = player.getUniqueId().toString();
        String banStatus = (ban != null && ban.isBanned() && !ban.isExpired()) ? ban.getDuration() : configManager.getMessage("check.notPunished", sender.getName());
        String muteStatus = (mute != null && mute.isMuted() && !mute.isExpired()) ? mute.getDuration() : configManager.getMessage("check.notPunished", sender.getName());

        sendTextComponent(sender, "check.uuid", message -> message.replace("%uuid%", uuid));
        sendTextComponent(sender, "check.ip", message -> message.replace("%ip%", ip));
        String language = storageManager.getOfflinePlayer(playerName).getLocale().toString();
        sendTextComponent(sender, "check.language", message -> message.replace("%language%", language));
        sendTextComponent(sender, "check.banStatus", message -> message.replace("%status%", banStatus));

        if (ban != null && ban.isBanned()) {
            sendTextComponent(sender, "check.banReason", message -> message.replace("%reason%", ban.getReason()));
            sendTextComponent(sender, "check.banOperator", message -> message.replace("%operator%", ban.getOperator()));
        }

        sendTextComponent(sender, "check.muteStatus", message -> message.replace("%status%", muteStatus));
        if (mute != null && mute.isMuted()) {
            sendTextComponent(sender, "check.muteReason", message -> message.replace("%reason%", mute.getReason()));
            sendTextComponent(sender, "check.muteOperator", message -> message.replace("%operator%", mute.getOperator()));
        }
    }
}
