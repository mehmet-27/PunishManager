package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.Punishment;
import com.mehmet_27.punishmanager.managers.MessageManager;
import com.mehmet_27.punishmanager.managers.PunishmentManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

import static com.mehmet_27.punishmanager.Punishment.PunishType.MUTE;

@CommandAlias("unmute")
@CommandPermission("punishmanager.command.unmute")
public class UnMuteCommand extends BaseCommand {

    @Dependency
    private PunishmentManager punishmentManager;
    private final MessageManager messageManager = PunishManager.getInstance().getMessageManager();

    @Default
    @CommandCompletion("@players")
    public void unMute(CommandSender sender, @Name("Player") String playerName) {
        Punishment punishment = punishmentManager.getPunishment(playerName, "mute");
        /* fixme: Small advice
           Replace it with ACF conditions
        */
        if (punishment == null || !punishment.playerIsMuted()) {
            sender.sendMessage(new TextComponent(messageManager.getNotPunishedMessage("UNMUTE").replace("%name%", playerName)));
            return;
        }
        punishmentManager.unPunishPlayer(punishment);
        sender.sendMessage(new TextComponent(messageManager.getUnPunishDoneMessage("UNMUTE").replace("%name%", playerName)));
    }
}
