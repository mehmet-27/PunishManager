package com.mehmet_27.punishmanager;

import com.mehmet_27.punishmanager.commands.BanCommand;
import com.mehmet_27.punishmanager.commands.KickCommand;
import com.mehmet_27.punishmanager.commands.UnBanCommand;
import com.mehmet_27.punishmanager.events.PlayerLoginEvent;
import com.mehmet_27.punishmanager.managers.CommandManager;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.managers.MysqlManager;
import com.mehmet_27.punishmanager.managers.PermissionManager;
import net.md_5.bungee.api.plugin.Plugin;

import java.sql.Connection;

import static com.mehmet_27.punishmanager.managers.PermissionManager.Permissions.*;

public final class PunishManager extends Plugin {
    private static PunishManager instansce;

    private MysqlManager sql;
    private ConfigManager config;
    private static PermissionManager pm;
    private CommandManager commandManager;

    @Override
    public void onEnable() {
        instansce = this;
        config = new ConfigManager(this);
        config.load();
        pm = new PermissionManager();
        commandManager = new CommandManager(this);
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
}
