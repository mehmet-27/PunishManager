package com.mehmet_27.punishmanager.bungee.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.bungee.PMBungee;
import com.mehmet_27.punishmanager.bungee.Utils.Utils;
import com.mehmet_27.punishmanager.managers.StorageManager;
import com.mehmet_27.punishmanager.objects.OfflinePlayer;
import com.mehmet_27.punishmanager.objects.Punishment;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

import static com.mehmet_27.punishmanager.objects.Punishment.PunishType.BAN;

@CommandAlias("punishmanager")
@CommandPermission("punishmanager.command.ban")
public class BanCommand extends BaseCommand {

    @Dependency
    private StorageManager storageManager;

    @CommandCompletion("@players Reason")
    @Description("{@@ban.description}")
    @CommandAlias("ban")
    public void ban(CommandSender sender, @Conditions("other_player") @Name("Player") OfflinePlayer player, @Optional @Name("Reason") @Default("none") String reason) {
        UUID uuid = player.getUniqueId();
        String playerName = player.getName();

        Punishment punishment = storageManager.getBan(uuid);
        if (punishment != null && punishment.isBanned()) {
            Utils.sendText(sender, playerName, "ban.alreadyPunished");
            return;
        }

        String server = "ALL";
        ProxiedPlayer onlinePlayer = PMBungee.getInstance().getProxy().getPlayer(uuid);

        if (onlinePlayer != null && onlinePlayer.isConnected()){
            server = onlinePlayer.getServer().getInfo().getName();
        }

        String ip = PunishManager.getInstance().getMethods().getPlayerIp(uuid);
        punishment = new Punishment(playerName, uuid, ip, BAN, reason, sender.getName(), server, -1);
        PunishManager.getInstance().getMethods().callPunishEvent(punishment);
    }
}
