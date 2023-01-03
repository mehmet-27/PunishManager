package dev.mehmet27.punishmanager.velocity.utils;

import com.velocitypowered.api.plugin.PluginContainer;
import dev.mehmet27.punishmanager.velocity.PMVelocity;

import java.util.Optional;

public class VelocityUtils {
    public static boolean isPluginEnabled(String pluginId) {
        Optional<PluginContainer> plugin = PMVelocity.getInstance().getServer().getPluginManager().getPlugin(pluginId);
        return plugin.isPresent();
    }
}
