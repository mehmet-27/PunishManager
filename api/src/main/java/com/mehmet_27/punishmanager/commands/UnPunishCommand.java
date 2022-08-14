package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.managers.StorageManager;
import com.mehmet_27.punishmanager.objects.OfflinePlayer;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.utils.Utils;

import java.util.UUID;

import static com.mehmet_27.punishmanager.objects.Punishment.PunishType.NONE;

@CommandAlias("punishmanager")
@CommandPermission("punishmanager.command.unpunish")
public class UnPunishCommand extends BaseCommand {

    @Dependency
    private StorageManager storageManager;

    @CommandCompletion("@players")
    @Description("{@@unpunish.description}")
    @CommandAlias("unpunish")
    public void unPunish(CommandIssuer sender, @Name("Player") OfflinePlayer player) {
        String playerName = player.getName();
        UUID uuid = player.getUniqueId();
        UUID operatorUuid = sender.isPlayer() ? sender.getUniqueId() : null;

        Punishment punishment = storageManager.getPunishment(uuid);
        if (punishment == null || punishment.getPunishType().equals(NONE)) {
            Utils.sendText(operatorUuid, playerName, "unpunish.notPunished");
            return;
        }
        storageManager.removePlayerAllPunishes(punishment);
        Utils.sendText(operatorUuid, playerName, "unpunish.done");
    }
}
