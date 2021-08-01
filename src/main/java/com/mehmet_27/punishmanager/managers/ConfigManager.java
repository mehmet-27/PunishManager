package com.mehmet_27.punishmanager.managers;

import com.mehmet_27.punishmanager.PunishManager;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ConfigManager {
    private final PunishManager plugin;

    private final ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
    private final Configuration config, messages;

    public ConfigManager(PunishManager plugin) {
        this.plugin = plugin;
        config = loadFile(new File(plugin.getDataFolder() + File.separator + "config.yml"));
        messages = loadFile(new File(plugin.getDataFolder() + File.separator + "messages.yml"));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private Configuration loadFile(File file) {
        try {
            if (!plugin.getDataFolder().exists()) {
                file.getParentFile().mkdirs();
            }

            if (!file.exists()) {
                Files.copy(plugin.getResourceAsStream(file.getName()), file.toPath());
                return provider.load(file);
            }

            return provider.load(file);
        } catch (IOException ex) {
            plugin.getLogger().severe(String.format("Error while trying to load file {0}: " + ex.getMessage(), file.getName()));
        }

        return null;
    }


    public Configuration getConfig() {
        return config;
    }

    public Configuration getMessages() {
        return messages;
    }
}
