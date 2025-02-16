package dev.mehmet27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.objects.OfflinePlayer;
import dev.mehmet27.punishmanager.objects.PunishmentBuilder;
import dev.mehmet27.punishmanager.objects.PunishmentRevoke;

@CommandAlias("%punishmanager")
@CommandPermission("punishmanager.command.unmute")
public class UnMuteCommand extends BaseCommand {

    @Dependency
    private PunishManager punishManager;

    @CommandCompletion("@players")
    @Description("{@@unmute.description}")
    @CommandAlias("%unmute")
    public void unMute(CommandIssuer sender, @Name("Player") OfflinePlayer player, @Optional @Name("Reason") @Default("none") String reason) {
        new PunishmentBuilder(punishManager).
                operator(sender).
                target(player).
                reason(reason).
                build().revoke(PunishmentRevoke.RevokeType.UNMUTE);
    }
}
