package com.mehmet_27.punishmanager.managers;

import co.aikar.commands.annotation.Optional;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.objects.Language;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class ConfigManager {
    private final PunishManager plugin;
    private final ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
    private Configuration config, messages;
    private Map<String, Configuration> locales;
    private String defaultLanguage;

    public ConfigManager(PunishManager plugin) {
        this.plugin = plugin;
        setup();
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

    public List<String> getLayout(String path, String playerName) {
        String language = new Language(playerName).getLanguage();
        if (locales.containsKey(language)) {
            List<String> messages = locales.get(language).getStringList(path);
            if (messages.size() != 0) {
                return locales.get(language).getStringList(path).stream().map(Utils::color).collect(Collectors.toList());
            } else {
                return locales.get(defaultLanguage).getStringList(path).stream().map(Utils::color).collect(Collectors.toList());
            }
        } else {
            return locales.get(defaultLanguage).getStringList(path).stream().map(Utils::color).collect(Collectors.toList());
        }
    }

    public String getMessage(String path, @Optional String playerName) {
        String language = new Language(playerName).getLanguage();
        if (locales.containsKey(language)) {
            String msg = locales.get(language).getString(path);
            if (msg != null && msg.length() != 0) {
                return Utils.color(locales.get(language).getString(path));
            } else {
                return Utils.color(locales.get(defaultLanguage).getString(path));
            }
        } else {
            return Utils.color(locales.get(defaultLanguage).getString(path));
        }
    }
    public String getMessage(String path) {
        if (locales.containsKey(defaultLanguage)) {
            String msg = locales.get(defaultLanguage).getString(path);
            if (msg != null && msg.length() != 0) {
                return Utils.color(locales.get(defaultLanguage).getString(path));
            }
        }
        return null;
    }
    public Configuration getConfig() {
        return config;
    }

    public Configuration getMessages() {
        return messages;
    }

    public void setup() {
        config = loadFile(new File(plugin.getDataFolder() + File.separator + "config.yml"));
        loadFolder(new File(plugin.getDataFolder() + File.separator + "locales"));
        messages = loadFile(new File(plugin.getDataFolder() + File.separator + "locales" + File.separator + "en.yml"));
        loadFile(new File(plugin.getDataFolder() + File.separator + "locales" + File.separator + "tr.yml"));
        plugin.getLogger().info("Found " + getLocales().size() + " language files.");
        this.locales = getLocales();
        defaultLanguage = getConfig().getString("default-server-language");
    }
    public String getDefaultLanguage(){
        return defaultLanguage;
    }
}
