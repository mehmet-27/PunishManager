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
        sender.sendMessage(new TextComponent(messageManager.getMessage("check.checking").
                replace("%name%", playerName)));
        if (!punishmentManager.isLoggedServer(playerName)) {
            sender.sendMessage(new TextComponent(messageManager.getMessage("check.playerNotFound").
                    replace("%name%", playerName)));
            return;
        }

        String ip = new Ip(playerName).getPlayerIp();
        Punishment ban = punishmentManager.getBan(playerName);
        Punishment mute = punishmentManager.getMute(playerName);
        OfflinePlayer player = punishmentManager.getOfflinePlayer(playerName);

        String uuid = player != null ? player.getUuid() : playerName;
        String banStatus = (ban != null && ban.playerIsBanned() && ban.isStillPunished()) ? ban.getDuration() : messageManager.getMessage("check.notPunished");
        String muteStatus = (mute != null && mute.playerIsMuted() && mute.isStillPunished()) ? mute.getDuration() : messageManager.getMessage("check.notPunished");
        //Kalan süreler çok uzun olduğunda bir yerden sonrasını kırp
        sender.sendMessage(new TextComponent(messageManager.getMessage("check.uuid").
                replace("%uuid%", uuid)));
        sender.sendMessage(new TextComponent(messageManager.getMessage("check.ip").
                replace("%ip%", ip)));
        sender.sendMessage(new TextComponent(messageManager.getMessage("check.banStatus").
                replace("%status%", banStatus)));
        if (ban != null && ban.playerIsBanned())
            sender.sendMessage(new TextComponent(messageManager.getMessage("check.banReason").
                    replace("%reason%", ban.getReason())));
        if (ban != null && ban.playerIsBanned())
            sender.sendMessage(new TextComponent(messageManager.getMessage("check.banOperator").
                    replace("%operator%", ban.getOperator())));
        sender.sendMessage(new TextComponent(messageManager.getMessage("check.muteStatus").
                replace("%status%", muteStatus)));
        if (mute != null && mute.playerIsMuted())
            sender.sendMessage(new TextComponent(messageManager.getMessage("check.muteReason").
                    replace("%reason%", mute.getReason())));
        if (mute != null && mute.playerIsMuted())
            sender.sendMessage(new TextComponent(messageManager.getMessage("check.muteOperator").
                    replace("%operator%", mute.getOperator())));
    }
}
