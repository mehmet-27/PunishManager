package com.mehmet_27.punishmanager.commands;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.Punishment;
import com.mehmet_27.punishmanager.managers.DisconnectManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class KickCommand extends Command {
    private DisconnectManager disconnectManager = new DisconnectManager();

    public KickCommand(String name, String permission) {
        super(name, permission);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0)  {
            sender.sendMessage(new TextComponent("Please specify a player."));
        }
        else {
            String playerName = args[0];
            String uuid;
            ProxiedPlayer player = PunishManager.getInstance().getProxy().getPlayer(playerName);
            if (player != null && player.isConnected()) {
                uuid = player.getUniqueId().toString();
            } else {
                uuid = playerName;
            }
            if (player != null && player.isConnected()) {
                Punishment punishment = new Punishment(playerName, uuid, Punishment.PunishType.KICK, "", sender.getName());
                if (args.length == 1){
                    disconnectManager.DisconnectPlayer(punishment);
                    sender.sendMessage(new TextComponent("The player was kicked from the server."));
                }
                else{
                    String reason = "";
                    for (int i = 1; i < args.length; i++){
                        String newReasonElement = args[i];
                        reason = reason + " " + newReasonElement;
                    }
                    punishment.setReason(reason);
                    disconnectManager.DisconnectPlayer(punishment);
                    sender.sendMessage(new TextComponent("The player was kicked from the server."));
                }
            } else {
                sender.sendMessage(new TextComponent("You cant kick offline players."));
            }
        }
    }
}
