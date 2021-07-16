package com.mehmet_27.punishmanager.managers;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.commands.BanCommand;
import com.mehmet_27.punishmanager.commands.KickCommand;
import com.mehmet_27.punishmanager.commands.UnBanCommand;

import static com.mehmet_27.punishmanager.managers.PermissionManager.Permissions.*;

public class CommandManager {
    private PunishManager plugin;
    private PermissionManager permissionManager;

    public CommandManager(PunishManager plugin) {
        this.plugin = plugin;
        this.permissionManager = plugin.getPermissionManager();
    }

    public void LoadAllCommands() {
        int loadedCommands = 0;
        plugin.getProxy().getPluginManager().registerCommand(plugin, new BanCommand("ban", permissionManager.getPermission(COMMAND_BAN)));
        loadedCommands++;
        plugin.getProxy().getPluginManager().registerCommand(plugin, new UnBanCommand("unban", permissionManager.getPermission(COMMAND_UNBAN)));
        loadedCommands++;
        plugin.getProxy().getPluginManager().registerCommand(plugin, new KickCommand("kick", permissionManager.getPermission(COMMAND_KICK)));
        loadedCommands++;
        plugin.getProxy().getLogger().info("Loaded " + loadedCommands + " commands.");
    }
}
