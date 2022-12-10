package com.mehmet_27.punishmanager.bungee;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.bungee.inventory.InventoryController;
import com.mehmet_27.punishmanager.bungee.listeners.ChatListener;
import com.mehmet_27.punishmanager.bungee.listeners.ConnectionListener;
import com.mehmet_27.punishmanager.bungee.listeners.PunishListener;
import com.mehmet_27.punishmanager.bungee.listeners.PunishRevokeListener;
import com.mehmet_27.punishmanager.bungee.managers.PMBungeeCommandManager;
import com.mehmet_27.punishmanager.managers.ConfigManager;
import net.md_5.bungee.api.plugin.Plugin;

public final class PMBungee extends Plugin {

    private static PMBungee instance;

    private ConfigManager configManager;
    private PMBungeeCommandManager commandManager;

    private InventoryController inventoryController;

    public static PMBungee getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        PunishManager.getInstance().onEnable(new BungeeMethods());
        configManager = PunishManager.getInstance().getConfigManager();
        commandManager = new PMBungeeCommandManager(this);

        getProxy().getPluginManager().registerListener(this, new ConnectionListener(this));
        getProxy().getPluginManager().registerListener(this, new ChatListener(this));
        getProxy().getPluginManager().registerListener(this, new PunishListener(this));
        getProxy().getPluginManager().registerListener(this, new PunishRevokeListener(this));

        inventoryController = new InventoryController();
    }

    @Override
    public void onDisable() {
        PunishManager.getInstance().onDisable();
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public PMBungeeCommandManager getCommandManager() {
        return commandManager;
    }

    public InventoryController getInventoryController() {
        return inventoryController;
    }
}
