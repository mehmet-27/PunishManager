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
@CommandPermission("punishmanager.command.tempban")
public class TempBanCommand extends BaseCommand {

    @Dependency
    private StorageManager storageManager;

    @Dependency
    private PunishManager punishManager;

    @CommandCompletion("@players @units Reason")
    @Description("{@@tempban.description}")
    @CommandAlias("%tempban")
    public void tempBan(CommandIssuer sender, @Conditions("other_player") @Name("Player") OfflinePlayer player, @Name("Time") String time, @Optional @Name("Reason") String reason) {
        UUID uuid = player.getUniqueId();
        String playerName = player.getName();

        long start = System.currentTimeMillis();
        long end = start + Utils.convertToMillis(time);
        String server = "ALL";
        if (punishManager.getMethods().isOnline(uuid)) {
            server = punishManager.getMethods().getServer(uuid);
        }

        String operator = sender.isPlayer() ? storageManager.getOfflinePlayer(sender.getUniqueId()).getName() : "CONSOLE";
        UUID operatorUuid = sender.isPlayer() ? sender.getUniqueId() : null;
        String ip = PunishManager.getInstance().getMethods().getPlayerIp(uuid);
        Punishment punishment = new Punishment(playerName, uuid, ip, Punishment.PunishType.TEMPBAN, reason, operator, operatorUuid, server, start, end, -1);
        PunishManager.getInstance().getMethods().callPunishEvent(punishment);
    }
}
