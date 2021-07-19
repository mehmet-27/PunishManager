package com.mehmet_27.punishmanager.commands;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.Punishment;
import com.mehmet_27.punishmanager.managers.DisconnectManager;
import com.mehmet_27.punishmanager.managers.PunishmentManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.List;

import static com.mehmet_27.punishmanager.managers.PermissionManager.Permissions.COMMAND_BAN;

public class BanCommand extends Command implements TabExecutor {

    private PunishmentManager punishmentManager = new PunishmentManager();
    private DisconnectManager disconnectManager = new DisconnectManager();

    public BanCommand(String name, String permission) {
        super(name, permission);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(new TextComponent("Please specify a player."));
        } else {
            String playerName = args[0];
            String uuid;
            ProxiedPlayer player = PunishManager.getInstance().getProxy().getPlayer(playerName);
            if (player != null && player.isConnected()) {
                uuid = player.getUniqueId().toString();
            } else {
                uuid = playerName;
            }
            //if (!playerName.equals(sender.getName())) {
            if (!punishmentManager.PlayerIsBanned(args[0])) {
                Punishment punishment = new Punishment(playerName, uuid, Punishment.PunishType.BAN, "", sender.getName());
                if (args.length == 1) {
                    punishmentManager.BanPlayer(punishment);
                    sender.sendMessage(new TextComponent(punishment.getPlayerName() + " banned by " + punishment.getOperator() + " due to " + punishment.getReason()));
                    disconnectManager.DisconnectPlayer(punishment);
                } else {
                    String reason = "";
                    for (int i = 1; i < args.length; i++) {
                        String newReasonElement = args[i];
                        reason = reason + " " + newReasonElement;
                    }
                    punishment.setReason(reason);
                    punishmentManager.BanPlayer(punishment);
                    sender.sendMessage(new TextComponent(punishment.getPlayerName() + " banned by " + punishment.getOperator() + " due to " + punishment.getReason()));
                    disconnectManager.DisconnectPlayer(punishment);
                }
            } else {
                sender.sendMessage(new TextComponent("This player has already been banned."));
            }
            /*}
            else{
                sender.sendMessage(new TextComponent("You can't ban yourself!"));
            }*/
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (sender.hasPermission(PunishManager.getPermissionManager().getPermission(COMMAND_BAN))) {
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
    }
}
