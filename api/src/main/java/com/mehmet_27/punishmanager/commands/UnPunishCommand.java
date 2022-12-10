package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.StorageManager;
import com.mehmet_27.punishmanager.objects.OfflinePlayer;
import com.mehmet_27.punishmanager.objects.PunishmentRevoke;

import java.util.UUID;

@CommandAlias("%punishmanager")
@CommandPermission("punishmanager.command.unpunish")
public class UnPunishCommand extends BaseCommand {

    @Dependency
    private StorageManager storageManager;

    @CommandCompletion("@players")
    @Description("{@@unpunish.description}")
    @CommandAlias("%unpunish")
    public void unPunish(CommandIssuer sender, @Name("Player") OfflinePlayer player, @Optional @Name("Reason") @Default("none") String reason) {
        String playerName = player.getName();
        UUID uuid = player.getUniqueId();
        UUID operatorUuid = sender.isPlayer() ? sender.getUniqueId() : null;

        String operator = sender.isPlayer() ? storageManager.getOfflinePlayer(sender.getUniqueId()).getName() : "CONSOLE";
        PunishmentRevoke punishmentRevoke = new PunishmentRevoke(playerName, uuid, PunishmentRevoke.RevokeType.UNPUNISH, reason, operator, operatorUuid, System.currentTimeMillis(), -1);
        PunishManager.getInstance().getMethods().callPunishRevokeEvent(punishmentRevoke);
    }
}
