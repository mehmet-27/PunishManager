package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.Punishment;
import com.mehmet_27.punishmanager.managers.PunishmentManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

@CommandAlias("unmute")
@CommandPermission("punishmanager.command.unmute")
public class UnMuteCommand extends BaseCommand {

    PunishmentManager punishmentManager = PunishManager.getInstance().getPunishmentManager();

    @Default
    @CommandCompletion("@players")
    public void unmute(CommandSender sender, @Name("Player") String playerName) {
        Punishment punishment = punishmentManager.getPunishment(playerName, "mute");
        /* fixme: Small advice
           Replace it with ACF conditions
        */
        if (punishment == null || !punishment.playerIsMuted()) {
            sender.sendMessage(new TextComponent("This player is not muted."));
            return;
        }
        punishmentManager.unPunishPlayer(punishment);
        sender.sendMessage(new TextComponent(playerName + "'s mute has been lifted"));
    }
}
