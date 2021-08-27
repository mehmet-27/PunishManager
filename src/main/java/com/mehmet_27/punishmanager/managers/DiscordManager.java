package com.mehmet_27.punishmanager.managers;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.config.Configuration;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.util.logging.ExceptionLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class DiscordManager {
    private final PunishManager plugin;
    private final Configuration config;
    private final DataBaseManager dataBaseManager;
    private DiscordApi api;

    public DiscordManager(PunishManager plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager().getConfig();
        this.dataBaseManager = plugin.getDataBaseManager();
    }

    public void buildBot() {
        //Return if feature is disabled.
        if (!config.getBoolean("discord.enable")) return;
        if (dataBaseManager.getSource() == null) {
            plugin.getLogger().severe("Discord feature will not work because DiscordSRV could not connect to database.");
        }

        new DiscordApiBuilder()
                .setToken(config.getString("discord.token"))
                .setAllIntentsExcept(Intent.GUILD_PRESENCES, Intent.GUILD_WEBHOOKS)
                .login()
                .thenAccept(this::onConnectToDiscord)
                .exceptionally(error -> {
                    plugin.getLogger().warning("Failed to connect to Discord!");
                    return null;
                });
    }

    public void onConnectToDiscord(DiscordApi api) {
        this.api = api;
        PunishManager.getInstance().getLogger().info("Bot connected to discord with name " + api.getYourself().getDiscriminatedName());
    }

    public void disconnectBot() {
        if (api != null) {
            api.disconnect();
            api = null;
        }
    }

    public void givePunishedRole(Punishment punishment) {
        if (!config.getBoolean("discord.enable") || !config.getBoolean("discord.punish-role.enable")) return;
        Optional<Server> server = api.getServerById(config.getString("discord.serverId"));
        if (!server.isPresent()) return;
        Optional<Role> role = server.flatMap(serverById -> serverById.getRoleById(config.getString("discord.punish-role.punishedRoleId")));
        if (!role.isPresent()) return;
        Optional<User> user = server.flatMap(serverById -> serverById.getMemberById(getUserId(punishment.getUuid())));
        if (!user.isPresent()) return;
        user.get().addRole(role.get()).exceptionally(ExceptionLogger.get());
        Utils.debug("Added the " + role.get().getName() + " role to " + punishment.getPlayerName() + " in Discord.");
    }

    public void removePunishedRole(Punishment punishment) {
        if (!config.getBoolean("discord.enable") || !config.getBoolean("discord.punish-role.enable")) return;
        Optional<Server> server = api.getServerById(config.getString("discord.serverId"));
        if (!server.isPresent()) return;
        Optional<Role> role = server.flatMap(serverById -> serverById.getRoleById(config.getString("discord.punish-role.punishedRoleId")));
        if (!role.isPresent()) return;
        Optional<User> user = server.flatMap(serverById -> serverById.getMemberById(getUserId(punishment.getUuid())));
        if (!user.isPresent()) return;
        user.get().removeRole(role.get()).exceptionally(ExceptionLogger.get());
        Utils.debug("Removed the " + role.get().getName() + " role to " + punishment.getPlayerName() + " in Discord.");
    }

    public String getUserId(String uuid) {
        try (Connection connection = dataBaseManager.getSource().getConnection()){
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM `discordsrv_accounts` WHERE uuid = ?");
            ps.setString(1, uuid);
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                return result.getString("discord");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
