package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.managers.DatabaseManager;
import com.mehmet_27.punishmanager.objects.Ip;
import com.mehmet_27.punishmanager.objects.OfflinePlayer;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.managers.DatabaseManager;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.api.CommandSender;

import static com.mehmet_27.punishmanager.utils.Utils.sendTextComponent;

@CommandAlias("punishmanager")
@CommandPermission("punishmanager.command.check")
public class CheckCommand extends BaseCommand {

    @Dependency
    private DatabaseManager dataBaseManager;
    @Dependency
    private ConfigManager configManager;

    @CommandCompletion("@players")
    @Description("{@@command.check.description}")
    @CommandAlias("check")
    public void check(CommandSender sender, @Name("Player") String playerName) {
        sendTextComponent(sender, "check.checking");
        if (!dataBaseManager.isLoggedServer(playerName)) {
            sendTextComponent(sender, "check.playerNotFound");
            return;
        }

        String ip = Utils.getPlayerIp(playerName);
        Punishment ban = dataBaseManager.getBan(playerName);
        Punishment mute = dataBaseManager.getMute(playerName);

        OfflinePlayer player = dataBaseManager.getOfflinePlayer(playerName);
        String uuid = player != null ? player.getUuid() : playerName;
        String banStatus = (ban != null && ban.isBanned() && !ban.isExpired()) ? ban.getDuration() : configManager.getMessage("check.notPunished", sender.getName());
        String muteStatus = (mute != null && mute.isMuted() && !mute.isExpired()) ? mute.getDuration() : configManager.getMessage("check.notPunished", sender.getName());
        //Kalan süreler çok uzun olduğunda bir yerden sonrasını kırp

        sendTextComponent(sender, "check.uuid", message -> message.replace("%uuid%", uuid));
        sendTextComponent(sender, "check.ip", message -> message.replace("%ip%", ip));
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
