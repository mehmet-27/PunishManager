package com.mehmet_27.punishmanager.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.bukkit.utils.BukkitUtils;
import com.mehmet_27.punishmanager.objects.OfflinePlayer;
import com.mehmet_27.punishmanager.objects.Punishment;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        Player onlinePlayer = (Player) PunishManager.getInstance().getMethods().getPlayer(playerName);
        if (onlinePlayer == null || !onlinePlayer.isOnline()) {
            BukkitUtils.sendText(sender, playerName, "kick.notOnline");
            return;
        }
        UUID uuid = player.getUniqueId();

        String ip = PunishManager.getInstance().getMethods().getPlayerIp(uuid);
        Punishment punishment = new Punishment(playerName, uuid, ip, KICK, reason, sender.getName(), "ALL", - 1);
        onlinePlayer.kickPlayer(BukkitUtils.getLayout(punishment));
        BukkitUtils.sendText(sender, playerName, "kick.punished");
    }
}
