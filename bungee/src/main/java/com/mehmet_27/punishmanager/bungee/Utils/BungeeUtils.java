package com.mehmet_27.punishmanager.bungee.Utils;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.bungee.PMBungee;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.List;
import java.util.Locale;

public class BungeeUtils {
    public static boolean isPluginEnabled(String pluginName) {
        Plugin plugin = PMBungee.getInstance().getProxy().getPluginManager().getPlugin(pluginName);
        return plugin != null;
    }
}
