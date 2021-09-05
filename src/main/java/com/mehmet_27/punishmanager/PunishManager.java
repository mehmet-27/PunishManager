package com.mehmet_27.punishmanager;

import co.aikar.commands.BungeeCommandManager;
import com.mehmet_27.punishmanager.listeners.PlayerChatEvent;
import com.mehmet_27.punishmanager.listeners.PlayerLoginEvent;
import com.mehmet_27.punishmanager.listeners.PlayerSettingsChangeEvent;
import com.mehmet_27.punishmanager.listeners.PunishEvent;
import com.mehmet_27.punishmanager.managers.*;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.List;

public final class PunishManager extends Plugin {

    private static PunishManager instance;

    private ConfigManager configManager;
    private DatabaseManager dataBaseManager;
    private DiscordManager discordManager;

    private List<String> allPlayerNames;
    private List<String> bannedIps;

    @Override
    public void onEnable() {
        instance = this;
        configManager = new ConfigManager(this);
        dataBaseManager = new DatabaseManager(this);
        new BungeeCommandManager(this);
        new CommandManager(this);
        allPlayerNames = dataBaseManager.getAllLoggedNames();
        bannedIps = dataBaseManager.getBannedIps();
        discordManager = new DiscordManager(this);
        discordManager.buildBot();
        getProxy().getPluginManager().registerListener(this, new PlayerLoginEvent());
        getProxy().getPluginManager().registerListener(this, new PlayerChatEvent());
        getProxy().getPluginManager().registerListener(this, new PlayerSettingsChangeEvent());
        getProxy().getPluginManager().registerListener(this, new PunishEvent());
    }

    @Override
    public void onDisable() {
        dataBaseManager.removeAllOutdatedPunishes();
        discordManager.disconnectBot();
    }

    public static PunishManager getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public DatabaseManager getDataBaseManager() {
        return dataBaseManager;
    }

    public List<String> getAllPlayerNames(){
        return allPlayerNames;
    }

    public List<String> getBannedIps() {
        return bannedIps;
    }

    public DiscordManager getDiscordManager() {
        return discordManager;
    }
}