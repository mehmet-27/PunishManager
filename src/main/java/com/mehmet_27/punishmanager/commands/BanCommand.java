package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.events.PlayerPunishEvent;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.managers.DatabaseManager;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import static com.mehmet_27.punishmanager.objects.Punishment.PunishType.BAN;

@CommandAlias("punishmanager")
@CommandPermission("punishmanager.command.ban")
public class BanCommand extends BaseCommand {

    @Dependency
    private DatabaseManager dataBaseManager;
    @Dependency
    private ConfigManager configManager;

    @CommandCompletion("@players Reason")
    @Description("{@@command.ban.description}")
    @CommandAlias("ban")
    public void ban(CommandSender sender, @Conditions("other_player") @Name("Player") String playerName, @Optional @Name("Reason") @Default("none") String reason) {
        ProxiedPlayer player = PunishManager.getInstance().getProxy().getPlayer(playerName);
        String uuid = (player != null && player.isConnected()) ? player.getUniqueId().toString() : playerName;
        Punishment punishment = dataBaseManager.getBan(playerName);
        if (punishment != null && punishment.isBanned()) {
            sender.sendMessage(new TextComponent(configManager.getMessage("ban.alreadyPunished", sender.getName()).
                    replace("%player%", playerName)));
            return;
        }
        String ip = Utils.getPlayerIp(playerName);
        punishment = new Punishment(playerName, uuid, ip, BAN, reason, sender.getName());
        PunishManager.getInstance().getProxy().getPluginManager().callEvent(new PlayerPunishEvent(punishment));
    }
}
