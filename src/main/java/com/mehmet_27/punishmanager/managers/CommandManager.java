package com.mehmet_27.punishmanager.managers;

import co.aikar.commands.*;
import co.aikar.locales.MessageKey;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.objects.OfflinePlayer;
import com.mehmet_27.punishmanager.utils.Utils;
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

public class CommandManager extends BungeeCommandManager {
    private final PunishManager punishManager;

    public CommandManager(PunishManager punishManager) {
        super(punishManager);
        this.punishManager = punishManager;
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
        registerDependency(ConfigManager.class, punishManager.getConfigManager());
        registerDependency(StorageManager.class, punishManager.getStorageManager());
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
        getCommandConditions().addCondition(OfflinePlayer.class, "other_player", (context, exec, value) -> {
            BungeeCommandIssuer issuer = context.getIssuer();
            if(!punishManager.getConfigManager().getConfig().getBoolean("self-punish")){
                if (issuer.isPlayer() && issuer.getPlayer().getName().equals(value.getPlayerName())) {
                    throw new ConditionFailedException(getMessage(issuer, "main.not-on-yourself"));
                }
            }
        });
        getCommandConditions().addCondition("requireProtocolize", (context) -> {
            if (!Utils.isPluginEnabled("Protocolize")) {
                throw new ConditionFailedException(Utils.color("The gui feature will not work because the Protocolize plugin cannot be found."));
            }
        });
    }

    public void registerContexts(){
        getCommandContexts().registerContext(OfflinePlayer.class, c -> {
            String playerName = c.popFirstArg();
            OfflinePlayer offlinePlayer = punishManager.getOfflinePlayers().get(playerName);
            if (offlinePlayer == null){
                throw new InvalidCommandArgument(getMessage(c.getIssuer(), "main.not-logged-server"));
            }
            return punishManager.getOfflinePlayers().get(playerName);
        });
        getCommandContexts().registerContext(OfflinePlayer.class, c -> {
            String playerName = c.popFirstArg();
            OfflinePlayer offlinePlayer = punishManager.getOfflinePlayers().get(playerName);
            if (offlinePlayer == null){
                throw new InvalidCommandArgument(getMessage(c.getIssuer(), "main.not-logged-server"));
            }
            return punishManager.getOfflinePlayers().get(playerName);
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
            if (punishManager.getConfigManager().getConfig().getBoolean("show-all-names-in-tab-completion")) {
                return punishManager.getAllPlayerNames();
            }
            return punishManager.getProxy().getPlayers().stream().map(ProxiedPlayer::getName).collect(Collectors.toSet());
        });
    }

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

    public void updateDefaultLocale(){
        Locale locale = punishManager.getConfigManager().getDefaultLocale();
        getLocales().setDefaultLocale(locale);
    }

    public String getMessage(CommandIssuer issuer, String key){
        return getLocales().getMessage(issuer, MessageKey.of(key));
    }

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
