package dev.mehmet27.punishmanager.bukkit.listeners;

import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.bukkit.PMBukkit;
import dev.mehmet27.punishmanager.configuration.Configuration;
import dev.mehmet27.punishmanager.managers.StorageManager;
import dev.mehmet27.punishmanager.objects.Punishment;
import dev.mehmet27.punishmanager.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public class CommandListener implements Listener {

    private final Configuration config;
    private final PunishManager punishManager = PunishManager.getInstance();
    private final StorageManager storageManager = punishManager.getStorageManager();

    public CommandListener(PMBukkit plugin) {
        config = plugin.getConfigManager().getConfig();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        Punishment punishment = storageManager.getMute(player.getUniqueId());
        if (punishment == null) return;
        if (punishment.isExpired()) {
            storageManager.unPunishPlayer(punishment);
            return;
        }
        List<String> bannedCommands = config.getStringList("banned-commands-while-mute");
        String command = event.getMessage().substring(1).split(" ")[0];
        if (bannedCommands.contains(command)) {
            event.setCancelled(true);
            player.sendMessage(Utils.getLayout(punishment));
        }
    }
}
