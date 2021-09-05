package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.managers.DatabaseManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

@CommandAlias("punishmanager")
@CommandPermission("punishmanager.command.unban")
public class UnBanCommand extends BaseCommand {

    @Dependency
    private DatabaseManager dataBaseManager;
    @Dependency
    private ConfigManager configManager;

    @CommandCompletion("@players")
    @Description("{@@command.unban.description}")
    @CommandAlias("unban")
    public void unBan(CommandSender sender, @Name("Player") String playerName) {
        Punishment punishment = dataBaseManager.getBan(playerName);

        if (punishment == null || !punishment.isBanned()) {
            sender.sendMessage(new TextComponent(configManager.getMessage("unban.notPunished", sender.getName()).
                    replace("%player%", playerName)));
            return;
        }
        dataBaseManager.unPunishPlayer(punishment);
        sender.sendMessage(new TextComponent(configManager.getMessage("unban.done", sender.getName()).
                replace("%player%", playerName)));

    }
}
