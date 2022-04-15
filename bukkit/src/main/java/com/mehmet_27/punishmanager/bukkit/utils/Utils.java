package com.mehmet_27.punishmanager.bukkit.utils;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.bukkit.PMBukkit;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.utils.UtilsCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public class Utils {
    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static Locale stringToLocale(String loc){
        String[] localeStr = loc.split("_");
        return new Locale(localeStr[0], localeStr[1]);
    }

    public static boolean isPluginEnabled(String pluginName){
        JavaPlugin plugin = (JavaPlugin) PMBukkit.getInstance().getServer().getPluginManager().getPlugin(pluginName);
        return plugin != null;
    }

    public static boolean isInteger(String value){
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }

    public static void sendText(CommandSender sender, String path) {
        sendText(sender, path, message -> message.replace("%player%", sender.getName()));
    }

    public static void sendText(CommandSender sender, String playerName, String path) {
        sendText(sender, path, message -> message.replace("%player%", playerName));
    }

    public static void sendText(CommandSender sender, String path, Function<String, String> placeholders) {
        String message = PunishManager.getInstance().getConfigManager().getMessage(path, sender.getName());

        message = placeholders.apply(message);

        PunishManager.getInstance().getMethods().sendMessage(sender, message);
    }

    public static String TextComponentBuilder(List<String> messages, Punishment punishment) {
        String layout = "";
        for (String message : messages) {
            message = message.replace("%prefix%", PunishManager.getInstance().getConfigManager().getMessage("main.prefix", punishment.getPlayerName()));
            // Replace general punishment placeholders
            message = UtilsCore.replacePunishmentPlaceholders(message, punishment);
            layout = layout.concat(message + "\n");
        }
        return layout;
    }

    public static String getLayout(Punishment punishment) {
        String path = punishment.getPunishType().toString().toLowerCase(Locale.ENGLISH) + ".layout";
        return TextComponentBuilder(PunishManager.getInstance().getConfigManager().getStringList(path, punishment.getPlayerName()), punishment);
    }
}
