package com.mehmet_27.punishmanager.bukkit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.bukkit.utils.Utils;
import com.mehmet_27.punishmanager.managers.StorageManager;
import com.mehmet_27.punishmanager.objects.OfflinePlayer;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.utils.UtilsCore;
import org.bukkit.command.CommandSender;

import java.util.UUID;
import java.util.regex.Matcher;

import static com.mehmet_27.punishmanager.objects.Punishment.PunishType.BAN;
import static com.mehmet_27.punishmanager.objects.Punishment.PunishType.TEMPBAN;

@CommandAlias("punishmanager")
@CommandPermission("punishmanager.command.tempban")
public class TempBanCommand extends BaseCommand {

    @Dependency
    private StorageManager storageManager;

    @CommandCompletion("@players @units Reason")
    @Description("{@@tempban.description}")
    @CommandAlias("tempban")
    public void tempBan(CommandSender sender, @Conditions("other_player") @Name("Player") OfflinePlayer player, @Name("Time") String time, @Optional @Name("Reason") String reason) {
        UUID uuid = player.getUniqueId();
        String playerName = player.getName();

        Punishment punishment = storageManager.getBan(uuid);
        if (punishment != null && punishment.isBanned()) {
            Utils.sendText(sender, playerName, "tempban.alreadyPunished");
            return;
        }
        Matcher matcher = UtilsCore.NumberAndUnit.matcher(time.toLowerCase());
        if (!matcher.find()) {
            throw new InvalidCommandArgument();
        }
        int number = Integer.parseInt(matcher.group("number"));
        String unit = matcher.group("unit");
        long start = System.currentTimeMillis();
        long end = start + UtilsCore.convertToMillis(number, unit);
        String ip = PunishManager.getInstance().getMethods().getPlayerIp(uuid);
        punishment = new Punishment(playerName, uuid, ip, TEMPBAN, reason, sender.getName(), "ALL", start, end, -1);
        PunishManager.getInstance().getMethods().callPunishEvent(punishment);
    }
}
