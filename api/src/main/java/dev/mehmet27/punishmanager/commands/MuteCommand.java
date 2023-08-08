package dev.mehmet27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import dev.mehmet27.punishmanager.objects.OfflinePlayer;
import dev.mehmet27.punishmanager.objects.Punishment;
import dev.mehmet27.punishmanager.objects.PunishmentRevoke;
import dev.mehmet27.punishmanager.utils.Utils;

@CommandAlias("%punishmanager")
@CommandPermission("punishmanager.command.mute")
public class MuteCommand extends BaseCommand {

    @CommandCompletion("@players Reason")
    @Description("{@@mute.description}")
    @CommandAlias("%mute")
    public void mute(CommandIssuer sender, @Conditions("other_player") @Name("Player") OfflinePlayer player, @Optional @Name("Reason") String reason) {
        Utils.handleCommands(sender, player.getName(), reason, null, Punishment.PunishType.MUTE, PunishmentRevoke.RevokeType.UNMUTE);
    }
}
