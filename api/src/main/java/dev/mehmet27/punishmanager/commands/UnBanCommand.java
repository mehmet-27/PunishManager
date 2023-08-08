package dev.mehmet27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import dev.mehmet27.punishmanager.objects.Punishment;
import dev.mehmet27.punishmanager.objects.PunishmentRevoke;
import dev.mehmet27.punishmanager.utils.Utils;

import java.util.Locale;

@CommandAlias("%punishmanager")
@CommandPermission("punishmanager.command.unban")
public class UnBanCommand extends BaseCommand {

    private static final String ERROR_MESSAGE = Punishment.PunishType.IPBAN.toString().toLowerCase(Locale.ENGLISH) + ".notPunished";


    @CommandCompletion("@players Reason")
    @Description("{@@unban.description}")
    @CommandAlias("%unban")
    public void unBan(CommandIssuer sender, @Name("Player|Ip") String target, @Optional @Name("Reason") @Default("none") String reason) {
        Utils.handleCommands(sender, target, reason, ERROR_MESSAGE, Punishment.PunishType.NONE, PunishmentRevoke.RevokeType.UNBAN);
    }
}
