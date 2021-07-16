package com.mehmet_27.punishmanager.commands;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.KickManager;
import com.mehmet_27.punishmanager.managers.PermissionManager;
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
import static com.mehmet_27.punishmanager.managers.PermissionManager.Permissions.COMMAND_BAN;

public class BanCommand extends Command implements TabExecutor {

    private PunishmentManager punishmentManager = new PunishmentManager();
    private KickManager kickManager = new KickManager();

    public BanCommand(String name, String permission) {
        super(name, permission);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String operator = sender.getName();
        if (args.length == 0) {
            sender.sendMessage(new TextComponent("Please specify a player."));
        } else if (args.length >= 1) {
            ProxiedPlayer player = PunishManager.getInstance().getProxy().getPlayer(args[0]);
            String playerName;
            if (player != null && player.isConnected()) {
                playerName = player.getName();
            } else {
                sender.sendMessage(new TextComponent("You cant ban offline players."));
                return;
            }
            //if (!player.getName().equals(sender.getName())) {
            if (!punishmentManager.PlayerIsBanned(playerName)) {
                if (args.length == 1) {
                    punishmentManager.BanPlayer(player, "none", operator);
                    sender.sendMessage(new TextComponent(player.getName() + " banned by " + operator + " due to " + "none"));
                    kickManager.DisconnectPlayer(player, "ban", "", operator);
                } else {
                    String reason = "";
                    for (int i = 1; i < args.length; i++){
                        String newReasonElement = args[i];
                        reason = reason + " " + newReasonElement;
                    }
                    punishmentManager.BanPlayer(player, reason, operator);
                    sender.sendMessage(new TextComponent(player.getName() + " banned by " + operator + " due to " + reason));
                    kickManager.DisconnectPlayer(player, "ban", reason, operator);
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
