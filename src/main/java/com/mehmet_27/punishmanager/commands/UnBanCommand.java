package com.mehmet_27.punishmanager.commands;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.PunishmentManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class UnBanCommand extends Command {

    private PunishmentManager punishmentManager = new PunishmentManager();

    public UnBanCommand(String name, String permission) {
        super(name, permission);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0){
            sender.sendMessage(new TextComponent("Please specify a player."));
        }
        else if (args.length == 1){
            ProxiedPlayer player = PunishManager.getInstance().getProxy().getPlayer(args[0]);
            String playerName;
            if (player != null && player.isConnected()){
                playerName = player.getName();
            } else {
                playerName = args[0];
            }
            if (punishmentManager.PlayerIsBanned(playerName)){
                punishmentManager.UnBanPlayer(playerName);
                sender.sendMessage(new TextComponent(playerName + "'s ban has been lifted"));
            }
            else{
                sender.sendMessage(new TextComponent("This player is not banned."));
            }
        }
    }
}
