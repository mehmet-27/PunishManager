package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.managers.DatabaseManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

@CommandAlias("punishmanager")
@CommandPermission("punishmanager.command.unmute")
public class UnMuteCommand extends BaseCommand {

    @Dependency
    private DatabaseManager dataBaseManager;
    @Dependency
    private ConfigManager configManager;

    @CommandCompletion("@players")
    @Description("Removes a player's mute.")
    @CommandAlias("unmute")
    public void unMute(CommandSender sender, @Name("Player") String playerName) {
        Punishment punishment = dataBaseManager.getMute(playerName);

        if (punishment == null || !punishment.isMuted()) {
            sender.sendMessage(new TextComponent(configManager.getMessage("unmute.notPunished", sender.getName()).
                    replace("%player%", playerName)));
            return;
        }
        dataBaseManager.unPunishPlayer(punishment);
        PunishManager.getInstance().getDiscordManager().removePunishedRole(punishment);
        sender.sendMessage(new TextComponent(configManager.getMessage("unmute.done", sender.getName()).
                replace("%player%", playerName)));
    }
}
