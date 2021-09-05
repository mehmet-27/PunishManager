package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.objects.Reason;
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
    @Description("Kicks a player from the server.")
    @CommandAlias("kick")
    public void kick(CommandSender sender, @Conditions("other_player") @Name("Player") String playerName, @Optional @Name("Reason") String reason) {
        ProxiedPlayer player = punishManager.getProxy().getPlayer(playerName);
        if (player == null || !player.isConnected()) {
            sendTextComponent(sender, "kick.notOnline");
            return;
        }

        UUID uuid = player.getUniqueId();
        Punishment punishment = new Punishment(playerName, uuid, player.getSocketAddress().toString().substring(1).split(":")[0], KICK, new Reason(reason, playerName).getReason(), sender.getName());
        Utils.sendLayout(punishment);
        sendTextComponent(sender, "kick.punished");
    }
}
