package com.mehmet_27.punishmanager.bungee.Utils;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.bungee.PMBungee;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public class BungeeUtils {
    public static boolean isPluginEnabled(String pluginName) {
        Plugin plugin = PMBungee.getInstance().getProxy().getPluginManager().getPlugin(pluginName);
        return plugin != null;
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
            message = Utils.replacePunishmentPlaceholders(message, punishment);
            layout.addExtra(message + "\n");
        }
        return layout;
    }

    public static TextComponent getLayout(Punishment punishment) {
        String path = punishment.getPunishType().toString().toLowerCase(Locale.ENGLISH) + ".layout";
        return TextComponentBuilder(PunishManager.getInstance().getConfigManager().getStringList(path, punishment.getPlayerName()), punishment);
    }
}
