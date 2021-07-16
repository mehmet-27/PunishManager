package com.mehmet_27.punishmanager.managers;

import com.mehmet_27.punishmanager.PunishManager;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class KickManager {
    private String reason;
    private String operator;
    private TextComponent layout;

    private PunishManager punishManager;
    private ConfigManager configManager;
    private MessagesManager messagesManager;

    public void setReason(String reason){
        this.reason = reason;
    }
    public void setOperator(String operator){
        this.operator = operator;
    }
    public void setLayout(TextComponent layout){
        this.layout = layout;
    }

    public void KickPlayer(ProxiedPlayer player, String type, String reason){
        TextComponent layout = messagesManager.TextComponentBuilder(configManager.getLayout("kick"), type.toLowerCase(), reason);
        PunishManager.getInstance().getProxy().getLogger().info(layout.toString());
        player.disconnect(layout);
    }
    public void KickPlayer(ProxiedPlayer player){
        TextComponent layout = new TextComponent(new ComponentBuilder("TITTLE").append("You kicked the server.").append("Reason: none").create());
        setLayout(layout);
        player.disconnect(layout);
    }
}
