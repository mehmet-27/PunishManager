package dev.mehmet27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.managers.StorageManager;
import dev.mehmet27.punishmanager.objects.OfflinePlayer;
import dev.mehmet27.punishmanager.objects.Punishment;
import dev.mehmet27.punishmanager.utils.Utils;

import java.util.UUID;

@CommandAlias("%punishmanager")
@CommandPermission("punishmanager.command.kick")
public class KickCommand extends BaseCommand {

    @Dependency
    private StorageManager storageManager;

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
        UUID uuid = player.getUniqueId();

        String server = "ALL";

        if (punishManager.getMethods().isOnline(uuid)) {
            server = punishManager.getMethods().getServer(uuid);
        }

        String operator = sender.isPlayer() ? storageManager.getOfflinePlayer(sender.getUniqueId()).getName() : "CONSOLE";
        String ip = PunishManager.getInstance().getMethods().getPlayerIp(uuid);
        Punishment punishment = new Punishment(playerName, uuid, ip, Punishment.PunishType.KICK, reason, operator, operatorUuid, server, -1);
        punishManager.getMethods().kickPlayer(uuid, Utils.getLayout(punishment));
        Utils.sendText(sender.getUniqueId(), playerName, "kick.punished");
    }
}
