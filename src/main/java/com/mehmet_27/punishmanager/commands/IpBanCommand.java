package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.PlayerPunishEvent;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.objects.Ip;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.objects.Reason;
import com.mehmet_27.punishmanager.managers.DatabaseManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import static com.mehmet_27.punishmanager.objects.Punishment.PunishType.IPBAN;

@CommandAlias("punishmanager")
@CommandPermission("punishmanager.command.banip")
public class IpBanCommand extends BaseCommand {

    @Dependency
    private DatabaseManager dataBaseManager;
    @Dependency
    private ConfigManager configManager;

    @CommandCompletion("@players Reason")
    @Description("Bans a player's ip address.")
    @CommandAlias("ipban")
    public void banIp(CommandSender sender, @Conditions("other_player") @Name("Player") String playerName, @Optional @Name("Reason") String reason) {
        ProxiedPlayer player = PunishManager.getInstance().getProxy().getPlayer(playerName);
        String uuid = (player != null && player.isConnected()) ? player.getUniqueId().toString() : playerName;
        Punishment punishment = dataBaseManager.getBan(playerName);
        if (punishment != null && punishment.isBanned()) {
            sender.sendMessage(new TextComponent(configManager.getMessage("ipban.alreadyPunished", sender.getName()).
                    replace("%player%", playerName)));
            return;
        }
        String ip = new Ip(playerName).getPlayerIp();
        punishment = new Punishment(playerName, uuid, ip, IPBAN, new Reason(reason, playerName).getReason(), sender.getName());
        PunishManager.getInstance().getProxy().getPluginManager().callEvent(new PlayerPunishEvent(punishment));
    }
}
