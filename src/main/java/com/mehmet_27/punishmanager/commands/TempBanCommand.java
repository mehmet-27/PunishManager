package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.Punishment;
import com.mehmet_27.punishmanager.managers.MessageManager;
import com.mehmet_27.punishmanager.managers.PunishmentManager;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.mehmet_27.punishmanager.Punishment.PunishType.TEMPBAN;
import static com.mehmet_27.punishmanager.utils.Utils.NumberAndUnit;

@CommandAlias("tempban")
@CommandPermission("punishmanager.command.tempban")
public class TempBanCommand extends BaseCommand {

    @Dependency
    private PunishmentManager punishmentManager;
    private final MessageManager messageManager = PunishManager.getInstance().getMessageManager();

    @Default
    @CommandCompletion("@players @units Reason")
    public void tempBan(CommandSender sender, @Conditions("other_player") @Name("Player") String playerName, @Name("Time") String time, @Optional @Name("Reason") @Default("none") String reason) {
        ProxiedPlayer player = PunishManager.getInstance().getProxy().getPlayer(playerName);
        String uuid = (player != null && player.isConnected()) ? player.getUniqueId().toString() : playerName;
        Punishment punishment = punishmentManager.getPunishment(playerName, "ban");
        if (punishment != null && punishment.playerIsBanned()) {
            sender.sendMessage(new TextComponent(messageManager.getAlreadyPunishedMessage(TEMPBAN.name()).replace("%name%", playerName)));
            return;
        }
        if (!Utils.isMatcherFound(time)) {
            sender.sendMessage(new TextComponent("Please specify a valid time."));
            return;
        }
        Matcher matcher = NumberAndUnit.matcher(time.toLowerCase());
        if (!matcher.find()) {
            return;
        }
        int number = Integer.parseInt(matcher.group("number"));
        String unit = matcher.group("unit");
        long start = System.currentTimeMillis();
        long end = start + Utils.convertToMillis(number, unit);
        punishment = new Punishment(playerName, uuid, TEMPBAN, reason, sender.getName(), start, end);
        punishmentManager.AddPunish(punishment);
        sender.sendMessage(new TextComponent(messageManager.getPunishedMessage(TEMPBAN.name()).replace("%name%", playerName)));
        Utils.disconnectPlayer(punishment);
    }
}
