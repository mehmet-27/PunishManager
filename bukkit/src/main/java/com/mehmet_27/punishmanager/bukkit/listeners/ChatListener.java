package com.mehmet_27.punishmanager.bukkit.listeners;

import com.mehmet_27.punishmanager.ConfigurationAdapter;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.bukkit.PMBukkit;
import com.mehmet_27.punishmanager.bukkit.utils.Utils;
import com.mehmet_27.punishmanager.managers.DiscordManager;
import com.mehmet_27.punishmanager.managers.StorageManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

public class ChatListener implements Listener {

    private final ConfigurationAdapter config;
    private final PunishManager punishManager = PunishManager.getInstance();
    private final StorageManager storageManager = punishManager.getStorageManager();
    private final DiscordManager discordManager = punishManager.getDiscordManager();

    public ChatListener(PMBukkit plugin) {
        config = plugin.getConfigManager().getConfig();
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Punishment punishment = storageManager.getMute(player.getUniqueId());
        if (punishment == null) return;
        if (punishment.isExpired()) {
            storageManager.unPunishPlayer(punishment);
            discordManager.updateRole(punishment, DiscordManager.DiscordAction.REMOVE);
            return;
        }
        // If message is command
        if (event.getMessage().length() > 0 && event.getMessage().charAt( 0 ) == '/') {
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
