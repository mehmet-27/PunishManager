package dev.mehmet27.punishmanager.bungee.listeners;

import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.bungee.PMBungee;
import dev.mehmet27.punishmanager.configuration.Configuration;
import dev.mehmet27.punishmanager.managers.StorageManager;
import dev.mehmet27.punishmanager.objects.Punishment;
import dev.mehmet27.punishmanager.utils.Utils;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.List;

public class ChatListener implements Listener {

    private final Configuration config;
    private final PunishManager punishManager = PunishManager.getInstance();
    private final StorageManager storageManager = punishManager.getStorageManager();

    public ChatListener(PMBungee plugin) {
        config = plugin.getConfigManager().getConfig();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(ChatEvent event) {
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        Punishment punishment = storageManager.getMute(player.getUniqueId());
        if (punishment == null) return;
        if (punishment.isExpired()) {
            storageManager.unPunishPlayer(punishment);
            return;
        }
        // If message is command
        if (event.isCommand()) {
            List<String> bannedCommands = config.getStringList("banned-commands-while-mute");
            String command = event.getMessage().substring(1).split(" ")[0];
            if (bannedCommands.contains(command)) {
                event.setCancelled(true);
                player.sendMessage(TextComponent.fromLegacyText(Utils.getLayout(punishment)));
            }
        } else {
            event.setCancelled(true);
            player.sendMessage(TextComponent.fromLegacyText(Utils.getLayout(punishment)));
        }
    }
}
