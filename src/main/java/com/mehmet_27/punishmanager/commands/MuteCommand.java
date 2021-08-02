package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
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

import static com.mehmet_27.punishmanager.objects.Punishment.PunishType.MUTE;

@CommandAlias("mute")
@CommandPermission("punishmanager.command.mute")
public class MuteCommand extends BaseCommand {

    @Dependency
    private PunishmentManager punishmentManager;
    @Dependency
    private MessageManager messageManager;

    @Default
    @CommandCompletion("@players Reason")
    public void mute(CommandSender sender, @Conditions("other_player") @Name("Player") String playerName, @Optional @Name("Reason") String reason) {
        ProxiedPlayer player = PunishManager.getInstance().getProxy().getPlayer(playerName);
        String uuid = (player != null && player.isConnected()) ? player.getUniqueId().toString() : playerName;
        Punishment punishment = punishmentManager.getPunishment(playerName, "mute");
        /* fixme: Small advice
           Replace it with ACF conditions
        */
        if (punishment != null && punishment.playerIsMuted()) {
            sender.sendMessage(new TextComponent(messageManager.getMessage("mute.alreadyPunished").
                    replace("%name%", playerName)));
            return;
        }
        String ip = new Ip(playerName).getPlayerIp();
        punishment = new Punishment(playerName, uuid, ip, MUTE, new Reason(reason).getReason(), sender.getName());
        punishmentManager.AddPunish(punishment);
        sender.sendMessage(new TextComponent(messageManager.getMessage("mute.punished").
                replace("%name%", playerName)));
        Utils.sendMuteMessage(punishment);
    }
}
