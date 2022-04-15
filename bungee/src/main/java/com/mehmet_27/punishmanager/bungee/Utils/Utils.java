package com.mehmet_27.punishmanager.bungee.Utils;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.bungee.PMBungee;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.utils.UtilsCore;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

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
        Plugin plugin = PMBungee.getInstance().getProxy().getPluginManager().getPlugin(pluginName);
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

    public static TextComponent TextComponentBuilder(List<String> messages, Punishment punishment) {
        TextComponent layout = new TextComponent();
        for (String message : messages) {
            message = message.replace("%prefix%", PunishManager.getInstance().getConfigManager().getMessage("main.prefix", punishment.getPlayerName()));
            // Replace general punishment placeholders
            message = UtilsCore.replacePunishmentPlaceholders(message, punishment);
            layout.addExtra(message + "\n");
        }
        return layout;
    }

    public static TextComponent getLayout(Punishment punishment) {
        String path = punishment.getPunishType().toString().toLowerCase(Locale.ENGLISH) + ".layout";
        return TextComponentBuilder(PunishManager.getInstance().getConfigManager().getStringList(path, punishment.getPlayerName()), punishment);
    }
}
