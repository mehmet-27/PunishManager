package com.mehmet_27.punishmanager;

import co.aikar.commands.BungeeCommandManager;
import com.mehmet_27.punishmanager.events.PlayerChatEvent;
import com.mehmet_27.punishmanager.events.PlayerLoginEvent;
import com.mehmet_27.punishmanager.events.PlayerSettingsChangeEvent;
import com.mehmet_27.punishmanager.events.PunishEvent;
import com.mehmet_27.punishmanager.managers.CommandManager;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.managers.DatabaseManager;
import com.mehmet_27.punishmanager.managers.DiscordManager;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.List;

public final class PunishManager extends Plugin {

    private static PunishManager instance;

    private ConfigManager configManager;
    private DatabaseManager dataBaseManager;
    private DiscordManager discordManager;

    private List<String> allPlayerNames;
    private List<String> bannedIps;

    public static PunishManager getInstance() {
        return instance;
    }

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

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public DatabaseManager getDataBaseManager() {
        return dataBaseManager;
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
}