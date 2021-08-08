package com.mehmet_27.punishmanager.managers;

import com.mehmet_27.punishmanager.PunishManager;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class ConfigManager {
    private final PunishManager plugin;

    private final ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
    private final Configuration config, messages;

    public ConfigManager(PunishManager plugin) {
        this.plugin = plugin;
        config = loadFile(new File(plugin.getDataFolder() + File.separator + "config.yml"));
        loadFolder(new File(plugin.getDataFolder() + File.separator + "locales"));
        messages = loadFile(new File(plugin.getDataFolder() + File.separator + "locales" + File.separator + "en.yml"));
        plugin.getLogger().info("Found " + getLocales().size() + " language files.");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private Configuration loadFile(File file) {
        try {
            if (!plugin.getDataFolder().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists() && !file.isDirectory()) {
                Files.copy(plugin.getResourceAsStream(file.getName()), file.toPath());
                return provider.load(file);
            }
            return provider.load(file);
        } catch (IOException ex) {
            plugin.getLogger().severe(String.format("Error while trying to load file {0}: " + ex.getMessage(), file.getName()));
        }
        return null;
    }
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void loadFolder(File file) {
        file.mkdirs();
    }

    public Map<String, Configuration> getLocales() {
        Map<String, Configuration> locales = new HashMap<>();
        for (File file : getLocaleFiles()) {
            locales.put(file.getName().split("\\.")[0], new Configuration(loadFile(file)));
        }
        return locales;
    }

    public Set<File> getLocaleFiles() {
        Set<File> files = new HashSet<>();
        File directoryPath = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "locales");
        FilenameFilter ymlFilter = (dir, name) -> {
            String lowercaseName = name.toLowerCase();
            return lowercaseName.endsWith(".yml");
        };
        File[] filesList = directoryPath.listFiles(ymlFilter);
        Objects.requireNonNull(filesList, "Locales folder not found!");
        Collections.addAll(files, filesList);
        return files;
    }

    public Configuration getConfig() {
        return config;
    }

    public Configuration getMessages() {
        return messages;
    }
}
