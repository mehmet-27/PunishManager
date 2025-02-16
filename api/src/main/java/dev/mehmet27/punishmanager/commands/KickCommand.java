package dev.mehmet27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.objects.OfflinePlayer;
import dev.mehmet27.punishmanager.objects.PunishmentBuilder;
import dev.mehmet27.punishmanager.utils.Utils;

import java.util.UUID;

import static dev.mehmet27.punishmanager.objects.Punishment.PunishType.KICK;

@CommandAlias("%punishmanager")
@CommandPermission("punishmanager.command.kick")
public class KickCommand extends BaseCommand {

    @Dependency
    private PunishManager punishManager;

    @CommandCompletion("@players Reason")
    @Description("{@@kick.description}")
    @CommandAlias("%kick")
    public void kick(CommandIssuer sender, @Conditions("other_player") @Name("Player") OfflinePlayer player, @Optional @Name("Reason") String reason) {
        String playerName = player.getName();
        UUID operatorUuid = sender.isPlayer() ? sender.getUniqueId() : null;
        if (!punishManager.getMethods().isOnline(player.getUniqueId())) {
            Utils.sendText(operatorUuid, playerName, "kick.notOnline");
            return;
        }

        new PunishmentBuilder(punishManager).
                operator(sender).
                target(player).
                reason(reason).
                build().punish(KICK);
    }
}
