package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.StorageManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import net.md_5.bungee.api.CommandSender;

import static com.mehmet_27.punishmanager.managers.DiscordAction.REMOVE;
import static com.mehmet_27.punishmanager.objects.Punishment.PunishType.NONE;
import static com.mehmet_27.punishmanager.utils.Utils.sendTextComponent;

@CommandAlias("punishmanager")
@CommandPermission("punishmanager.command.unpunish")
public class UnPunishCommand extends BaseCommand {

    @Dependency
    private StorageManager storageManager;

    @CommandCompletion("@players")
    @Description("{@@unpunish.description}")
    @CommandAlias("unpunish")
    public void unPunish(CommandSender sender, @Name("Player") String playerName) {
        Punishment punishment = storageManager.getPunishment(playerName);
        if (punishment == null || punishment.getPunishType().equals(NONE)) {
            sendTextComponent(sender, playerName, "unpunish.notPunished");
            return;
        }
        storageManager.removeAllPunishes(punishment);
        PunishManager.getInstance().getDiscordManager().updateRole(punishment, REMOVE);
        sendTextComponent(sender, playerName, "unpunish.done");
    }
}
