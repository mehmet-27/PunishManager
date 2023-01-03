package dev.mehmet27.punishmanager.velocity.managers;

import co.aikar.commands.*;
import co.aikar.locales.MessageKey;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.configuration.Configuration;
import dev.mehmet27.punishmanager.configuration.ConfigurationProvider;
import dev.mehmet27.punishmanager.configuration.YamlConfiguration;
import dev.mehmet27.punishmanager.managers.CommandManager;
import dev.mehmet27.punishmanager.managers.ConfigManager;
import dev.mehmet27.punishmanager.managers.StorageManager;
import dev.mehmet27.punishmanager.objects.OfflinePlayer;
import dev.mehmet27.punishmanager.objects.Replacements;
import dev.mehmet27.punishmanager.utils.FileUtils;
import dev.mehmet27.punishmanager.utils.Utils;
import dev.mehmet27.punishmanager.velocity.PMVelocity;
import dev.mehmet27.punishmanager.velocity.utils.VelocityUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PMVelocityCommandManager extends VelocityCommandManager implements CommandManager {

    private final PMVelocity plugin;
    public PMVelocityCommandManager(ProxyServer proxy, PMVelocity plugin) {
        super(proxy, plugin);
        this.plugin = plugin;
        setup();
    }

    @Override
    public void registerDependencies() {
        registerDependency(PunishManager.class, PunishManager.getInstance());
        registerDependency(ConfigManager.class, PunishManager.getInstance().getConfigManager());
        registerDependency(StorageManager.class, PunishManager.getInstance().getStorageManager());
    }

    @Override
    public void registerCommands() {
        Set<Class<? extends BaseCommand>> commands = FileUtils.getClassesBySubType(PunishManager.COMMANDS_PACKAGE, BaseCommand.class);
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
            VelocityCommandIssuer issuer = context.getIssuer();
            if (!PunishManager.getInstance().getConfigManager().getConfig().getBoolean("self-punish")) {
                if (issuer.isPlayer() && issuer.getPlayer().getUsername().equals(value.getName())) {
                    throw new ConditionFailedException(getMessage(issuer, "main.not-on-yourself"));
                }
            }
        });
        getCommandConditions().addCondition("requireProtocolize", (context) -> {
            if (!VelocityUtils.isPluginEnabled("protocolize")) {
                throw new ConditionFailedException(Utils.color("The gui feature will not work because the Protocolize plugin cannot be found."));
            }
        });
        getCommandConditions().addCondition(String.class, "mustInteger", (context, exec, value) -> {
            VelocityCommandIssuer issuer = context.getIssuer();
            if (!Utils.isInteger(value)) {
                throw new ConditionFailedException(getMessage(issuer, "main.mustInteger"));
            }
        });
    }

    @Override
    public void registerContexts() {
        getCommandContexts().registerContext(OfflinePlayer.class, c -> {
            OfflinePlayer player;
            String arg = c.popFirstArg();
            UUID uuid;
            try {
                uuid = UUID.fromString(arg);
            } catch (IllegalArgumentException e) {
                uuid = null;
            }
            if (uuid != null) {
                player = PunishManager.getInstance().getOfflinePlayers().get(uuid);
            } else {
                player = PunishManager.getInstance().getStorageManager().getOfflinePlayer(arg);
            }
            if (player == null) {
                throw new InvalidCommandArgument(getMessage(c.getIssuer(), "main.not-logged-server"));
            }
            return player;
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
            return PMVelocity.getInstance().getServer().getAllPlayers().stream().map(Player::getUsername).collect(Collectors.toSet());
        });
    }

    @Override
    public void loadLocaleFiles(List<File> files) {
        try {
            for (File file : files) {
                String[] name = file.getName().split("[._]");
                Locale locale = new Locale(name[0], name[1]);
                this.loadYamlLanguageFile(file, locale);
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

    public String getMessage(VelocityCommandIssuer issuer, String key) {
        return plugin.getConfigManager().getMessage(key, issuer.getUniqueId());
    }

    @Override
    @SuppressWarnings("deprecation")
    public void setup() {
        loadLocaleFiles(PunishManager.getInstance().getConfigManager().getLocaleFiles());
        updateDefaultLocale();
        registerDependencies();
        addCommandReplacements();
        enableUnstableAPI("help");
        registerContexts();
        registerCommands();
        registerConditions();
        registerCompletions();
        usePerIssuerLocale(true);
    }

    public void loadYamlLanguageFile(File file, Locale locale) throws IOException {
        Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        this.loadLanguage(configuration, locale);
    }

    public void loadLanguage(Configuration config, Locale locale) {
        Iterator<String> var4 = config.getKeys().iterator();

        while (true) {
            String key;
            do {
                if (!var4.hasNext()) {
                    return;
                }

                key = var4.next();
            } while (!config.isString(key) && !config.isDouble(key) && !config.isLong(key) && !config.isInt(key) && !config.isBoolean(key));

            String value = config.getString(key);
            if (value != null && !value.isEmpty()) {
                getLocales().addMessage(locale, MessageKey.of(key), value);
            }
        }
    }

    private void addCommandReplacements() {
        getCommandReplacements().addReplacements(
                "punishmanager", "punishmanager|puma"
        );

        for (Replacements value : Replacements.values()) {
            String command = plugin.getConfigManager().getMessage(value.getPath());
            if (command == null || command.isEmpty()) command = value.getDef();
            command = command.replace(" ", "");
            String replacement = command.equals(value.getDef()) ? value.getDef() : command + "|" + value.getDef();
            getCommandReplacements().addReplacement(value.getDef(), replacement);
        }
    }
}
