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
import java.util.logging.Logger;

public class PunishManager {
    private static PunishManager instance = null;

    private MethodInterface methods;

    private ConfigManager configManager;
    private DependencyManager dependencyManager;
    private StorageManager storageManager;
    private DiscordManager discordManager;

    private Map<String, OfflinePlayer> offlinePlayers;
    private List<String> allPlayerNames;
    private List<String> bannedIps;

    public static PunishManager getInstance() {
        return instance == null ? instance = new PunishManager() : instance;
    }

    public void onEnable(MethodInterface methodInterface) {
        this.methods = methodInterface;
        this.configManager = methods.getConfigManager();
        this.dependencyManager = new DependencyManager();
        // Download protocolize-bungeecord.jar
        if (methods.getPlatform().equals(Platform.BUNGEECORD)) {
            dependencyManager.downloadDependency(Dependency.PROTOCOLIZE_BUNGEECORD, methods.getPluginsFolder().resolve(Dependency.PROTOCOLIZE_BUNGEECORD.getFileName()));
        }
        this.storageManager = new StorageManager();
        this.offlinePlayers = storageManager.getAllOfflinePlayers();
        this.allPlayerNames = storageManager.getAllLoggedNames();
        this.bannedIps = storageManager.getBannedIps();
        this.discordManager = new DiscordManager();

        methodInterface.setupMetrics();

        new UpdateChecker(methods).start();
    }

    public void onDisable() {
        storageManager.removeAllExpiredPunishes();
        discordManager.disconnectBot();
    }

    public MethodInterface getMethods() {
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
        return discordManager == null ? discordManager = new DiscordManager() : discordManager;
    }

    public Map<String, OfflinePlayer> getOfflinePlayers() {
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
}
