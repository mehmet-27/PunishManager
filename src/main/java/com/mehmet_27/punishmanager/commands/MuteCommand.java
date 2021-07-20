package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.Punishment;
import com.mehmet_27.punishmanager.managers.PunishmentManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@CommandAlias("mute")
@CommandPermission("punishmanager.command.mute")
public class MuteCommand extends BaseCommand {

    PunishmentManager punishmentManager = new PunishmentManager();

    @Default
    @CommandCompletion("@players Reason")
    public void mute(CommandSender sender, @Conditions("other_player") @Name("Player") String playerName, @Optional @Name("Reason") @Default("none") String reason) {
        ProxiedPlayer player = PunishManager.getInstance().getProxy().getPlayer(playerName);
        String uuid = (player != null && player.isConnected()) ? player.getUniqueId().toString() : playerName;
        Punishment punishment = punishmentManager.getPunishment(playerName, "mute");
        if (punishment != null && punishment.playerIsMuted()) {
            sender.sendMessage(new TextComponent("This player has already been muted."));
            return;
        }
        punishment = new Punishment(playerName, uuid, Punishment.PunishType.MUTE, reason, sender.getName());
        punishmentManager.AddPunish(punishment);
        sender.sendMessage(new TextComponent(punishment.getPlayerName() + " muted by " + punishment.getOperator() + " due to " + punishment.getReason()));
    }
}