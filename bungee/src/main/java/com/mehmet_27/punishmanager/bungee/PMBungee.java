package com.mehmet_27.punishmanager.bungee;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.bungee.events.PunishEvent;
import com.mehmet_27.punishmanager.bungee.listeners.ChatListener;
import com.mehmet_27.punishmanager.bungee.listeners.ConnectionListener;
import com.mehmet_27.punishmanager.bungee.listeners.PunishListener;
import com.mehmet_27.punishmanager.bungee.managers.PMBungeeCommandManager;
import com.mehmet_27.punishmanager.bungee.managers.BungeeConfigManager;
import net.md_5.bungee.api.plugin.Plugin;

public final class PMBungee extends Plugin {

    private static PMBungee instance;

    private BungeeConfigManager configManager;
    private PMBungeeCommandManager commandManager;

    public static PMBungee getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        configManager = new BungeeConfigManager(this);
        PunishManager.getInstance().onEnable(new BungeeMethods());
        commandManager = new PMBungeeCommandManager(this);

        getProxy().getPluginManager().registerListener(this, new ConnectionListener(this));
        getProxy().getPluginManager().registerListener(this, new ChatListener(this));
        getProxy().getPluginManager().registerListener(this, new PunishListener(this));
    }

    @Override
    public void onDisable() {
        PunishManager.getInstance().onDisable();
    }

    public BungeeConfigManager getConfigManager() {
        return configManager;
    }

    public PMBungeeCommandManager getCommandManager() {
        return commandManager;
    }
}
