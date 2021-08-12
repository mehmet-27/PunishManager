package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.managers.MessageManager;
import com.mehmet_27.punishmanager.managers.PunishmentManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

import static com.mehmet_27.punishmanager.objects.Punishment.PunishType.NONE;

@CommandAlias("unpunish")
@CommandPermission("punishmanager.command.unpunish")
public class UnPunishCommand extends BaseCommand {

    @Dependency
    private PunishmentManager punishmentManager;
    @Dependency
    private MessageManager messageManager;

    @Default
    @CommandCompletion("@players")
    public void unPunish(CommandSender sender, @Name("Player") String playerName) {
        Punishment punishment = punishmentManager.getPunishment(playerName);
        if (punishment == null || punishment.getPunishType().equals(NONE)) {
            sender.sendMessage(new TextComponent(messageManager.getMessage("unpunish.notPunished", sender.getName()).
                    replace("%player%", playerName)));
            return;
        }
        punishmentManager.removeAllPunishes(punishment);
        PunishManager.getInstance().getDiscordManager().removePunishedRole(punishment);
        sender.sendMessage(new TextComponent(messageManager.getMessage("unpunish.done", sender.getName()).
                replace("%player%", playerName)));
    }
}
