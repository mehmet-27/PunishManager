package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

import static com.mehmet_27.punishmanager.objects.Punishment.PunishType.KICK;
import static com.mehmet_27.punishmanager.utils.Utils.sendTextComponent;

@CommandAlias("punishmanager")
@CommandPermission("punishmanager.command.kick")
public class KickCommand extends BaseCommand {

    @Dependency
    private PunishManager punishManager;

    @CommandCompletion("@players Reason")
    @Description("{@@command.kick.description}")
    @CommandAlias("kick")
    public void kick(CommandSender sender, @Conditions("other_player") @Name("Player") String playerName, @Optional @Name("Reason") String reason) {
        ProxiedPlayer player = punishManager.getProxy().getPlayer(playerName);
        if (player == null || !player.isConnected()) {
            sendTextComponent(sender, "kick.notOnline");
            return;
        }

        UUID uuid = player.getUniqueId();
        String ip = Utils.getPlayerIp(playerName);
            Punishment punishment = new Punishment(playerName, uuid, ip, KICK, reason,  sender.getName());
        Utils.sendLayout(punishment);
        sendTextComponent(sender, "kick.punished");
    }
}
