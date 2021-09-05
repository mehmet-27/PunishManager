package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.managers.DatabaseManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

import static com.mehmet_27.punishmanager.objects.Punishment.PunishType.NONE;

@CommandAlias("punishmanager")
@CommandPermission("punishmanager.command.unpunish")
public class UnPunishCommand extends BaseCommand {

    @Dependency
    private DatabaseManager dataBaseManager;
    @Dependency
    private ConfigManager configManager;

    @CommandCompletion("@players")
    @Description("{@@command.unpunish.description}")
    @CommandAlias("unpunish")
    public void unPunish(CommandSender sender, @Name("Player") String playerName) {
        Punishment punishment = dataBaseManager.getPunishment(playerName);
        if (punishment == null || punishment.getPunishType().equals(NONE)) {
            sender.sendMessage(new TextComponent(configManager.getMessage("unpunish.notPunished", sender.getName()).
                    replace("%player%", playerName)));
            return;
        }
        dataBaseManager.removeAllPunishes(punishment);
        PunishManager.getInstance().getDiscordManager().removePunishedRole(punishment);
        sender.sendMessage(new TextComponent(configManager.getMessage("unpunish.done", sender.getName()).
                replace("%player%", playerName)));
    }
}
