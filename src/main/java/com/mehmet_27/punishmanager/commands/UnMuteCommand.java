package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.DatabaseManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import net.md_5.bungee.api.CommandSender;

import static com.mehmet_27.punishmanager.managers.DiscordAction.REMOVE;
import static com.mehmet_27.punishmanager.utils.Utils.sendTextComponent;

@CommandAlias("punishmanager")
@CommandPermission("punishmanager.command.unmute")
public class UnMuteCommand extends BaseCommand {

    @Dependency
    private DatabaseManager dataBaseManager;

    @CommandCompletion("@players")
    @Description("{@@command.unmute.description}")
    @CommandAlias("unmute")
    public void unMute(CommandSender sender, @Name("Player") String playerName) {
        Punishment punishment = dataBaseManager.getMute(playerName);

        if (punishment == null || !punishment.isMuted()) {
            sendTextComponent(sender, "unmute.notPunished");
            return;
        }
        dataBaseManager.unPunishPlayer(punishment);
        PunishManager.getInstance().getDiscordManager().updateRole(punishment, REMOVE);
        sendTextComponent(sender, "unmute.done");
    }
}
