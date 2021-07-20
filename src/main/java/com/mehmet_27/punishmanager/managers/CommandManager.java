package com.mehmet_27.punishmanager.managers;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.commands.BanCommand;
import com.mehmet_27.punishmanager.commands.KickCommand;
import com.mehmet_27.punishmanager.commands.UnBanCommand;
import net.md_5.bungee.api.plugin.Command;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.mehmet_27.punishmanager.managers.PermissionManager.Permissions.*;

public class CommandManager {
    private final PunishManager plugin;
    private final PermissionManager permissionManager;

    public CommandManager(PunishManager plugin) {
        this.plugin = plugin;
        this.permissionManager = PunishManager.getPermissionManager();
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
