package com.mehmet_27.punishmanager.velocity.listeners;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.StorageManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.utils.Utils;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;

public class ChatListener {

    private final PunishManager punishManager = PunishManager.getInstance();
    private final StorageManager storageManager = punishManager.getStorageManager();

    @Subscribe(order = PostOrder.FIRST)
    public void onPlayerChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        Punishment punishment = storageManager.getMute(player.getUniqueId());
        if (punishment == null) return;
        if (punishment.isExpired()) {
            storageManager.unPunishPlayer(punishment);
            return;
        }
        event.setResult(PlayerChatEvent.ChatResult.denied());
        player.sendMessage(Component.text(Utils.getLayout(punishment)));
    }
}
