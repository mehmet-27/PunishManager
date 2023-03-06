package dev.mehmet27.punishmanager.bungee;

import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.bungee.inventory.InventoryController;
import dev.mehmet27.punishmanager.bungee.listeners.ChatListener;
import dev.mehmet27.punishmanager.bungee.listeners.ConnectionListener;
import dev.mehmet27.punishmanager.bungee.listeners.PunishListener;
import dev.mehmet27.punishmanager.bungee.listeners.PunishRevokeListener;
import dev.mehmet27.punishmanager.bungee.managers.PMBungeeCommandManager;
import dev.mehmet27.punishmanager.managers.ConfigManager;
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
        getProxy().getPluginManager().registerListener(this, new PunishRevokeListener());

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
