package dev.mehmet27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import dev.mehmet27.punishmanager.objects.OfflinePlayer;
import dev.mehmet27.punishmanager.objects.Punishment;
import dev.mehmet27.punishmanager.objects.PunishmentRevoke;
import dev.mehmet27.punishmanager.utils.Utils;

@CommandAlias("%punishmanager")
@CommandPermission("punishmanager.command.tempban")
public class TempBanCommand extends BaseCommand {

    @CommandCompletion("@players @units Reason")
    @Description("{@@tempban.description}")
    @CommandAlias("%tempban")
    public void tempBan(CommandIssuer sender, @Conditions("other_player") @Name("Player") OfflinePlayer player, @Name("Time") String time, @Optional @Name("Reason") String reason) {
        long start = System.currentTimeMillis();
        long end = start + Utils.convertToMillis(time);

        PunishManagerCommand.handleCommands(sender, player.getName(), reason, null, Punishment.PunishType.TEMPBAN, PunishmentRevoke.RevokeType.UNBAN, start, end);
    }
}
