package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.Punishment;
import com.mehmet_27.punishmanager.managers.PunishmentManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

@CommandAlias("unban")
@CommandPermission("punishmanager.command.unban")
public class UnBanCommand extends BaseCommand {

    @Dependency
    private PunishmentManager punishmentManager;

    @Default
    @CommandCompletion("@players")
    public void unban(CommandSender sender, @Name("Player") String playerName) {
        Punishment punishment = punishmentManager.getPunishment(playerName, "ban");
        if (punishment == null || !punishment.playerIsBanned()) {
            sender.sendMessage(new TextComponent("This player is not banned."));
            return;
        }
        punishmentManager.unPunishPlayer(punishment);
        sender.sendMessage(new TextComponent(playerName + "'s ban has been lifted"));

    }
}
