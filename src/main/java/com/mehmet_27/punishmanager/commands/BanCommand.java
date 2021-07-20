package com.mehmet_27.punishmanager.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.Punishment;
import com.mehmet_27.punishmanager.managers.DisconnectManager;
import com.mehmet_27.punishmanager.managers.PunishmentManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@CommandAlias("ban")
@CommandPermission("punishmanager.command.ban")
public class BanCommand extends BaseCommand {

    @Dependency
    private PunishmentManager punishmentManager;
    @Dependency
    private DisconnectManager disconnectManager;

    @Default
    @CommandCompletion("@players Reason")
    public void ban(CommandSender sender, @Conditions("other_player") @Name("Player") String playerName, @Optional @Name("Reason") @Default("none") String reason) {
        ProxiedPlayer player = PunishManager.getInstance().getProxy().getPlayer(playerName);
        String uuid = (player != null && player.isConnected()) ? player.getUniqueId().toString() : playerName;
        Punishment punishment = punishmentManager.getPunishment(playerName, "ban");
        if (punishment != null && punishment.playerIsBanned()) {
            sender.sendMessage(new TextComponent("This player has already been banned."));
            return;
        }
        punishment = new Punishment(playerName, uuid, Punishment.PunishType.BAN, reason, sender.getName());
        punishmentManager.AddPunish(punishment);
        sender.sendMessage(new TextComponent(punishment.getPlayerName() + " banned by " + punishment.getOperator() + " due to " + punishment.getReason()));
        disconnectManager.DisconnectPlayer(punishment);
    }

    /*public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (sender.hasPermission("punishmanager.command.ban")) {
            if (args.length == 1) {
                List<String> playerNames = new ArrayList<>();
                for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                    if (!sender.getName().equals(player.getName())) {
                        playerNames.add(player.getName());
                        //don't add if player has ban protection permission
                    }
                }
                return playerNames;
            }
            if (args.length == 2) {
                List<String> reason = new ArrayList<>();
                reason.add("Reason...");
                return reason;
            }
        }
        return new ArrayList<>();
    }*/
}
