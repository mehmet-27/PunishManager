package com.mehmet_27.punishmanager.bukkit.managers;

import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.ConfigurationAdapter;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.bukkit.BukkitConfiguration;
import com.mehmet_27.punishmanager.bukkit.PMBukkit;
import com.mehmet_27.punishmanager.bukkit.utils.Utils;
import com.mehmet_27.punishmanager.objects.PlayerLocale;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BukkitConfigManager implements ConfigManager {

    private final PMBukkit plugin;
    private BukkitConfiguration config;
    private final Map<Locale, BukkitConfiguration> locales = new HashMap<>();
    private Map<String, String> embeds;
    private java.util.Locale defaultLocale;
    private List<String> exemptPlayers;

    public BukkitConfigManager(PMBukkit plugin){
        this.plugin = plugin;
    }

    @Override
    public ConfigurationAdapter getConfig() {
        return config;
    }

    @Override
    public Map<Locale, Object> getLocales() {
        Map<Locale, Object> locales = new HashMap<>();
        for (File file : getLocaleFiles()) {
            Locale locale = Utils.stringToLocale(file.getName().split("\\.")[0]);
            locales.put(locale, new BukkitConfiguration(file));
        }
        plugin.getLogger().info("Found " + locales.size() + " language files.");
        return locales;
    }

    @Override
    public List<Locale> getAvailableLocales() {
        List<Locale> locales = new ArrayList<>();
        for (Map.Entry<Locale, BukkitConfiguration> locale : this.locales.entrySet()) {
            locales.add(locale.getKey());
        }
        return locales;
    }

    @Override
    public List<File> getLocaleFiles() {
        List<File> files = new ArrayList<>();
        File directoryPath = new File(plugin.getDataFolder() + File.separator + "locales");
        FilenameFilter ymlFilter = (dir, name) -> {
            String lowercaseName = name.toLowerCase();
            return lowercaseName.endsWith(".yml");
        };
        File[] filesList = directoryPath.listFiles(ymlFilter);
        Objects.requireNonNull(filesList, "Locales folder not found!");
        Collections.addAll(files, filesList);
        return files;
    }

    @Override
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

    @Override
    public String getMessage(String path, String playerName) {
        Locale locale = new PlayerLocale(playerName).getLocale();
        String message;
        String prefix = "";
        if (locales.containsKey(locale)) {
            message = locales.get(locale).getString(path);
            if (message == null || message.isEmpty()) {
                message = locales.get(defaultLocale).getString(path);
            }
            prefix = locales.get(locale).getString("main.prefix", locales.get(defaultLocale).getString("main.prefix"));
        } else {
            message = getMessage(path);
        }
        return Utils.color(message.replace("%prefix%", prefix));
    }

    @Override
    public String getMessage(String path, String playerName, Function<String, String> placeholders) {
        String message = getMessage(path, playerName);
        return Utils.color(placeholders.apply(message));
    }

    @Override
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

    @Override
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

    @Override
    public Set<File> getEmbedFiles() {
        Set<File> files = new HashSet<>();
        File directoryPath = new File(plugin.getDataFolder() + File.separator + "embeds");
        FilenameFilter jsonFilter = (dir, name) -> {
            String lowercaseName = name.toLowerCase();
            return lowercaseName.endsWith(".json");
        };
        File[] filesList = directoryPath.listFiles(jsonFilter);
        Objects.requireNonNull(filesList, "Embeds folder not found!");
        Collections.addAll(files, filesList);
        return files;
    }

    @Override
    public Map<String, String> getEmbeds() {
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

    @Override
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

    @Override
    public String getEmbed(String path) {
        return embeds.get(path);
    }

    @Override
    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    @Override
    public List<String> getExemptPlayers() {
        return exemptPlayers;
    }

    @Override
    public void setup() {
        Path dataFolder = plugin.getDataFolder().toPath();
        copyFileFromResources(new File(dataFolder + File.separator  + "config.yml"));
        config = new BukkitConfiguration(new File(dataFolder + File.separator  + "config.yml"));
        copyFileFromResources(new File(dataFolder + File.separator  + "locales" + File.separator + "en_US.yml"));
        copyFileFromResources(new File(dataFolder + File.separator  + "locales" + File.separator + "tr_TR.yml"));
        copyFileFromResources(new File(dataFolder + File.separator  + "locales" + File.separator + "es_ES.yml"));
        copyFileFromResources(new File(dataFolder + File.separator  + "embeds" + File.separator + "ban.json"));
        copyFileFromResources(new File(dataFolder + File.separator  + "embeds" + File.separator + "ipban.json"));
        copyFileFromResources(new File(dataFolder + File.separator  + "embeds" + File.separator + "mute.json"));
        copyFileFromResources(new File(dataFolder + File.separator  + "embeds" + File.separator + "kick.json"));
        copyFileFromResources(new File(dataFolder + File.separator  + "embeds" + File.separator + "tempban.json"));
        copyFileFromResources(new File(dataFolder + File.separator  + "embeds" + File.separator + "tempmute.json"));

        for (Map.Entry<Locale, Object> entry : getLocales().entrySet()) {
            locales.put(entry.getKey(), (BukkitConfiguration) entry.getValue());
        }
        this.embeds = getEmbeds();
        defaultLocale = Utils.stringToLocale(getConfig().getString("default-server-language"));
        this.exemptPlayers = getConfig().getStringList("exempt-players");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void copyFileFromResources(File file) {
        if (!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        if (!file.exists() && !file.isDirectory()) {
            try {
                InputStream inputStream = PunishManager.getInstance().getResourceStream(file.getName());
                Files.copy(inputStream, file.toPath());
            } catch (IOException e) {
                plugin.getLogger().severe(String.format("Error while trying to load file {0}: " + e.getMessage(), file.getName()));
                throw new RuntimeException(e);
            }
        }
    }
}
