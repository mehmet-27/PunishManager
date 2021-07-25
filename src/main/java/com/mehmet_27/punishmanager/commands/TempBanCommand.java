package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.Punishment;
import com.mehmet_27.punishmanager.managers.PunishmentManager;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.mehmet_27.punishmanager.Punishment.PunishType.TEMPBAN;
import static com.mehmet_27.punishmanager.utils.Utils.REGEX;

@CommandAlias("tempban")
@CommandPermission("punishmanager.command.tempban")
public class TempBanCommand extends BaseCommand {

    @Dependency
    private PunishmentManager punishmentManager;

    @Default
    @CommandCompletion("@players Reason")
    public void tempBan(CommandSender sender, @Conditions("other_player") @Name("Player") String playerName, @Name("Time") String time, @Optional @Name("Reason") @Default("none") String reason) {
        ProxiedPlayer player = PunishManager.getInstance().getProxy().getPlayer(playerName);
        String uuid = (player != null && player.isConnected()) ? player.getUniqueId().toString() : playerName;
        Punishment punishment = punishmentManager.getPunishment(playerName, "ban");
        if (punishment != null && punishment.playerIsBanned()) {
            sender.sendMessage(new TextComponent("This player has already been banned."));
            return;
        }
        if (!Utils.isMatcherFound(time)) {
            sender.sendMessage(new TextComponent("Please specify a valid time."));
            return;
        }
        Matcher matcher = REGEX.matcher(time.toLowerCase());
        if (!matcher.find()) {
            return;
        }
        int number = Integer.parseInt(matcher.group("number"));
        String unit = matcher.group("unit");
        long start = System.currentTimeMillis();
        long end = start + Utils.convertToMillis(number, unit);
        punishment = new Punishment(playerName, uuid, TEMPBAN, reason, sender.getName(), start, end);
        punishmentManager.AddPunish(punishment);
        sender.sendMessage(new TextComponent(punishment.getPlayerName() + " banned by " + punishment.getOperator() + " due to " + punishment.getReason()));
        Utils.disconnectPlayer(punishment);
    }
}
