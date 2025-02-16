package dev.mehmet27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.objects.PunishmentBuilder;

import java.util.Locale;

import static dev.mehmet27.punishmanager.objects.Punishment.PunishType.IPBAN;

@CommandAlias("%punishmanager")
@CommandPermission("punishmanager.command.banip")
public class IpBanCommand extends BaseCommand {

    @Dependency
    private PunishManager punishManager;
    private static final String ERROR_MESSAGE = IPBAN.toString().toLowerCase(Locale.ENGLISH) + ".invalidIp";

    @CommandCompletion("Target Reason")
    @Description("{@@ipban.description}")
    @CommandAlias("%ipban")
    public void banIp(CommandIssuer sender, @Name("Target") String target, @Optional @Name("Reason") String reason) {
        new PunishmentBuilder(punishManager).
                operator(sender).
                target(target).
                error(ERROR_MESSAGE).
                reason(reason).
                build().punish(IPBAN);
    }
}
