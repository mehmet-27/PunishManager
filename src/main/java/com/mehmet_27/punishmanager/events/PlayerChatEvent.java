package com.mehmet_27.punishmanager.events;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.managers.PunishmentManager;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

import java.util.List;

public class PlayerChatEvent implements Listener {

    PunishmentManager punishmentManager = PunishManager.getInstance().getPunishmentManager();
    Configuration config = PunishManager.getInstance().getConfigManager().getConfig();

    @EventHandler
    public void onChat(ChatEvent event) {
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        Punishment punishment = punishmentManager.getPunishment(player.getName(), "mute");
        if (punishment == null || !punishment.isMuted()) {
            return;
        }
        if (punishment.isExpired()){
            PunishManager.getInstance().getDiscordManager().removePunishedRole(punishment);
            return;
        }
        if (event.isCommand()){
            List<String> bannedCommands = config.getStringList("banned-commands-while-mute");
            String command = event.getMessage().substring(1).split(" ")[0];
            if (bannedCommands.contains(command)){
                event.setCancelled(true);
                Utils.sendLayout(punishment);
            }
        }else {
            event.setCancelled(true);
            Utils.sendLayout(punishment);
        }
    }
}
