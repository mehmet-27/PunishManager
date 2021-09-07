package com.mehmet_27.punishmanager.utils;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.managers.DatabaseManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.regex.Pattern;

public class Utils {

    public static final Pattern NumberAndUnit = Pattern.compile("(?<number>[0-9]+)(?<unit>mo|[ywdhms])");
    private static final PunishManager plugin = PunishManager.getInstance();
    private static final ConfigManager configManager = plugin.getConfigManager();
    private static final Configuration config = configManager.getConfig();

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
        TextComponent layout = TextComponentBuilder(configManager.getLayout(path, punishment.getPlayerName()), punishment);
        if (punishment.isBanned() || punishment.getPunishType().equals(Punishment.PunishType.KICK)) {
            player.disconnect(layout);
        }
        if (punishment.isMuted()) {
            player.sendMessage(layout);
        }
    }

    public static long convertToMillis(int number, String unit) {
        switch (unit) {
            case "s":
                return (long) number * 1000;
            case "m":
                return (long) number * 1000 * 60;
            case "h":
                return (long) number * 1000 * 60 * 60;
            case "d":
                return (long) number * 1000 * 60 * 60 * 24;
            case "w":
                return (long) number * 1000 * 60 * 60 * 24 * 7;
            case "mo":
                return (long) number * 1000 * 60 * 60 * 24 * 28;
            case "y":
                return (long) number * 1000 * 60 * 60 * 24 * 28 * 12;
            default:
                return -1;
        }
    }

    public static void debug(String message) {
        if (!config.getBoolean("debug")) return;
        plugin.getLogger().info(message);
    }

    public static void sendColoredTextComponent(CommandSender sender, String message){
        sender.sendMessage(new TextComponent(Utils.color(message)));
    }
    public static void sendTextComponent(CommandSender sender, String path) {
        sendTextComponent(sender, path, message -> message.replace("%player%", sender.getName()));
    }

    public static void sendTextComponent(CommandSender sender, String playerName, String path) {
        sendTextComponent(sender, path, message -> message.replace("%player%", playerName));
    }

    public static void sendTextComponent(CommandSender sender, String path, Function<String, String> placeholders) {
        String message = configManager.getMessage(path, sender.getName());

        message = placeholders.apply(message);

        TextComponent textComponent = new TextComponent(message);
        sender.sendMessage(textComponent);
    }

    public static String getPlayerIp(String playerName) {
        ProxiedPlayer player = PunishManager.getInstance().getProxy().getPlayer(playerName);
        DatabaseManager dataBaseManager = PunishManager.getInstance().getDataBaseManager();
        return player != null && player.isConnected() ? player.getSocketAddress().toString().substring(1).split(":")[0] : dataBaseManager.getOfflinePlayer(playerName).getPlayerIp();
    }
}
