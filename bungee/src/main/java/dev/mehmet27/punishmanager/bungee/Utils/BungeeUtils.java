package dev.mehmet27.punishmanager.bungee.Utils;

import dev.mehmet27.punishmanager.bungee.PMBungee;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeUtils {
    public static boolean isPluginEnabled(String pluginName) {
        Plugin plugin = PMBungee.getInstance().getProxy().getPluginManager().getPlugin(pluginName);
        return plugin != null;
    }
}
