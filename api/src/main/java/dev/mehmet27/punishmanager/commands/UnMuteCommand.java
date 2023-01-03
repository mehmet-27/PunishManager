package dev.mehmet27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.managers.StorageManager;
import dev.mehmet27.punishmanager.objects.OfflinePlayer;
import dev.mehmet27.punishmanager.objects.PunishmentRevoke;

import java.util.UUID;

@CommandAlias("%punishmanager")
@CommandPermission("punishmanager.command.unmute")
public class UnMuteCommand extends BaseCommand {

    @Dependency
    private StorageManager storageManager;

    @CommandCompletion("@players")
    @Description("{@@unmute.description}")
    @CommandAlias("%unmute")
    public void unMute(CommandIssuer sender, @Name("Player") OfflinePlayer player, @Optional @Name("Reason") @Default("none") String reason) {
        String playerName = player.getName();
        UUID uuid = player.getUniqueId();
        UUID operatorUuid = sender.isPlayer() ? sender.getUniqueId() : null;

        String operator = sender.isPlayer() ? storageManager.getOfflinePlayer(sender.getUniqueId()).getName() : "CONSOLE";
        PunishmentRevoke punishmentRevoke = new PunishmentRevoke(playerName, uuid, PunishmentRevoke.RevokeType.UNMUTE, reason, operator, operatorUuid, System.currentTimeMillis(), -1);
        PunishManager.getInstance().getMethods().callPunishRevokeEvent(punishmentRevoke);
    }
}
