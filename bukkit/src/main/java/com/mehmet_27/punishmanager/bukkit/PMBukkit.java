package com.mehmet_27.punishmanager.bukkit;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.bukkit.listeners.ChatListener;
import com.mehmet_27.punishmanager.bukkit.listeners.ConnectionListener;
import com.mehmet_27.punishmanager.bukkit.listeners.PunishListener;
import com.mehmet_27.punishmanager.bukkit.managers.BukkitConfigManager;
import com.mehmet_27.punishmanager.bukkit.managers.PMBukkitCommandManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PMBukkit extends JavaPlugin {

    private static PMBukkit instance;

    private BukkitConfigManager configManager;
    private PMBukkitCommandManager commandManager;

    public static PMBukkit getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        configManager = new BukkitConfigManager(this);
        PunishManager.getInstance().onEnable(new BukkitMethods());
        commandManager = new PMBukkitCommandManager(this);

        getServer().getPluginManager().registerEvents(new ConnectionListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new PunishListener(this), this);
    }

    @Override
    public void onDisable() {
        PunishManager.getInstance().onDisable();
    }

    public BukkitConfigManager getConfigManager() {
        return configManager;
    }

    public PMBukkitCommandManager getCommandManager() {
        return commandManager;
    }
}
