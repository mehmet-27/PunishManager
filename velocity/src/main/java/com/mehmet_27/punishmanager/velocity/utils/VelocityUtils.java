package com.mehmet_27.punishmanager.velocity.utils;

import com.mehmet_27.punishmanager.velocity.PMVelocity;
import com.velocitypowered.api.plugin.PluginContainer;

import java.util.Optional;

public class VelocityUtils {
    public static boolean isPluginEnabled(String pluginId) {
        Optional<PluginContainer> plugin = PMVelocity.getInstance().getServer().getPluginManager().getPlugin(pluginId);
        return plugin.isPresent();
    }
}
