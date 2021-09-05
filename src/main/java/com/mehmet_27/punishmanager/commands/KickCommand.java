package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import static com.mehmet_27.punishmanager.objects.Punishment.PunishType.KICK;

@CommandAlias("punishmanager")
@CommandPermission("punishmanager.command.kick")
public class KickCommand extends BaseCommand {

    @Dependency
    private ConfigManager configManager;

    @CommandCompletion("@players Reason")
    @Description("{@@command.kick.description}")
    @CommandAlias("kick")
    public void kick(CommandSender sender, @Conditions("other_player") @Name("Player") String playerName, @Optional @Name("Reason") String reason) {
        ProxiedPlayer player = PunishManager.getInstance().getProxy().getPlayer(playerName);
        String uuid = (player != null && player.isConnected()) ? player.getUniqueId().toString() : playerName;
        if (player != null && player.isConnected()) {
            String ip = Utils.getPlayerIp(playerName);
            Punishment punishment = new Punishment(playerName, uuid, ip, KICK, reason, sender.getName());
            Utils.sendLayout(punishment);
            sender.sendMessage(new TextComponent(configManager.getMessage("kick.punished", sender.getName()).
                    replace("%player%", playerName)));
        } else {
            sender.sendMessage(new TextComponent(configManager.getMessage("kick.notOnline", sender.getName()).
                    replace("%player%", playerName)));
        }
    }
}
