package com.mehmet_27.punishmanager.bungee.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.bungee.Utils.BungeeUtils;
import com.mehmet_27.punishmanager.objects.OfflinePlayer;
import com.mehmet_27.punishmanager.objects.Punishment;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

import static com.mehmet_27.punishmanager.objects.Punishment.PunishType.KICK;

@CommandAlias("punishmanager")
@CommandPermission("punishmanager.command.kick")
public class KickCommand extends BaseCommand {

    @CommandCompletion("@players Reason")
    @Description("{@@kick.description}")
    @CommandAlias("kick")
    public void kick(CommandSender sender, @Conditions("other_player") @Name("Player") OfflinePlayer player, @Optional @Name("Reason") String reason) {
        String playerName = player.getName();
        ProxiedPlayer onlinePlayer = (ProxiedPlayer) PunishManager.getInstance().getMethods().getPlayer(playerName);
        if (onlinePlayer == null || !onlinePlayer.isConnected()) {
            BungeeUtils.sendText(sender, playerName, "kick.notOnline");
            return;
        }
        UUID uuid = player.getUniqueId();

        String server = "ALL";

        if (onlinePlayer.isConnected()){
            server = onlinePlayer.getServer().getInfo().getName();
        }

        String ip = PunishManager.getInstance().getMethods().getPlayerIp(uuid);
        Punishment punishment = new Punishment(playerName, uuid, ip, KICK, reason, sender.getName(), server, -1);
        onlinePlayer.disconnect(BungeeUtils.getLayout(punishment));
        BungeeUtils.sendText(sender, playerName, "kick.punished");
    }
}
