package com.mehmet_27.punishmanager;

import co.aikar.commands.BungeeCommandManager;
import com.mehmet_27.punishmanager.events.PlayerChatEvent;
import com.mehmet_27.punishmanager.events.PlayerLoginEvent;
import com.mehmet_27.punishmanager.events.PlayerSettingsChangeEvent;
import com.mehmet_27.punishmanager.events.PunishEvent;
import com.mehmet_27.punishmanager.managers.*;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.List;

public final class PunishManager extends Plugin {

    private static PunishManager instance;

    private MysqlManager sql;
    private ConfigManager configManager;
    private PunishmentManager punishmentManager;
    private DiscordManager discordManager;

    private List<String> bannedIps;

    @Override
    public void onEnable() {
        instance = this;
        configManager = new ConfigManager(this);
        sql = new MysqlManager(this);
        new BungeeCommandManager(this);
        punishmentManager = new PunishmentManager(this);
        new CommandManager(this);
        bannedIps = punishmentManager.getBannedIps();
        discordManager = new DiscordManager(this);
        discordManager.buildBot();
        getProxy().getPluginManager().registerListener(this, new PlayerLoginEvent());
        getProxy().getPluginManager().registerListener(this, new PlayerChatEvent());
        getProxy().getPluginManager().registerListener(this, new PlayerSettingsChangeEvent());
        getProxy().getPluginManager().registerListener(this, new PunishEvent());
    }

    @Override
    public void onDisable() {
        punishmentManager.removeAllOutdatedPunishes();
        sql.disconnect();
        discordManager.disconnectBot();
    }

    public static PunishManager getInstance() {
        return instance;
    }

    public MysqlManager getMySQLManager() {
        return sql;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public PunishmentManager getPunishmentManager() {
        return punishmentManager;
    }

    public List<String> getBannedIps() {
        return bannedIps;
    }

    public DiscordManager getDiscordManager() {
        return discordManager;
    }
}