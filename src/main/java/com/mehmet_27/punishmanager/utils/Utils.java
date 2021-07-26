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

import static com.mehmet_27.punishmanager.Punishment.PunishType.*;

public class Utils {

    public static final Pattern NumberAndUnit = Pattern.compile("(?<number>[0-9]+)(?<unit>mo|[ywdhms])");

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static TextComponent TextComponentBuilder(List<String> messages, Punishment punishment) {
        TextComponent layout = new TextComponent();
        Punishment.PunishType punishType = punishment.getPunishType();
        for (String message : messages) {
            if (punishType.equals(BAN)) {
                layout.addExtra(String.format("%s %s", punishment.getReason(), punishment.getOperator()).
                        replaceAll("%reason%", punishment.getReason()).
                        replaceAll("%operator%", punishment.getOperator())
                );
            } else if (punishType.equals(TEMPBAN)) {
                layout.addExtra(String.format("%s %s %s", punishment.getReason(), punishment.getOperator(), punishment.getDuration()).
                        replaceAll("%reason%", punishment.getReason()).
                        replaceAll("%operator%", punishment.getOperator()).
                        replaceAll("%duration%", punishment.getDuration())
                );
                layout.addExtra(message.replace("%reason%", punishment.getReason()).replace("%operator%", punishment.getOperator()).replace("%duration%", punishment.getDuration()) + "\n");
            } else if (punishType.equals(KICK)) {
                layout.addExtra(String.format("%s %s", punishment.getReason(), punishment.getOperator()).
                        replaceAll("%reason%", punishment.getReason()).
                        replaceAll("%operator%", punishment.getOperator())
                );
            }
        }
        return layout;
    }

    public static void disconnectPlayer(Punishment punishment) {
        PunishManager plugin = PunishManager.getInstance();
        String path = punishment.getPunishType().toString().toLowerCase(Locale.ENGLISH);
        TextComponent layout = TextComponentBuilder(plugin.getConfigManager().getLayout(path), punishment);
        ProxiedPlayer player = plugin.getProxy().getPlayer(punishment.getPlayerName());

        if (player == null || !player.isConnected()) return;
        player.disconnect(layout);
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
