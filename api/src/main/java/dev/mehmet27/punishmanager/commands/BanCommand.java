package dev.mehmet27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.managers.StorageManager;
import dev.mehmet27.punishmanager.objects.OfflinePlayer;
import dev.mehmet27.punishmanager.objects.Punishment;

import java.util.UUID;

import static dev.mehmet27.punishmanager.objects.Punishment.PunishType.BAN;

@CommandAlias("%punishmanager")
@CommandPermission("punishmanager.command.ban")
public class BanCommand extends BaseCommand {

    @Dependency
    private StorageManager storageManager;

    @Dependency
    private PunishManager punishManager;

    @CommandCompletion("@players Reason")
    @Description("{@@ban.description}")
    @CommandAlias("%ban")
    public void ban(CommandIssuer sender, @Conditions("other_player") @Name("Player") OfflinePlayer player, @Optional @Name("Reason") @Default("none") String reason) {
        UUID uuid = player.getUniqueId();
        String playerName = player.getName();

        String server = "ALL";
        if (punishManager.getMethods().isOnline(uuid)) {
            server = punishManager.getMethods().getServer(uuid);
        }

        String operator = sender.isPlayer() ? storageManager.getOfflinePlayer(sender.getUniqueId()).getName() : "CONSOLE";
        UUID operatorUuid = sender.isPlayer() ? sender.getUniqueId() : null;
        String ip = PunishManager.getInstance().getMethods().getPlayerIp(uuid);
        Punishment punishment = new Punishment(playerName, uuid, ip, BAN, reason, operator, operatorUuid, server, -1);
        PunishManager.getInstance().getMethods().callPunishEvent(punishment);
    }
}
