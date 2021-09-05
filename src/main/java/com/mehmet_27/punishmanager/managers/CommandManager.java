package com.mehmet_27.punishmanager.managers;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BungeeCommandIssuer;
import co.aikar.commands.BungeeCommandManager;
import co.aikar.commands.ConditionFailedException;
import com.mehmet_27.punishmanager.PunishManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CommandManager extends BungeeCommandManager {
    private final PunishManager plugin;

    public CommandManager(PunishManager plugin) {
        super(plugin);
        this.plugin = plugin;
        setup();
    }

    public static Set<Class<?>> getClasses(String packageName) {
        Set<Class<?>> classes = new LinkedHashSet<>();

        Predicate<? super Path> filter = entry -> {
            String path = entry.getFileName().toString();
            return !path.contains("$") && path.endsWith(".class");
        };

        for (Path filesPath : getFilesPath(packageName, filter)) {
            String fileName = filesPath.toString().replace("/", ".").split(".class")[0].substring(1);
            try {
                Class<?> clazz = Class.forName(fileName);
                classes.add(clazz);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return classes;
    }

    @SuppressWarnings("unchecked")
    public static <T> Set<Class<? extends T>> getClassesBySubType(String packageName, Class<?> type) {
        return getClasses(packageName).stream().
                filter(type::isAssignableFrom).
                map(aClass -> ((Class<? extends T>) aClass)).
                collect(Collectors.toSet());
    }

    public static Set<Path> getFilesPath(String path, Predicate<? super Path> filter) {
        Set<Path> files = new LinkedHashSet<>();
        String packagePath = path.replace(".", "/");
        try {
            URI uri = PunishManager.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            FileSystem fileSystem = FileSystems.newFileSystem(URI.create("jar:" + uri), Collections.emptyMap());
            files = Files.walk(fileSystem.getPath(packagePath)).
                    filter(Objects::nonNull).
                    filter(filter).
                    collect(Collectors.toSet());
            fileSystem.close();
        } catch (URISyntaxException | IOException ex) {
            PunishManager.getInstance().getLogger().
                    log(Level.WARNING, "An error occurred while trying to load files: " + ex.getMessage(), ex);
        }

        return files;
    }

    private void registerDependencies() {
        registerDependency(ConfigManager.class, plugin.getConfigManager());
        registerDependency(DatabaseManager.class, plugin.getDataBaseManager());
        registerDependency(PunishManager.class, PunishManager.getInstance());
    }

    private void registerCommands() {
        Set<Class<? extends BaseCommand>> commands = getClassesBySubType("com.mehmet_27.punishmanager.commands", BaseCommand.class);
        plugin.getLogger().info(String.format("Registering %d base commands...", commands.size()));

        for (Class<? extends BaseCommand> c : commands) {
            // ACF already registers nested classes
            if (c.isMemberClass() || Modifier.isStatic(c.getModifiers())) {
                continue;
            }
            try {
                // Make an instance of commands
                BaseCommand baseCommand = c.getConstructor().newInstance();
                // Register them in ACF
                registerCommand(baseCommand);
            } catch (Exception ex) {
                plugin.getLogger().log(Level.SEVERE, "Error registering command", ex);
            }
        }
    }

    private void registerConditions() {
        getCommandConditions().addCondition(String.class, "other_player", (context, exec, value) -> {
            BungeeCommandIssuer issuer = context.getIssuer();
            if (issuer.isPlayer() && issuer.getPlayer().getName().equals(value)) {
                throw new ConditionFailedException("You cannot use this command on yourself!");
            }
        });
    }

    private void registerCompletions() {
        getCommandCompletions().registerCompletion("units", c -> {
            String input = c.getInput();
            Pattern numberPattern = Pattern.compile("^[0-9]+$");
            Matcher matcher = numberPattern.matcher(input);
            if (matcher.find()) {
                String[] units = {"y", "mo", "w", "d", "h", "m", "s"};
                List<String> completions = new ArrayList<>();
                for (String u : units) {
                    completions.add(input + u);
                }
                return completions;
            }
            return Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
        });
        getCommandCompletions().registerCompletion("players", c -> {
            if (plugin.getConfigManager().getConfig().getBoolean("show-all-names-in-tab-completion")) {
                return plugin.getAllPlayerNames();
            }
            return plugin.getProxy().getPlayers().stream().map(ProxiedPlayer::getName).collect(Collectors.toSet());
        });
    }

    public void loadLocaleFiles(Set<File> files){
        try {
            for (File file : files){
                String[] locale = file.getName().split("\\.");
                getLocales().loadYamlLanguageFile(file, new Locale(locale[0], locale[1]));
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    public void setup() {
        loadLocaleFiles(PunishManager.getInstance().getConfigManager().getLocaleFiles());
        String[] locale = plugin.getConfigManager().getDefaultLanguage().split("_");
        getLocales().setDefaultLocale(new Locale(locale[0], locale[1]));
        registerDependencies();
        enableUnstableAPI("help");
        registerConditions();
        registerCompletions();
        registerCommands();
    }

}
