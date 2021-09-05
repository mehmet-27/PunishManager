package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.managers.DatabaseManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import net.md_5.bungee.api.CommandSender;

import static com.mehmet_27.punishmanager.utils.Utils.sendTextComponent;

@CommandAlias("punishmanager")
@CommandPermission("punishmanager.command.unban")
public class UnBanCommand extends BaseCommand {

    @Dependency
    private DatabaseManager dataBaseManager;

    @CommandCompletion("@players")
    @Description("Removes a player's ban.")
    @CommandAlias("unban")
    public void unBan(CommandSender sender, @Name("Player") String playerName) {
        Punishment punishment = dataBaseManager.getBan(playerName);

        if (punishment == null || !punishment.isBanned()) {
            sendTextComponent(sender, "unban.notPunished");
            return;
        }

        dataBaseManager.unPunishPlayer(punishment);
        sendTextComponent(sender, "unban.done");
    }
}
