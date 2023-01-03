package dev.mehmet27.punishmanager.bukkit.utils;

import dev.mehmet27.punishmanager.bukkit.PMBukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitUtils {

    public static boolean isPluginEnabled(String pluginName) {
        JavaPlugin plugin = (JavaPlugin) PMBukkit.getInstance().getServer().getPluginManager().getPlugin(pluginName);
        return plugin != null;
    }
}
