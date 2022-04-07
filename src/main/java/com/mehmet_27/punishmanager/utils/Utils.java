package com.mehmet_27.punishmanager.utils;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.StorageManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Utils {

    public static final Pattern NumberAndUnit = Pattern.compile("(?<number>[0-9]+)(?<unit>mo|[ywdhms])");
    private static final PunishManager punishManager = PunishManager.getInstance();

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static List<String> color(List<String> stringList){
        return stringList.stream().map(Utils::color).collect(Collectors.toList());
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
        ProxiedPlayer player = punishManager.getProxy().getPlayer(punishment.getPlayerName());
        if (player == null || !player.isConnected()) return;
        String path = punishment.getPunishType().toString().toLowerCase(Locale.ENGLISH) + ".layout";
        TextComponent layout = TextComponentBuilder(punishManager.getConfigManager().getLayout(path, punishment.getPlayerName()), punishment);
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
        if (!punishManager.getConfigManager().getConfig().getBoolean("debug")) return;
        punishManager.getLogger().info(message);
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
        String message = punishManager.getConfigManager().getMessage(path, sender.getName());

        message = placeholders.apply(message);

        TextComponent textComponent = new TextComponent(message);
        sender.sendMessage(textComponent);
    }

    public static String getPlayerIp(UUID playerUuid) {
        ProxiedPlayer player = PunishManager.getInstance().getProxy().getPlayer(playerUuid);
        StorageManager storageManager = PunishManager.getInstance().getStorageManager();
        return player != null && player.isConnected() ? player.getSocketAddress().toString().substring(1).split(":")[0] : storageManager.getOfflinePlayer(playerUuid).getPlayerIp();
    }

    public static Locale stringToLocale(String loc){
        String[] localeStr = loc.split("_");
        return new Locale(localeStr[0], localeStr[1]);
    }

    public static boolean isPluginEnabled(String pluginName){
        Plugin plugin = punishManager.getProxy().getPluginManager().getPlugin(pluginName);
        return plugin != null;
    }

    public static String replacePunishmentPlaceholders(String message, Punishment punishment){
        return message.replace("%reason%", punishment.getReason())
                .replace("%operator%", punishment.getOperator())
                .replace("%player%", punishment.getPlayerName())
                .replace("%type%", punishment.getPunishType().name())
                .replace("%ip%", "" + punishment.getIp())
                .replace("%uuid%", punishment.getUuid().toString());
    }

    public static boolean isInteger(String value){
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }
}
