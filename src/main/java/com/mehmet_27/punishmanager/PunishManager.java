package com.mehmet_27.punishmanager;

import co.aikar.commands.BungeeCommandManager;
import com.mehmet_27.punishmanager.events.PlayerChatEvent;
import com.mehmet_27.punishmanager.events.PlayerLoginEvent;
import com.mehmet_27.punishmanager.managers.CommandManager;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import com.mehmet_27.punishmanager.managers.MysqlManager;
import com.mehmet_27.punishmanager.managers.PunishmentManager;
import net.md_5.bungee.api.plugin.Plugin;

public final class PunishManager extends Plugin {

    private static PunishManager instance;

    private MysqlManager sql;
    private ConfigManager configManager;
    private PunishmentManager punishmentManager;

    @Override
    public void onEnable() {
        instance = this;
        configManager = new ConfigManager(this);
        sql = new MysqlManager(this);
        new BungeeCommandManager(this);
        punishmentManager = new PunishmentManager(this);
        punishmentManager.removeAllOutdatedPunishes();
        new CommandManager(this);
        getProxy().getPluginManager().registerListener(this, new PlayerLoginEvent());
        getProxy().getPluginManager().registerListener(this, new PlayerChatEvent());
    }

    @Override
    public void onDisable() {
        punishmentManager.removeAllOutdatedPunishes();
        sql.disconnect();
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
}