package com.mehmet_27.punishmanager.bungee.managers;

import co.aikar.commands.*;
import co.aikar.locales.MessageKey;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.bungee.PMBungee;
import com.mehmet_27.punishmanager.bungee.Utils.Utils;
import com.mehmet_27.punishmanager.managers.CommandManager;
import com.mehmet_27.punishmanager.managers.StorageManager;
import com.mehmet_27.punishmanager.objects.OfflinePlayer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.File;
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

public class PMBungeeCommandManager extends BungeeCommandManager implements CommandManager{

    private final PMBungee plugin;

    public PMBungeeCommandManager(PMBungee plugin) {
        super(plugin);
        this.plugin = plugin;
        setup();
    }

    @Override
    public Set<Class<?>> getClasses(String packageName) {
        Set<Class<?>> classes = new LinkedHashSet<>();

        Predicate<? super Path> filter = entry -> {
            String path = entry.getFileName().toString();
            return !path.contains("$") && path.endsWith(".class");
        };

        for (Path filesPath : getFilesPath(packageName, filter)) {
            String fileName = filesPath.toString().replace("/", ".").split(".class")[0].substring(1);
            if (fileName.startsWith(".")){
                fileName = fileName.substring(1);
            }
            try {

                Class<?> clazz = Class.forName(fileName);
                classes.add(clazz);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return classes;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Set<Class<? extends T>> getClassesBySubType(String packageName, Class<?> type) {
        return getClasses(packageName).stream().
                filter(type::isAssignableFrom).
                map(aClass -> ((Class<? extends T>) aClass)).
                collect(Collectors.toSet());
    }

    @Override
    public Set<Path> getFilesPath(String path, Predicate<? super Path> filter) {
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

    @Override
    public void registerDependencies() {
        registerDependency(BungeeConfigManager.class, PunishManager.getInstance().getConfigManager());
        registerDependency(StorageManager.class, PunishManager.getInstance().getStorageManager());
    }

    @Override
    public void registerCommands() {
        Set<Class<? extends BaseCommand>> commands = getClassesBySubType("com.mehmet_27.punishmanager.bungee.commands", BaseCommand.class);
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

    @Override
    public void registerConditions() {
        getCommandConditions().addCondition(OfflinePlayer.class, "other_player", (context, exec, value) -> {
            BungeeCommandIssuer issuer = context.getIssuer();
            if (!PunishManager.getInstance().getConfigManager().getConfig().getBoolean("self-punish")) {
                if (issuer.isPlayer() && issuer.getPlayer().getName().equals(value.getName())) {
                    throw new ConditionFailedException(getMessage(issuer, "main.not-on-yourself"));
                }
            }
        });
        getCommandConditions().addCondition("requireProtocolize", (context) -> {
            if (!Utils.isPluginEnabled("Protocolize")) {
                throw new ConditionFailedException(Utils.color("The gui feature will not work because the Protocolize plugin cannot be found."));
            }
        });
        getCommandConditions().addCondition(String.class, "mustInteger", (context, exec, value) -> {
            BungeeCommandIssuer issuer = context.getIssuer();
            if (!Utils.isInteger(value)) {
                throw new ConditionFailedException(getMessage(issuer, "main.mustInteger"));
            }
        });
    }

    @Override
    public void registerContexts() {
        getCommandContexts().registerContext(OfflinePlayer.class, c -> {
            String playerName = c.popFirstArg();
            OfflinePlayer offlinePlayer = PunishManager.getInstance().getOfflinePlayers().get(playerName);
            if (offlinePlayer == null) {
                throw new InvalidCommandArgument(getMessage(c.getIssuer(), "main.not-logged-server"));
            }
            return PunishManager.getInstance().getOfflinePlayers().get(playerName);
        });
        getCommandContexts().registerContext(OfflinePlayer.class, c -> {
            String playerName = c.popFirstArg();
            OfflinePlayer offlinePlayer = PunishManager.getInstance().getOfflinePlayers().get(playerName);
            if (offlinePlayer == null) {
                throw new InvalidCommandArgument(getMessage(c.getIssuer(), "main.not-logged-server"));
            }
            return PunishManager.getInstance().getOfflinePlayers().get(playerName);
        });
    }

    @Override
    public void registerCompletions() {
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
            if (PunishManager.getInstance().getConfigManager().getConfig().getBoolean("show-all-names-in-tab-completion")) {
                return PunishManager.getInstance().getAllPlayerNames();
            }
            return PMBungee.getInstance().getProxy().getPlayers().stream().map(ProxiedPlayer::getName).collect(Collectors.toSet());
        });
    }

    @Override
    public void loadLocaleFiles(List<File> files) {
        try {
            for (File file : files) {
                String[] name = file.getName().split("[._]");
                Locale locale = new Locale(name[0], name[1]);
                getLocales().loadYamlLanguageFile(file, locale);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateDefaultLocale() {
        Locale locale = PunishManager.getInstance().getConfigManager().getDefaultLocale();
        getLocales().setDefaultLocale(locale);
    }

    public String getMessage(BungeeCommandIssuer issuer, String key) {
        return getLocales().getMessage(issuer, MessageKey.of(key));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void setup() {
        loadLocaleFiles(PunishManager.getInstance().getConfigManager().getLocaleFiles());
        updateDefaultLocale();
        registerDependencies();
        enableUnstableAPI("help");
        registerContexts();
        registerCommands();
        registerConditions();
        registerCompletions();
    }
}
