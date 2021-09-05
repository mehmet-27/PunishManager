package com.mehmet_27.punishmanager.listeners;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.DatabaseManager;
import com.mehmet_27.punishmanager.managers.DiscordManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

import java.util.List;

import static com.mehmet_27.punishmanager.managers.DiscordAction.REMOVE;

public class PlayerChatEvent implements Listener {

    Configuration config = PunishManager.getInstance().getConfigManager().getConfig();
    DatabaseManager dataBaseManager = PunishManager.getInstance().getDataBaseManager();
    DiscordManager discordManager = PunishManager.getInstance().getDiscordManager();

    @EventHandler
    public void onChat(ChatEvent event) {
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        Punishment punishment = dataBaseManager.getMute(player.getName());
        if (punishment == null) return;
        if (punishment.isExpired()) {
            dataBaseManager.unPunishPlayer(punishment);
            discordManager.updateRole(punishment, REMOVE);
            return;
        }
        if (event.isCommand()) {
            List<String> bannedCommands = config.getStringList("banned-commands-while-mute");
            String command = event.getMessage().substring(1).split(" ")[0];
            if (bannedCommands.contains(command)) {
                event.setCancelled(true);
                Utils.sendLayout(punishment);
            }
        } else {
            event.setCancelled(true);
            Utils.sendLayout(punishment);
        }
    }
}
