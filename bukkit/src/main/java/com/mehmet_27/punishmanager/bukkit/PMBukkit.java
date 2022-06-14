package com.mehmet_27.punishmanager.bukkit;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.bukkit.inventory.InventoryController;
import com.mehmet_27.punishmanager.bukkit.listeners.ChatListener;
import com.mehmet_27.punishmanager.bukkit.listeners.CommandListener;
import com.mehmet_27.punishmanager.bukkit.listeners.ConnectionListener;
import com.mehmet_27.punishmanager.bukkit.listeners.PunishListener;
import com.mehmet_27.punishmanager.bukkit.managers.PMBukkitCommandManager;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PMBukkit extends JavaPlugin {

    private static PMBukkit instance;

    private ConfigManager configManager;
    private PMBukkitCommandManager commandManager;

    public static PMBukkit getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        PunishManager.getInstance().onEnable(new BukkitMethods());
        configManager = PunishManager.getInstance().getConfigManager();
        commandManager = new PMBukkitCommandManager(this);

        getServer().getPluginManager().registerEvents(new ConnectionListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new CommandListener(this), this);
        getServer().getPluginManager().registerEvents(new PunishListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryController(), this);
    }

    @Override
    public void onDisable() {
        PunishManager.getInstance().onDisable();
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public PMBukkitCommandManager getCommandManager() {
        return commandManager;
    }
}
