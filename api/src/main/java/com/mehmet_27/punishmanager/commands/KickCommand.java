package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.StorageManager;
import com.mehmet_27.punishmanager.objects.OfflinePlayer;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.utils.Utils;

import java.util.UUID;

import static com.mehmet_27.punishmanager.objects.Punishment.PunishType.BAN;
import static com.mehmet_27.punishmanager.objects.Punishment.PunishType.KICK;

@CommandAlias("punishmanager")
@CommandPermission("punishmanager.command.kick")
public class KickCommand extends BaseCommand {

    @Dependency
    private StorageManager storageManager;

    @Dependency
    private PunishManager punishManager;

    @CommandCompletion("@players Reason")
    @Description("{@@kick.description}")
    @CommandAlias("kick")
    public void kick(CommandIssuer sender, @Conditions("other_player") @Name("Player") OfflinePlayer player, @Optional @Name("Reason") String reason) {
        String playerName = player.getName();
        if (punishManager.getMethods().isOnline(player.getUniqueId())) {
            Utils.sendText(sender.getUniqueId(), playerName, "kick.notOnline");
            return;
        }
        UUID uuid = player.getUniqueId();

        String server = "ALL";

        if (punishManager.getMethods().isOnline(uuid)) {
            server = punishManager.getMethods().getServer(uuid);
        }

        String operator = sender.isPlayer() ? storageManager.getOfflinePlayer(sender.getUniqueId()).getName() : "CONSOLE";
        UUID operatorUuid = sender.isPlayer() ? sender.getUniqueId() : null;
        String ip = PunishManager.getInstance().getMethods().getPlayerIp(uuid);
        Punishment punishment = new Punishment(playerName, uuid, ip, KICK, reason, operator, operatorUuid, server, -1);
        punishManager.getMethods().kickPlayer(uuid, Utils.getLayout(punishment));
        Utils.sendText(sender.getUniqueId(), playerName, "kick.punished");
    }
}
