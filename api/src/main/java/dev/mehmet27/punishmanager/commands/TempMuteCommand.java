package dev.mehmet27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.objects.OfflinePlayer;
import dev.mehmet27.punishmanager.objects.PunishmentBuilder;
import dev.mehmet27.punishmanager.utils.Utils;

import static dev.mehmet27.punishmanager.objects.Punishment.PunishType.TEMPMUTE;

@CommandAlias("%punishmanager")
@CommandPermission("punishmanager.command.tempmute")
public class TempMuteCommand extends BaseCommand {

    @Dependency
    private PunishManager punishManager;

    @CommandCompletion("@players @units Reason")
    @Description("{@@tempmute.description}")
    @CommandAlias("%tempmute")
    public void tempMute(CommandIssuer sender, @Conditions("other_player") @Name("Player") OfflinePlayer player, @Name("Time") String time, @Optional @Name("Reason") String reason) {
        long start = System.currentTimeMillis();
        long end = start + Utils.convertToMillis(time);

        new PunishmentBuilder(punishManager).
                operator(sender).
                target(player).
                reason(reason).
                end(end).
                build().punish(TEMPMUTE);
    }
}
