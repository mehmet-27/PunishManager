package com.mehmet_27.punishmanager.managers;

import com.mehmet_27.punishmanager.Punishment;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;


public class MessageManager {
    public TextComponent TextComponentBuilder(List<String> messages, String type, String reason, String operator) {
        TextComponent layout = new TextComponent();
        Punishment punishment;
        for (String message : messages) {
            if (type.equalsIgnoreCase("ban")) {
                punishment = new Punishment(Punishment.PunishType.BAN, reason, operator);
                layout.addExtra(message.replace("%reason%", punishment.getReason()).replace("%operator%", punishment.getOperator()) + "\n");
            } else if (type.equalsIgnoreCase("kick")) {
                punishment = new Punishment(Punishment.PunishType.KICK, reason, operator);
                layout.addExtra(message.replace("%reason%", punishment.getReason()).replace("%operator%", punishment.getOperator()) + "\n");
            }
        }
        return layout;
    }
}
