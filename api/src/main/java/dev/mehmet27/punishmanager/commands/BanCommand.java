package dev.mehmet27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.objects.OfflinePlayer;
import dev.mehmet27.punishmanager.objects.PunishmentBuilder;

import static dev.mehmet27.punishmanager.objects.Punishment.PunishType.BAN;

@CommandAlias("%punishmanager")
@CommandPermission("punishmanager.command.ban")
public class BanCommand extends BaseCommand {

    @Dependency
    private PunishManager punishManager;
    
    @CommandCompletion("@players Reason")
    @Description("{@@ban.description}")
    @CommandAlias("%ban")
    public void ban(CommandIssuer sender, @Conditions("other_player") @Name("Player") OfflinePlayer player, @Optional @Name("Reason") @Default("none") String reason) {
        new PunishmentBuilder(punishManager).
                operator(sender).
                target(player).
                reason(reason).
                build().punish(BAN);
    }
}
