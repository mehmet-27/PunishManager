package com.mehmet_27.punishmanager.bungee.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.bungee.PMBungee;
import com.mehmet_27.punishmanager.managers.StorageManager;
import com.mehmet_27.punishmanager.objects.OfflinePlayer;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;
import java.util.regex.Matcher;

import static com.mehmet_27.punishmanager.objects.Punishment.PunishType.TEMPMUTE;

@CommandAlias("punishmanager")
@CommandPermission("punishmanager.command.tempmute")
public class TempMuteCommand extends BaseCommand {

    @Dependency
    private StorageManager storageManager;

    @CommandCompletion("@players @units Reason")
    @Description("{@@tempmute.description}")
    @CommandAlias("tempmute")
    public void tempMute(CommandSender sender, @Conditions("other_player") @Name("Player") OfflinePlayer player, @Name("Time") String time, @Optional @Name("Reason") String reason) {
        UUID uuid = player.getUniqueId();
        String playerName = player.getName();

        Matcher matcher = Utils.NumberAndUnit.matcher(time.toLowerCase());
        if (!matcher.find()) {
            throw new InvalidCommandArgument();
        }
        int number = Integer.parseInt(matcher.group("number"));
        String unit = matcher.group("unit");
        long start = System.currentTimeMillis();
        long end = start + Utils.convertToMillis(number, unit);
        String server = "ALL";
        ProxiedPlayer onlinePlayer = PMBungee.getInstance().getProxy().getPlayer(uuid);

        if (onlinePlayer != null && onlinePlayer.isConnected()) {
            server = onlinePlayer.getServer().getInfo().getName();
        }

        String ip = PunishManager.getInstance().getMethods().getPlayerIp(uuid);
        Punishment punishment = new Punishment(playerName, uuid, ip, TEMPMUTE, reason, sender.getName(), server, start, end, -1);
        PunishManager.getInstance().getMethods().callPunishEvent(punishment);
    }
}
