package com.mehmet_27.punishmanager.bungee.listeners;

import com.mehmet_27.punishmanager.ConfigurationAdapter;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.bungee.PMBungee;
import com.mehmet_27.punishmanager.bungee.Utils.Utils;
import com.mehmet_27.punishmanager.managers.DiscordManager;
import com.mehmet_27.punishmanager.managers.StorageManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;

public class ChatListener implements Listener {

    private final ConfigurationAdapter config;
    private final PunishManager punishManager = PunishManager.getInstance();
    private final StorageManager storageManager = punishManager.getStorageManager();
    private final DiscordManager discordManager = punishManager.getDiscordManager();

    public ChatListener(PMBungee plugin) {
        config = plugin.getConfigManager().getConfig();
    }

    @EventHandler
    public void onChat(ChatEvent event) {
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        Punishment punishment = storageManager.getMute(player.getUniqueId());
        if (punishment == null) return;
        if (punishment.isExpired()) {
            storageManager.unPunishPlayer(punishment);
            discordManager.updateRole(punishment, DiscordManager.DiscordAction.REMOVE);
            return;
        }
        // If message is command
        if (event.isCommand()) {
            List<String> bannedCommands = config.getStringList("banned-commands-while-mute");
            String command = event.getMessage().substring(1).split(" ")[0];
            if (bannedCommands.contains(command)) {
                event.setCancelled(true);
                player.sendMessage(Utils.getLayout(punishment));
            }
        } else {
            event.setCancelled(true);
            player.sendMessage(Utils.getLayout(punishment));
        }
    }
}
