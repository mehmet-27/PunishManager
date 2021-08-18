package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.PlayerPunishEvent;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.objects.Ip;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.objects.Reason;
import com.mehmet_27.punishmanager.managers.MessageManager;
import com.mehmet_27.punishmanager.managers.PunishmentManager;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import static com.mehmet_27.punishmanager.objects.Punishment.PunishType.BAN;

@CommandAlias("ban")
@CommandPermission("punishmanager.command.ban")
public class BanCommand extends BaseCommand {

    @Dependency
    private PunishmentManager punishmentManager;
    @Dependency
    private MessageManager messageManager;

    @Default
    @CommandCompletion("@players Reason")
    public void ban(CommandSender sender, @Conditions("other_player") @Name("Player") String playerName, @Optional @Name("Reason") @Default("none") String reason) {
        ProxiedPlayer player = PunishManager.getInstance().getProxy().getPlayer(playerName);
        String uuid = (player != null && player.isConnected()) ? player.getUniqueId().toString() : playerName;
        Punishment punishment = punishmentManager.getBan(playerName);
        if (punishment != null && punishment.isBanned()) {
            sender.sendMessage(new TextComponent(messageManager.getMessage("ban.alreadyPunished", sender.getName()).
                    replace("%player%", playerName)));
            return;
        }
        String ip = new Ip(playerName).getPlayerIp();
        punishment = new Punishment(playerName, uuid, ip, BAN, new Reason(reason, playerName).getReason(), sender.getName());
        PunishManager.getInstance().getProxy().getPluginManager().callEvent(new PlayerPunishEvent(punishment));
    }
}
