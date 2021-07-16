package com.mehmet_27.punishmanager.managers;

import com.mehmet_27.punishmanager.Punishment;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;
import java.util.Locale;

public class MessagesManager {
    public TextComponent TextComponentBuilder(List<String> messages, String type, String reason) {
        TextComponent layout = new TextComponent();
        Punishment punishment;
        for (int i = 0; i < messages.size(); i++) {
            if (type.toLowerCase().equals("ban")) {
                layout.addExtra(messages.get(i) + "\n");
            } else if (type.toLowerCase().equals("kick")) {
                punishment = new Punishment(Punishment.PunishType.KICK, reason);
                layout.addExtra(messages.get(i).replace("%reason%", Punishment.getReason()) + "\n");
            }
        }
        return layout;
    }
}
