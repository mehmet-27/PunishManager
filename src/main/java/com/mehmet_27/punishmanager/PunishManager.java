package com.mehmet_27.punishmanager;

import com.mehmet_27.punishmanager.events.PlayerLoginEvent;
import com.mehmet_27.punishmanager.managers.*;
import net.md_5.bungee.api.plugin.Plugin;

import java.sql.Connection;


public final class PunishManager extends Plugin {
    private static PunishManager instansce;

    private MysqlManager sql;
    private ConfigManager configManager;
    private static PermissionManager pm;
    private CommandManager commandManager;
    private MessageManager messageManager;

    @Override
    public void onEnable() {
        instansce = this;
        configManager = new ConfigManager(this);
        configManager.load();
        pm = new PermissionManager();
        commandManager = new CommandManager(this);
        messageManager = new MessageManager();
        sql = new MysqlManager(this);
        sql.createTable();
        commandManager.LoadAllCommands();
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
    public static PermissionManager getPermissionManager() {
        return pm;
    }
    public MessageManager getMessageManager(){
        return messageManager;
    }
    public ConfigManager getConfigManager(){
        return configManager;
    }

}
