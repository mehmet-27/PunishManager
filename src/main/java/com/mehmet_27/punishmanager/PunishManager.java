package com.mehmet_27.punishmanager;

import co.aikar.commands.BungeeCommandManager;
import com.mehmet_27.punishmanager.listeners.PlayerChatEvent;
import com.mehmet_27.punishmanager.listeners.PlayerLoginEvent;
import com.mehmet_27.punishmanager.listeners.PunishEvent;
import com.mehmet_27.punishmanager.managers.CommandManager;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.managers.StorageManager;
import com.mehmet_27.punishmanager.managers.DiscordManager;
import com.mehmet_27.punishmanager.objects.OfflinePlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.List;
import java.util.Map;

//TODO make everymethod to uuid
public final class PunishManager extends Plugin {

    private static PunishManager instance;

    private ConfigManager configManager;
    private StorageManager storageManager;
    private CommandManager commandManager;
    private DiscordManager discordManager;

    private Map<String, OfflinePlayer> offlinePlayers;
    private List<String> allPlayerNames;
    private List<String> bannedIps;

    public static PunishManager getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        configManager = new ConfigManager(this);
        storageManager = new StorageManager(this);
        new BungeeCommandManager(this);
        this.commandManager = new CommandManager(this);
        offlinePlayers = storageManager.getAllOfflinePlayers();
        allPlayerNames = storageManager.getAllLoggedNames();
        bannedIps = storageManager.getBannedIps();
        discordManager = new DiscordManager(this);
        discordManager.buildBot();
        getProxy().getPluginManager().registerListener(this, new PlayerLoginEvent());
        getProxy().getPluginManager().registerListener(this, new PlayerChatEvent());
        getProxy().getPluginManager().registerListener(this, new PunishEvent());
    }

    @Override
    public void onDisable() {
        storageManager.removeAllExpiredPunishes();
        discordManager.disconnectBot();
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public StorageManager getStorageManager() {
        return storageManager;
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

    public DiscordManager getDiscordManager() {
        return discordManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }
}