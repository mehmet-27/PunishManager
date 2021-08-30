package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.objects.Ip;
import com.mehmet_27.punishmanager.objects.OfflinePlayer;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.managers.DatabaseManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

@CommandAlias("check")
@CommandPermission("punishmanager.command.check")
public class CheckCommand extends BaseCommand {

    @Dependency
    private DatabaseManager dataBaseManager;
    @Dependency
    private ConfigManager configManager;

    @Default
    @CommandCompletion("@players")
    public void check(CommandSender sender, @Name("Player") String playerName) {
        sender.sendMessage(new TextComponent(configManager.getMessage("check.checking", sender.getName()).
                replace("%player%", playerName)));
        if (!dataBaseManager.isLoggedServer(playerName)) {
            sender.sendMessage(new TextComponent(configManager.getMessage("check.playerNotFound", sender.getName()).
                    replace("%player%", playerName)));
            return;
        }

        String ip = new Ip(playerName).getPlayerIp();
        Punishment ban = dataBaseManager.getBan(playerName);
        Punishment mute = dataBaseManager.getMute(playerName);
        OfflinePlayer player = dataBaseManager.getOfflinePlayer(playerName);

        String uuid = player != null ? player.getUuid() : playerName;
        String banStatus = (ban != null && ban.isBanned() && !ban.isExpired()) ? ban.getDuration() : configManager.getMessage("check.notPunished", sender.getName());
        String muteStatus = (mute != null && mute.isMuted() && !mute.isExpired()) ? mute.getDuration() : configManager.getMessage("check.notPunished", sender.getName());
        //Kalan süreler çok uzun olduğunda bir yerden sonrasını kırp
        sender.sendMessage(new TextComponent(configManager.getMessage("check.uuid", sender.getName()).
                replace("%uuid%", uuid)));
        sender.sendMessage(new TextComponent(configManager.getMessage("check.ip", sender.getName()).
                replace("%ip%", ip)));
        sender.sendMessage(new TextComponent(configManager.getMessage("check.banStatus", sender.getName()).
                replace("%status%", banStatus)));
        if (ban != null && ban.isBanned())
            sender.sendMessage(new TextComponent(configManager.getMessage("check.banReason", sender.getName()).
                    replace("%reason%", ban.getReason())));
        if (ban != null && ban.isBanned())
            sender.sendMessage(new TextComponent(configManager.getMessage("check.banOperator", sender.getName()).
                    replace("%operator%", ban.getOperator())));
        sender.sendMessage(new TextComponent(configManager.getMessage("check.muteStatus", sender.getName()).
                replace("%status%", muteStatus)));
        if (mute != null && mute.isMuted())
            sender.sendMessage(new TextComponent(configManager.getMessage("check.muteReason", sender.getName()).
                    replace("%reason%", mute.getReason())));
        if (mute != null && mute.isMuted())
            sender.sendMessage(new TextComponent(configManager.getMessage("check.muteOperator", sender.getName()).
                    replace("%operator%", mute.getOperator())));
    }
}
