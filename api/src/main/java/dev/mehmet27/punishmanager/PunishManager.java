package dev.mehmet27.punishmanager;

import dev.mehmet27.punishmanager.managers.CommandManager;
import dev.mehmet27.punishmanager.managers.ConfigManager;
import dev.mehmet27.punishmanager.managers.DiscordManager;
import dev.mehmet27.punishmanager.managers.StorageManager;
import dev.mehmet27.punishmanager.objects.OfflinePlayer;
import dev.mehmet27.punishmanager.utils.UpdateChecker;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PunishManager {
    private static PunishManager instance = null;

    private MethodProvider methods;

    private ConfigManager configManager;
    private StorageManager storageManager;
    private CommandManager commandManager;
    private DiscordManager discordManager;

    private Map<UUID, OfflinePlayer> offlinePlayers;
    private List<String> allPlayerNames;
    private List<String> bannedIps;

    public static final String COMMANDS_PACKAGE = "dev.mehmet27.punishmanager.commands";

    public static PunishManager getInstance() {
        return instance == null ? instance = new PunishManager() : instance;
    }

    public void onEnable(MethodProvider methodProvider) {
        long startTime = System.currentTimeMillis();
        this.methods = methodProvider;
        methods.getLogger().info("Platform: " + methods.getPlatform().getFriendlyName());
        this.configManager = new ConfigManager(this);
        configManager.setup();
        this.storageManager = new StorageManager();
        this.offlinePlayers = storageManager.getAllOfflinePlayers();
        this.allPlayerNames = offlinePlayers.isEmpty() ? new ArrayList<>() : offlinePlayers.values().stream().map(OfflinePlayer::getName).collect(Collectors.toList());
        this.commandManager = methodProvider.getCommandManager();
        this.bannedIps = storageManager.getBannedIps();
        this.discordManager = new DiscordManager();

        long enableTime = System.currentTimeMillis() - startTime;
        getLogger().info(String.format("Successfully enabled within %sms", enableTime));

        methodProvider.setupMetrics();
        new UpdateChecker(methods).start();
    }

    public void onDisable() {
        getLogger().info("Shutdown process initiated...");
        if (storageManager == null) return;
        storageManager.removeAllExpiredPunishes();
        storageManager.getCore().getDataSource().close();
        if (discordManager == null) return;
        discordManager.disconnectBot();
    }

    public MethodProvider getMethods() {
        return methods;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public StorageManager getStorageManager() {
        return storageManager == null ? storageManager = new StorageManager() : storageManager;
    }

    public DiscordManager getDiscordManager() {
        return discordManager;
    }

    public Map<UUID, OfflinePlayer> getOfflinePlayers() {
        return offlinePlayers;
    }

    public List<String> getAllPlayerNames() {
        return allPlayerNames;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public List<String> getBannedIps() {
        return bannedIps;
    }

    public Logger getLogger() {
        return methods.getLogger();
    }

    public InputStream getResourceStream(String path) {
        return getClass().getClassLoader().getResourceAsStream(path);
    }

    public void debug(String message) {
        if (!configManager.getConfig().getBoolean("debug", false)) return;
        methods.getLogger().info(message);
    }
}
