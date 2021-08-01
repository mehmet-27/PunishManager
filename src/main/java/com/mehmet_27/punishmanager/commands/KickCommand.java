package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.Punishment;
import com.mehmet_27.punishmanager.Reason;
import com.mehmet_27.punishmanager.managers.MessageManager;
import com.mehmet_27.punishmanager.managers.PunishmentManager;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import static com.mehmet_27.punishmanager.Punishment.PunishType.KICK;

@CommandAlias("kick")
@CommandPermission("punishmanager.command.kick")
public class KickCommand extends BaseCommand {

    @Dependency
    private MessageManager messageManager;

    @Default
    @CommandCompletion("@players Reason")
    public void kick(CommandSender sender, @Conditions("other_player") @Name("Player") String playerName, @Optional @Name("Reason") String reason) {
        ProxiedPlayer player = PunishManager.getInstance().getProxy().getPlayer(playerName);
        String uuid = (player != null && player.isConnected()) ? player.getUniqueId().toString() : playerName;
        if (player != null && player.isConnected()) {
            Punishment punishment = new Punishment(playerName, uuid, player.getSocketAddress().toString().substring(1).split(":")[0], KICK, new Reason(reason).getReason(), sender.getName());
            Utils.disconnectPlayer(punishment);
            sender.sendMessage(new TextComponent(messageManager.getMessage("kick.punished").
                    replace("%name%", playerName)));
        } else {
            sender.sendMessage(new TextComponent(messageManager.getMessage("kick.notOnline").
                    replace("%name%", playerName)));
        }
    }
}
