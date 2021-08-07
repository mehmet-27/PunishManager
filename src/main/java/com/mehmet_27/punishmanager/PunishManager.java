package com.mehmet_27.punishmanager;

import co.aikar.commands.BungeeCommandManager;
import com.mehmet_27.punishmanager.events.PlayerChatEvent;
import com.mehmet_27.punishmanager.events.PlayerLoginEvent;
import com.mehmet_27.punishmanager.events.PlayerSettingsChangeEvent;
import com.mehmet_27.punishmanager.managers.*;
import com.mehmet_27.punishmanager.utils.SqlQuery;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public final class PunishManager extends Plugin {

    private static PunishManager instance;

    private MysqlManager sql;
    private ConfigManager configManager;
    private MessageManager messageManager;
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
        punishmentManager.removeAllOutdatedPunishes();
        messageManager = new MessageManager(this);
        new CommandManager(this);
        bannedIps = punishmentManager.getBannedIps();
        discordManager = new DiscordManager();
        discordManager.buildBot();
        getProxy().getPluginManager().registerListener(this, new PlayerLoginEvent());
        getProxy().getPluginManager().registerListener(this, new PlayerChatEvent());
        getProxy().getPluginManager().registerListener(this, new PlayerSettingsChangeEvent());
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
    public MessageManager getMessageManager() {
        return messageManager;
    }
    public List<String> getBannedIps(){
        return bannedIps;
    }
    public DiscordManager getDiscordManager(){
        return discordManager;
    }
}