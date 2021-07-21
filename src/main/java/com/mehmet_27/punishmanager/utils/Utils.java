package com.mehmet_27.punishmanager.utils;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.Punishment;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;

public class Utils {

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static TextComponent TextComponentBuilder(List<String> messages, Punishment punishment) {
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

    public static void disconnectPlayer(Punishment punishment) {
        PunishManager plugin = PunishManager.getInstance();
        String path = punishment.getPunishType().toString().toLowerCase();
        TextComponent layout = TextComponentBuilder(plugin.getConfigManager().getLayout(path), punishment);
        ProxiedPlayer player = plugin.getProxy().getPlayer(punishment.getPlayerName());

        if (player == null || !player.isConnected()) return;
        player.disconnect(layout);
    }
}
