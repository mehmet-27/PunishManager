package com.mehmet_27.punishmanager.managers;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.PermissionManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    private static PunishManager plugin;
    private File configFile;
    private final ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
    private static Configuration config;

    public ConfigManager(PunishManager plugin) {
        this.plugin = plugin;
        configFile = new File(plugin.getDataFolder(), "config.yml");
    }

    public void load() {
        try {
            if (!plugin.getDataFolder().exists())
                plugin.getDataFolder().mkdirs();
            if (!configFile.exists()) {
                final InputStream im = plugin.getResourceAsStream("config.yml");
                Files.copy(im, configFile.toPath());
            }
            config = provider.load(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getLayout(String path) {
        List<String> messages = new ArrayList<>();
        for (String message : getConfig().getStringList(path)) {
            messages.add(ChatColor.translateAlternateColorCodes('&', message));
        }
        plugin.getProxy().getLogger().info(messages.toString());
        return messages;
    }

    public static Configuration getConfig() {
        return config;
    }
}
