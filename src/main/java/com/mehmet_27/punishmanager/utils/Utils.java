package com.mehmet_27.punishmanager.utils;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.Punishment;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static PunishManager plugin = PunishManager.getInstance();

    public static final Pattern NumberAndUnit = Pattern.compile("(?<number>[0-9]+)(?<unit>mo|[ywdhms])");

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static TextComponent colorComponent(String message) {
        return new TextComponent(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static TextComponent TextComponentBuilder(List<String> messages, Punishment punishment) {
        TextComponent layout = new TextComponent();
        Punishment.PunishType punishType = punishment.getPunishType();
        for (String message : messages) {
            message = message.
                    replace("%reason%", punishment.getReason()).
                    replace("%operator%", punishment.getOperator());
            if (punishType.isTemp()) {
                message = message.replace("%duration%", punishment.getDuration());
            }
            layout.addExtra(message + "\n");
        }
        return layout;
    }

    public static void disconnectPlayer(Punishment punishment) {
        ProxiedPlayer player = plugin.getProxy().getPlayer(punishment.getPlayerName());
        if (player == null || !player.isConnected()) return;
        String path = punishment.getPunishType().toString().toLowerCase(Locale.ENGLISH) + ".layout";
        TextComponent layout = TextComponentBuilder(plugin.getMessageManager().getLayout(path), punishment);
        player.disconnect(layout);
    }

    public static void sendMuteMessage(Punishment punishment) {
        ProxiedPlayer player = plugin.getProxy().getPlayer(punishment.getPlayerName());
        if (player == null || !player.isConnected()) return;
        String path = punishment.getPunishType().toString().toLowerCase(Locale.ENGLISH);
        TextComponent layout = TextComponentBuilder(plugin.getMessageManager().getLayout(path), punishment);
        player.sendMessage(layout);
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

    public static boolean isMatcherFound(String time) {
        Matcher matcher = NumberAndUnit.matcher(time.toLowerCase());
        return matcher.find();
    }
}
