package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.events.PlayerPunishEvent;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.managers.StorageManager;
import com.mehmet_27.punishmanager.objects.OfflinePlayer;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.api.CommandSender;

import java.util.UUID;
import java.util.regex.Matcher;

import static com.mehmet_27.punishmanager.objects.Punishment.PunishType.TEMPBAN;
import static com.mehmet_27.punishmanager.utils.Utils.NumberAndUnit;
import static com.mehmet_27.punishmanager.utils.Utils.sendTextComponent;

@CommandAlias("punishmanager")
@CommandPermission("punishmanager.command.tempban")
public class TempBanCommand extends BaseCommand {

    @Dependency
    private StorageManager storageManager;
    @Dependency
    private ConfigManager configManager;
    @Dependency
    private PunishManager punishManager;

    @CommandCompletion("@players @units Reason")
    @Description("{@@tempban.description}")
    @CommandAlias("tempban")
    public void tempBan(CommandSender sender, @Conditions("other_player") @Name("Player") OfflinePlayer player, @Name("Time") String time, @Optional @Name("Reason") String reason) {
        UUID uuid = player.getUniqueId();
        String playerName = player.getPlayerName();

        Punishment punishment = storageManager.getBan(uuid);
        if (punishment != null && punishment.isBanned()) {
            Utils.sendTextComponent(sender, playerName, "tempban.alreadyPunished");
            return;
        }
        Matcher matcher = NumberAndUnit.matcher(time.toLowerCase());
        if (!matcher.find()) {
            throw new InvalidCommandArgument();
        }
        int number = Integer.parseInt(matcher.group("number"));
        String unit = matcher.group("unit");
        long start = System.currentTimeMillis();
        long end = start + Utils.convertToMillis(number, unit);
        String ip = Utils.getPlayerIp(uuid);
        punishment = new Punishment(playerName, uuid, ip, TEMPBAN, reason, sender.getName(), start, end, -1);
        punishManager.getProxy().getPluginManager().callEvent(new PlayerPunishEvent(punishment));
    }
}
