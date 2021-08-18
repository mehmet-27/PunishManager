package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.objects.Ip;
import com.mehmet_27.punishmanager.objects.OfflinePlayer;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.managers.MessageManager;
import com.mehmet_27.punishmanager.managers.PunishmentManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

@CommandAlias("check")
@CommandPermission("punishmanager.command.check")
public class CheckCommand extends BaseCommand {

    @Dependency
    private PunishmentManager punishmentManager;
    @Dependency
    private MessageManager messageManager;

    @Default
    @CommandCompletion("@players")
    public void check(CommandSender sender, @Name("Player") String playerName) {
        sender.sendMessage(new TextComponent(messageManager.getMessage("check.checking", sender.getName()).
                replace("%player%", playerName)));
        if (!punishmentManager.isLoggedServer(playerName)) {
            sender.sendMessage(new TextComponent(messageManager.getMessage("check.playerNotFound", sender.getName()).
                    replace("%player%", playerName)));
            return;
        }

        String ip = new Ip(playerName).getPlayerIp();
        Punishment ban = punishmentManager.getBan(playerName);
        Punishment mute = punishmentManager.getMute(playerName);
        OfflinePlayer player = punishmentManager.getOfflinePlayer(playerName);

        String uuid = player != null ? player.getUuid() : playerName;
        String banStatus = (ban != null && ban.isBanned() && !ban.isExpired()) ? ban.getDuration() : messageManager.getMessage("check.notPunished", sender.getName());
        String muteStatus = (mute != null && mute.isMuted() && !mute.isExpired()) ? mute.getDuration() : messageManager.getMessage("check.notPunished", sender.getName());
        //Kalan süreler çok uzun olduğunda bir yerden sonrasını kırp
        sender.sendMessage(new TextComponent(messageManager.getMessage("check.uuid", sender.getName()).
                replace("%uuid%", uuid)));
        sender.sendMessage(new TextComponent(messageManager.getMessage("check.ip", sender.getName()).
                replace("%ip%", ip)));
        sender.sendMessage(new TextComponent(messageManager.getMessage("check.banStatus", sender.getName()).
                replace("%status%", banStatus)));
        if (ban != null && ban.isBanned())
            sender.sendMessage(new TextComponent(messageManager.getMessage("check.banReason", sender.getName()).
                    replace("%reason%", ban.getReason())));
        if (ban != null && ban.isBanned())
            sender.sendMessage(new TextComponent(messageManager.getMessage("check.banOperator", sender.getName()).
                    replace("%operator%", ban.getOperator().getName())));
        sender.sendMessage(new TextComponent(messageManager.getMessage("check.muteStatus", sender.getName()).
                replace("%status%", muteStatus)));
        if (mute != null && mute.isMuted())
            sender.sendMessage(new TextComponent(messageManager.getMessage("check.muteReason", sender.getName()).
                    replace("%reason%", mute.getReason())));
        if (mute != null && mute.isMuted())
            sender.sendMessage(new TextComponent(messageManager.getMessage("check.muteOperator", sender.getName()).
                    replace("%operator%", mute.getOperator().getName())));
    }
}
