package com.mehmet_27.punishmanager.managers;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.Punishment;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Locale;

public class DisconnectManager {
    private ConfigManager configManager;
    private MessageManager messageManager;

    public DisconnectManager() {
        this.messageManager = PunishManager.getInstance().getMessageManager();
        this.configManager = PunishManager.getInstance().getConfigManager();
    }

    // player, type, reason, operator
    public void DisconnectPlayer(Punishment punishment){
        String path = punishment.getPunishType().toString().toLowerCase(Locale.ENGLISH);
        TextComponent layout = messageManager.TextComponentBuilder(configManager.getLayout(path), punishment);
        ProxiedPlayer player = PunishManager.getInstance().getProxy().getPlayer(punishment.getPlayerName());
        if (player == null || !player.isConnected()) return;
        player.disconnect(layout);
    }
}
