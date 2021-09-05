package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.events.PlayerPunishEvent;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.DatabaseManager;
import com.mehmet_27.punishmanager.objects.Ip;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.objects.Reason;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.managers.DatabaseManager;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;
import java.util.regex.Matcher;

import static com.mehmet_27.punishmanager.objects.Punishment.PunishType.TEMPMUTE;
import static com.mehmet_27.punishmanager.utils.Utils.NumberAndUnit;
import static com.mehmet_27.punishmanager.utils.Utils.sendTextComponent;

@CommandAlias("punishmanager")
@CommandPermission("punishmanager.command.tempmute")
public class TempMuteCommand extends BaseCommand {

    @Dependency
    private DatabaseManager dataBaseManager;
    @Dependency
    private PunishManager punishManager;

    @CommandCompletion("@players @units Reason")
    @Description("{@@command.tempmute.description}")
    @CommandAlias("tempmute")
    public void tempMute(CommandSender sender, @Conditions("other_player") @Name("Player") String playerName, @Name("Time") String time, @Optional @Name("Reason") String reason) {
        ProxiedPlayer player = punishManager.getProxy().getPlayer(playerName);
        if (player == null || !player.isConnected()) {
            return;
        }
        UUID uuid = player.getUniqueId();
        Punishment punishment = dataBaseManager.getMute(playerName);
        if (punishment != null && punishment.isMuted()) {
            sendTextComponent(sender, "tempmute.alreadyPunished");
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
        String ip = Utils.getPlayerIp(playerName);
        punishment = new Punishment(playerName, uuid, ip, TEMPMUTE, reason, sender.getName(), start, end);
        punishManager.getProxy().getPluginManager().callEvent(new PlayerPunishEvent(punishment));
    }
}
