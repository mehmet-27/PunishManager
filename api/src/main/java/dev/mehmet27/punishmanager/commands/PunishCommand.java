package dev.mehmet27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.objects.OfflinePlayer;

@CommandAlias("%punishmanager")
@CommandPermission("punishmanager.command.punish")
public class PunishCommand extends BaseCommand {

    @Dependency
    private PunishManager punishManager;

    @CommandCompletion("@players")
    @Description("{@@punish.description}")
    @CommandAlias("%punish")
    public void punish(CommandIssuer sender, @Conditions("other_player") @Name("Player") OfflinePlayer player) {
        if (!sender.isPlayer()) return;
        Object senderPlayer = punishManager.getMethods().getPlayer(sender.getUniqueId());
        punishManager.getMethods().openPunishFrame(senderPlayer, player);
    }
}