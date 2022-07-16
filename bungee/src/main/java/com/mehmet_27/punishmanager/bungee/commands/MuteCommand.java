package com.mehmet_27.punishmanager.bungee.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.bungee.PMBungee;
import com.mehmet_27.punishmanager.managers.StorageManager;
import com.mehmet_27.punishmanager.objects.OfflinePlayer;
import com.mehmet_27.punishmanager.objects.Punishment;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

import static com.mehmet_27.punishmanager.objects.Punishment.PunishType.MUTE;

@CommandAlias("punishmanager")
@CommandPermission("punishmanager.command.mute")
public class MuteCommand extends BaseCommand {

    @Dependency
    private StorageManager storageManager;

    @CommandCompletion("@players Reason")
    @Description("{@@mute.description}")
    @CommandAlias("mute")
    public void mute(CommandSender sender, @Conditions("other_player") @Name("Player") OfflinePlayer player, @Optional @Name("Reason") String reason) {
        UUID uuid = player.getUniqueId();
        String playerName = player.getName();

        String server = "ALL";
        ProxiedPlayer onlinePlayer = PMBungee.getInstance().getProxy().getPlayer(uuid);

        if (onlinePlayer != null && onlinePlayer.isConnected()) {
            server = onlinePlayer.getServer().getInfo().getName();
        }

        String ip = PunishManager.getInstance().getMethods().getPlayerIp(uuid);
        Punishment punishment = new Punishment(playerName, uuid, ip, MUTE, reason, sender.getName(), server, -1);
        PunishManager.getInstance().getMethods().callPunishEvent(punishment);
    }
}
