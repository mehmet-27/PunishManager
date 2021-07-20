package com.mehmet_27.punishmanager;

import co.aikar.commands.BungeeCommandManager;
import com.mehmet_27.punishmanager.events.PlayerLoginEvent;
import com.mehmet_27.punishmanager.managers.*;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.api.plugin.Plugin;

import java.sql.Connection;


public final class PunishManager extends Plugin {
    private static PunishManager instansce;

    private MysqlManager sql;
    private ConfigManager configManager;
    private CommandManager commandManager;
    private MessageManager messageManager;
    private DisconnectManager disconnectManager;
    private PunishmentManager punishmentManager;
    private BungeeCommandManager manager;

    @Override
    public void onEnable() {
        instansce = this;
        configManager = new ConfigManager(this);
        configManager.load();
        sql = new MysqlManager(this);
        sql.setup();
        getProxy().getLogger().info((!sql.isConnected() ? Utils.color("&cPlease check mysql information.") : Utils.color("&aDatabase is connected.")));
        manager = new BungeeCommandManager(this);
        messageManager = new MessageManager();
        disconnectManager = new DisconnectManager();
        punishmentManager = new PunishmentManager();
        commandManager = new CommandManager(this);
        getProxy().getPluginManager().registerListener(this, new PlayerLoginEvent());
    }

    @Override
    public void onDisable() {
       sql.disconnect();
    }

    public static PunishManager getInstance(){
        return instansce;
    }
    public Connection getConnection() {
        return sql.getConnection();
    }
    public MessageManager getMessageManager(){
        return messageManager;
    }
    public ConfigManager getConfigManager(){
        return configManager;
    }
    public DisconnectManager getDisconnectManager(){
        return disconnectManager;
    }
    public PunishmentManager getPunishmentManager(){
        return punishmentManager;
    }
    public BungeeCommandManager getManager(){
        return manager;
    }
}
