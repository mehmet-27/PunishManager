package com.mehmet_27.punishmanager.managers;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PermissionManager {

    public enum Permissions{

        COMMAND_BAN("punishmanager.command.ban"),
        COMMAND_UNBAN("punishmanager.command.unban"),
        COMMAND_KICK("punishmanager.command.kick");

        private final String permission;

        Permissions(String permission) {
            this.permission = permission;
        }
    }
    public String getPermission(Permissions permission){
        return permission.permission;
    }
}