package dev.mehmet27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.objects.Punishment;
import dev.mehmet27.punishmanager.objects.PunishmentBuilder;
import dev.mehmet27.punishmanager.objects.PunishmentRevoke;

import java.util.Locale;

@CommandAlias("%punishmanager")
@CommandPermission("punishmanager.command.unban")
public class UnBanCommand extends BaseCommand {
    @Dependency
    private PunishManager punishManager;
    private static final String ERROR_MESSAGE = Punishment.PunishType.IPBAN.toString().toLowerCase(Locale.ENGLISH) + ".notPunished";

    @CommandCompletion("@players Reason")
    @Description("{@@unban.description}")
    @CommandAlias("%unban")
    public void unBan(CommandIssuer sender, @Name("Player|Ip") String target, @Optional @Name("Reason") @Default("none") String reason) {
        new PunishmentBuilder(punishManager).
                operator(sender).
                target(target).
                error(ERROR_MESSAGE).
                reason(reason).
                build().revoke(PunishmentRevoke.RevokeType.UNBAN);
    }
}
