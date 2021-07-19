package com.mehmet_27.punishmanager.managers;

import com.mehmet_27.punishmanager.Punishment;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;


public class MessageManager {
    public TextComponent TextComponentBuilder(List<String> messages, Punishment punishment) {
        TextComponent layout = new TextComponent();
        Punishment.PunishType punishType = punishment.getPunishType();
        for (String message : messages) {
            if (punishType.equals(Punishment.PunishType.BAN)) {
                layout.addExtra(message.replace("%reason%", punishment.getReason()).replace("%operator%", punishment.getOperator()) + "\n");
            } else if (punishType.equals(Punishment.PunishType.KICK)) {
                layout.addExtra(message.replace("%reason%", punishment.getReason()).replace("%operator%", punishment.getOperator()) + "\n");
            }
        }
        return layout;
    }
}
