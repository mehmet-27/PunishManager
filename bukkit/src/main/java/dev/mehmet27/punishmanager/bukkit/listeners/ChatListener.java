package dev.mehmet27.punishmanager.bukkit.listeners;

import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.managers.StorageManager;
import dev.mehmet27.punishmanager.objects.Punishment;
import dev.mehmet27.punishmanager.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private final PunishManager punishManager = PunishManager.getInstance();
    private final StorageManager storageManager = punishManager.getStorageManager();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Punishment punishment = storageManager.getMute(player.getUniqueId());
        if (punishment == null) return;
        if (punishment.isExpired()) {
            storageManager.unPunishPlayer(punishment);
            return;
        }
        event.setCancelled(true);
        player.sendMessage(Utils.getLayout(punishment));
    }
}
