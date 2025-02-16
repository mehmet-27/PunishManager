package dev.mehmet27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.objects.OfflinePlayer;
import dev.mehmet27.punishmanager.objects.PunishmentBuilder;

import static dev.mehmet27.punishmanager.objects.Punishment.PunishType.MUTE;

@CommandAlias("%punishmanager")
@CommandPermission("punishmanager.command.mute")
public class MuteCommand extends BaseCommand {
    @Dependency
    private PunishManager punishManager;

    @CommandCompletion("@players Reason")
    @Description("{@@mute.description}")
    @CommandAlias("%mute")
    public void mute(CommandIssuer sender, @Conditions("other_player") @Name("Player") OfflinePlayer player, @Optional @Name("Reason") String reason) {
        new PunishmentBuilder(punishManager).
                operator(sender).
                target(player).
                reason(reason).
                build().punish(MUTE);
    }
}
