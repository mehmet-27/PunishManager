package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.events.PlayerPunishEvent;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.DatabaseManager;
import com.mehmet_27.punishmanager.objects.Ip;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.objects.Reason;
import com.mehmet_27.punishmanager.managers.DatabaseManager;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

import static com.mehmet_27.punishmanager.objects.Punishment.PunishType.BAN;
import static com.mehmet_27.punishmanager.utils.Utils.sendTextComponent;

@CommandAlias("punishmanager")
@CommandPermission("punishmanager.command.ban")
public class BanCommand extends BaseCommand {

    @Dependency
    private DatabaseManager dataBaseManager;
    @Dependency
    private PunishManager punishManager;

    @CommandCompletion("@players Reason")
    @Description("{@@command.ban.description}")
    @CommandAlias("ban")
    public void ban(CommandSender sender, @Conditions("other_player") @Name("Player") String playerName, @Optional @Name("Reason") @Default("none") String reason) {
        ProxiedPlayer player = punishManager.getProxy().getPlayer(playerName);
        if (player == null || !player.isConnected()) {
            return;
        }

        UUID uuid = player.getUniqueId();

        Punishment punishment = dataBaseManager.getBan(playerName);
        if (punishment != null && punishment.isBanned()) {
            sendTextComponent(sender, "ban.alreadyPunished");
            return;
        }

        String ip = Utils.getPlayerIp(playerName);
        punishment = new Punishment(playerName, uuid, ip, BAN, reason, sender.getName());
        punishManager.getProxy().getPluginManager().callEvent(new PlayerPunishEvent(punishment));
    }
}
