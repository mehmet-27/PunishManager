package com.mehmet_27.punishmanager.utils;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.managers.MessageManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class Utils {

    private static final PunishManager plugin = PunishManager.getInstance();
    private final Configuration config = plugin.getConfigManager().getConfig();

    public static final Pattern NumberAndUnit = Pattern.compile("(?<number>[0-9]+)(?<unit>mo|[ywdhms])");

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static TextComponent TextComponentBuilder(List<String> messages, Punishment punishment) {
        TextComponent layout = new TextComponent();
        Punishment.PunishType punishType = punishment.getPunishType();
        for (String message : messages) {
            message = message.
                    replace("%reason%", punishment.getReason()).
                    replace("%operator%", punishment.getOperator()).
                    replace("%name%", punishment.getPlayerName());
            if (punishType.isTemp()) {
                message = message.replace("%duration%", punishment.getDuration());
            }
            layout.addExtra(message + "\n");
        }
        return layout;
    }

    public static void sendLayout(Punishment punishment) {
        ProxiedPlayer player = plugin.getProxy().getPlayer(punishment.getPlayerName());
        if (player == null || !player.isConnected()) return;
        String path = punishment.getPunishType().toString().toLowerCase(Locale.ENGLISH) + ".layout";
        MessageManager messageManager = PunishManager.getInstance().getMessageManager();
        TextComponent layout = TextComponentBuilder(messageManager.getLayout(path, punishment.getPlayerName()), punishment);
        if (punishment.isBanned()) {
            player.disconnect(layout);
        }
        if (punishment.isMuted()){
            player.sendMessage(layout);
        }
        if (punishment.getPunishType().equals(Punishment.PunishType.KICK)){
            player.disconnect(layout);
        }
    }

    public static long convertToMillis(int number, String unit) {
        long millis = number;
        switch (unit) {
            case "s":
                return millis * 1000;
            case "m":
                return millis * 1000 * 60;
            case "h":
                return millis * 1000 * 60 * 60;
            case "d":
                return millis * 1000 * 60 * 60 * 24;
            case "w":
                return millis * 1000 * 60 * 60 * 24 * 7;
            case "mo":
                return millis * 1000 * 60 * 60 * 24 * 28;
            case "y":
                return millis * 1000 * 60 * 60 * 24 * 28 * 12;
            default:
                return -1;
        }
    }
    public void debug(String message){
        if (!config.getBoolean("debug")) return;
        plugin.getLogger().info(message);
    }
}
