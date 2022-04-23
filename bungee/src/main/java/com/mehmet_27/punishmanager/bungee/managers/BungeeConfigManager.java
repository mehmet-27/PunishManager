package com.mehmet_27.punishmanager.bungee.managers;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.bungee.BungeeConfiguration;
import com.mehmet_27.punishmanager.bungee.PMBungee;
import com.mehmet_27.punishmanager.bungee.Utils.Utils;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.objects.PlayerLocale;
import com.mehmet_27.punishmanager.utils.FileUtils;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class BungeeConfigManager implements ConfigManager {

    private final PMBungee plugin;
    private BungeeConfiguration config;
    private final Map<Locale, BungeeConfiguration> locales = new HashMap<>();
    private Map<String, String> embeds;
    private java.util.Locale defaultLocale;
    private List<String> exemptPlayers;

    public BungeeConfigManager(PMBungee plugin) {
        this.plugin = plugin;
    }

    @Override
    public BungeeConfiguration getConfig() {
        return config;
    }

    @Override
    public Map<Locale, Object> getLocales() {
        Map<Locale, Object> locales = new HashMap<>();
        for (File file : getLocaleFiles()) {
            Locale locale = Utils.stringToLocale(file.getName().split("\\.")[0]);
            locales.put(locale, new BungeeConfiguration(file));
        }
        plugin.getLogger().info("Found " + locales.size() + " language files.");
        return locales;
    }

    @Override
    public List<Locale> getAvailableLocales() {
        List<Locale> locales = new ArrayList<>();
        for (Map.Entry<Locale, BungeeConfiguration> locale : this.locales.entrySet()) {
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
        copyFileFromResources(new File(dataFolder + File.separator + "config.yml"));
        config = new BungeeConfiguration(new File(dataFolder + File.separator + "config.yml"));
        // Copies all locale files.
        copyFilesFromFolder("locales");
        // Copies all embed files.
        copyFilesFromFolder("embeds");

        for (Map.Entry<Locale, Object> entry : getLocales().entrySet()) {
            locales.put(entry.getKey(), (BungeeConfiguration) entry.getValue());
        }
        this.embeds = getEmbeds();
        defaultLocale = Utils.stringToLocale(getConfig().getString("default-server-language"));
        this.exemptPlayers = getConfig().getStringList("exempt-players");
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void copyFileFromResources(File file) {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists() && !file.isDirectory()) {
            try {
                InputStream inputStream = PunishManager.getInstance().getResourceStream(file.toString());
                Files.copy(inputStream, file.toPath());
            } catch (IOException e) {
                plugin.getLogger().severe(String.format("Error while trying to load file %s.", file.getName()));
                throw new RuntimeException(e);
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void copyFilesFromFolder(String folder){
        Predicate<? super Path> filter = entry -> {
            String path = entry.getFileName().toString();
            if (folder.equals("locales")){
                return path.endsWith(".yml");
            }
            if (folder.equals("embeds")){
                return path.endsWith(".json");
            }
            return false;
        };
        FileUtils.getFilesIn(folder, filter).forEach(file -> {
            File destination = new File(plugin.getDataFolder().toPath() + File.separator + folder + File.separator + file.getName());
            if (!destination.getParentFile().exists()) {
                destination.getParentFile().mkdirs();
            }
            if (!destination.exists() && !destination.isDirectory()) {
                try {
                    InputStream inputStream = PunishManager.getInstance().getResourceStream(file.toString().replace("\\", "/"));
                    Files.copy(inputStream, destination.toPath());

                } catch (IOException e) {
                    plugin.getLogger().severe(String.format("Error while trying to load file %s.", file.getName()));
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
