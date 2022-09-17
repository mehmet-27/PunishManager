package com.mehmet_27.punishmanager;

import com.mehmet_27.punishmanager.dependencies.Dependency;
import com.mehmet_27.punishmanager.dependencies.DependencyManager;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.managers.DiscordManager;
import com.mehmet_27.punishmanager.managers.StorageManager;
import com.mehmet_27.punishmanager.objects.OfflinePlayer;
import com.mehmet_27.punishmanager.objects.Platform;
import com.mehmet_27.punishmanager.utils.UpdateChecker;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class PunishManager {
    private static PunishManager instance = null;

    private MethodProvider methods;

    private ConfigManager configManager;
    private DependencyManager dependencyManager;
    private StorageManager storageManager;
    private DiscordManager discordManager;

    private Map<UUID, OfflinePlayer> offlinePlayers;
    private List<String> allPlayerNames;
    private List<String> bannedIps;

    public static PunishManager getInstance() {
        return instance == null ? instance = new PunishManager() : instance;
    }

    public void onEnable(MethodProvider methodProvider) {
        this.methods = methodProvider;
        methods.getLogger().info("Platform: " + methods.getPlatform().getFriendlyName());
        this.configManager = new ConfigManager(this);
        configManager.setup();
        this.dependencyManager = new DependencyManager();
        // Download protocolize-bungeecord.jar
        if (methods.getPlatform().equals(Platform.BUNGEECORD)) {
            dependencyManager.downloadDependency(Dependency.PROTOCOLIZE_BUNGEECORD, methods.getPluginsFolder().resolve(Dependency.PROTOCOLIZE_BUNGEECORD.getFileName()));
        } else if (methods.getPlatform().equals(Platform.VELOCITY)) {
            dependencyManager.downloadDependency(Dependency.PROTOCOLIZE_VELOCITY, methods.getPluginsFolder().resolve(Dependency.PROTOCOLIZE_VELOCITY.getFileName()));
        }
        this.storageManager = new StorageManager();
        this.offlinePlayers = storageManager.getAllOfflinePlayers();
        this.allPlayerNames = storageManager.getAllLoggedNames();
        this.bannedIps = storageManager.getBannedIps();
        this.discordManager = new DiscordManager();

        methodProvider.setupMetrics();

        new UpdateChecker(methods).start();
    }

    public void onDisable() {
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

    public DependencyManager getDependencyManager() {
        return dependencyManager;
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
