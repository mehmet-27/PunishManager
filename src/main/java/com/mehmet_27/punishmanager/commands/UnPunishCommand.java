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
import static com.mehmet_27.punishmanager.Punishment.PunishType.NONE;

@CommandAlias("unpunish")
@CommandPermission("punishmanager.command.unpunish")
public class UnPunishCommand extends BaseCommand {

    @Dependency
    private PunishmentManager punishmentManager;
    private final MessageManager messageManager = PunishManager.getInstance().getMessageManager();

    @Default
    @CommandCompletion("@players")
    public void unPunish(CommandSender sender, @Name("Player") String playerName) {
        Punishment punishment = punishmentManager.getPunishment(playerName);
        if (punishment == null || punishment.getPunishType().equals(NONE)) {
            sender.sendMessage(new TextComponent(messageManager.getNotPunishedMessage("UNPUNISH").replace("%name%", playerName)));
            return;
        }
        punishmentManager.removeAllPunishes(punishment);
        sender.sendMessage(new TextComponent(messageManager.getUnPunishDoneMessage("UNPUNISH").replace("%name%", playerName)));
    }
}
