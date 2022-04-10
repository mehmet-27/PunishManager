package com.mehmet_27.punishmanager.managers;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.objects.PlayerLocale;
import com.mehmet_27.punishmanager.utils.FileUtils;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.mehmet_27.punishmanager.utils.FileUtils.copyFileFromResources;

public class ConfigManager {
    private final PunishManager plugin;
    private final ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
    private Configuration config;
    private Map<Locale, Configuration> locales;
    private Map<String, String> embeds;
    private java.util.Locale defaultLocale;
    private List<String> exemptPlayers;

    public ConfigManager(PunishManager plugin) {
        this.plugin = plugin;
        setup();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private Configuration loadConfigFile(File file) {
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
            plugin.getLogger().severe(String.format("Error while trying to load config file {0}: " + ex.getMessage(), file.getName()));
        }
        return null;
    }

    public void saveConfiguration(Configuration config, File file) {
        try {
            provider.save(config, file);
        } catch (IOException e) {
            plugin.getLogger().severe("An error occurred while saving the configuration: ");
            e.printStackTrace();
        }
    }

    public Map<Locale, Configuration> getLocales() {
        Map<Locale, Configuration> locales = new HashMap<>();
        for (File file : getLocaleFiles()) {
            Locale locale = Utils.stringToLocale(file.getName().split("\\.")[0]);
            locales.put(locale, new Configuration(loadConfigFile(file)));
        }
        plugin.getLogger().info("Found " + locales.size() + " language files.");
        return locales;
    }

    public List<File> getLocaleFiles() {
        List<File> files = new ArrayList<>();
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

    public List<Locale> getAvailableLocales() {
        List<Locale> locales = new ArrayList<>();
        for (Map.Entry<Locale, Configuration> locale : this.locales.entrySet()) {
            locales.add(locale.getKey());
        }
        return locales;
    }

    public String getMessage(String path) {
        if (locales.containsKey(defaultLocale)) {
            String msg = locales.get(defaultLocale).getString(path);
            if (msg != null && msg.length() != 0) {
                return Utils.color(msg);
            }
        }
        plugin.getLogger().warning("The searched value was not found in the language file and the default language file: " + path);
        return "";
    }

    public String getMessage(String path, String playerName) {
        Locale locale = new PlayerLocale(playerName).getLocale();
        String message;
        String prefix = "";
        if (locales.containsKey(locale)) {
            message = locales.get(locale).getString(path);
            if (message == null || message.isEmpty()){
                message = locales.get(defaultLocale).getString(path);
            }
            prefix = locales.get(locale).getString("main.prefix", locales.get(defaultLocale).getString("main.prefix"));
        } else {
            message = getMessage(path);
        }
        return Utils.color(message.replace("%prefix%", prefix));
    }

    public String getMessage(String path, String playerName, Function<String, String> placeholders){
        String message = getMessage(path, playerName);
        return Utils.color(placeholders.apply(message));
    }

    public List<String> getStringList(String path) {
        if (locales.containsKey(defaultLocale)) {
            List<String> stringList = locales.get(defaultLocale).getStringList(path);
            if (!stringList.isEmpty()) {
                return stringList.stream().map(Utils::color).collect(Collectors.toList());
            }
        }
        plugin.getLogger().warning("The searched value was not found in the language file and the default language file: " + path);
        return new ArrayList<>();
    }

    public List<String> getStringList(String path, String playerName) {
        Locale locale = new PlayerLocale(playerName).getLocale();
        List<String> stringList;
        if (locales.containsKey(locale)) {
            stringList = locales.get(locale).getStringList(path);
        } else {
            stringList = getStringList(path);
        }
        return stringList.stream().map(Utils::color).collect(Collectors.toList());
    }

    public Configuration getConfig() {
        return config;
    }

    private Set<File> getEmbedFiles() {
        Set<File> files = new HashSet<>();
        File directoryPath = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "embeds");
        FilenameFilter jsonFilter = (dir, name) -> {
            String lowercaseName = name.toLowerCase();
            return lowercaseName.endsWith(".json");
        };
        File[] filesList = directoryPath.listFiles(jsonFilter);
        Objects.requireNonNull(filesList, "Embeds folder not found!");
        Collections.addAll(files, filesList);
        return files;
    }

    private Map<String, String> getEmbeds() {
        Map<String, String> embeds = new HashMap<>();
        for (File file : getEmbedFiles()) {
            try {
                embeds.put(file.getName().split("\\.")[0].toUpperCase(java.util.Locale.ENGLISH), fileToString(file, "UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return embeds;
    }

    public String fileToString(File file, String charset) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int result = bis.read();
        while (result != -1) {
            buf.write((byte) result);
            result = bis.read();
        }
        return buf.toString(charset);
    }

    public String getEmbed(String path) {
        return embeds.get(path);
    }

    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    public List<String> getExemptPlayers() {
        return exemptPlayers;
    }

    public void setDefaultLocale(Locale locale) {
        defaultLocale = locale;
        config.set("default-server-language", locale.toString());
        saveConfiguration(config, new File(plugin.getDataFolder() + File.separator + "config.yml"));
        config = loadConfigFile(new File(plugin.getDataFolder() + File.separator + "config.yml"));
    }

    public void setup() {
        config = loadConfigFile(new File(plugin.getDataFolder() + File.separator + "config.yml"));
        String pluginFolder = plugin.getDataFolder() + File.separator;
        FileUtils.createDirectoriesIfNotExists(Paths.get(pluginFolder + "locales"));
        FileUtils.createDirectoriesIfNotExists(Paths.get(pluginFolder + "embeds"));
        copyFileFromResources(plugin, new File(pluginFolder + "embeds" + File.separator + "ban.json"));
        copyFileFromResources(plugin, new File(pluginFolder + "embeds" + File.separator + "mute.json"));
        copyFileFromResources(plugin, new File(pluginFolder + "embeds" + File.separator + "tempban.json"));
        copyFileFromResources(plugin, new File(pluginFolder + "embeds" + File.separator + "tempmute.json"));
        copyFileFromResources(plugin, new File(pluginFolder + "embeds" + File.separator + "ipban.json"));
        copyFileFromResources(plugin, new File(pluginFolder + "embeds" + File.separator + "kick.json"));
        copyFileFromResources(plugin, new File(pluginFolder + "locales" + File.separator + "en_US.yml"));
        copyFileFromResources(plugin, new File(pluginFolder + "locales" + File.separator + "tr_TR.yml"));
        this.locales = getLocales();
        this.embeds = getEmbeds();
        defaultLocale = Utils.stringToLocale(getConfig().getString("default-server-language"));
        this.exemptPlayers = getConfig().getStringList("exempt-players");
    }
}
