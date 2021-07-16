package com.mehmet_27.punishmanager.commands;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.KickManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class KickCommand extends Command {
    private KickManager kickManager = new KickManager();

    public KickCommand(String name, String permission) {
        super(name, permission);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String operatorName = sender.getName();
        if (args.length == 0)  {
            sender.sendMessage(new TextComponent("Please specify a player."));
        }
        else {
            ProxiedPlayer player = PunishManager.getInstance().getProxy().getPlayer(args[0]);
            if (player != null && player.isConnected()) {
                if (args.length == 1){
                    kickManager.DisconnectPlayer(player, "kick", "", sender.getName());
                    sender.sendMessage(new TextComponent("The player was kicked from the server."));
                }
                else{
                    String reason = "";
                    for (int i = 1; i < args.length; i++){
                        String newReasonElement = args[i];
                        reason = reason + " " + newReasonElement;
                    }
                    kickManager.DisconnectPlayer(player, "kick", reason, sender.getName());
                    sender.sendMessage(new TextComponent("The player was kicked from the server."));
                }
            } else {
                sender.sendMessage(new TextComponent("You cant ban offline players."));
                return;
            }
        }
    }
}
