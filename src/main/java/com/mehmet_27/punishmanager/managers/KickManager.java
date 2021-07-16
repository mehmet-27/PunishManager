package com.mehmet_27.punishmanager.managers;

import com.mehmet_27.punishmanager.PunishManager;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class KickManager {
    private String reason;
    private String operator;
    private TextComponent layout;

    private ConfigManager configManager;
    private MessagesManager messagesManager;

    public KickManager() {
        this.messagesManager = PunishManager.getInstance().getMessagesManager();
        this.configManager = PunishManager.getInstance().getConfigManager();
    }

    public void setReason(String reason){
        this.reason = reason;
    }
    public void setOperator(String operator){
        this.operator = operator;
    }
    public void setLayout(TextComponent layout){
        this.layout = layout;
    }

    // player, type, reason, operator
    public void DisconnectPlayer(ProxiedPlayer player, String type, String reason, String operator){
        TextComponent layout = messagesManager.TextComponentBuilder(configManager.getLayout(type), type.toLowerCase(), reason, operator);
        PunishManager.getInstance().getProxy().getLogger().info(layout.toString());
        player.disconnect(layout);
    }
    public void DisconnectPlayer(ProxiedPlayer player){
        TextComponent layout = new TextComponent(new ComponentBuilder("TITTLE").append("You kicked the server.").append("Reason: none").create());
        setLayout(layout);
        player.disconnect(layout);
    }
}
