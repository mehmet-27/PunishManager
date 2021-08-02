package com.mehmet_27.punishmanager.managers;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.objects.Punishment;
import net.md_5.bungee.config.Configuration;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.permission.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class DiscordManager {
    private final PunishManager plugin = PunishManager.getInstance();
    private final Configuration config = plugin.getConfigManager().getConfig();
    private final MysqlManager mysqlManager = new MysqlManager(plugin);
    private final Connection discordSrvData = mysqlManager.getConnection();
    private DiscordApi api;

    public void buildBot() {
        //Return if feature is disabled.
        if (!config.getBoolean("discord.enabled")) return;

        new DiscordApiBuilder()
                .setToken(config.getString("discord.token"))
                .login()
                .thenAccept(this::onConnectToDiscord)
                .exceptionally(error -> {
                    plugin.getLogger().warning("Failed to connect to Discord!");
                    return null;
                });
    }

    public void onConnectToDiscord(DiscordApi api) {
        PunishManager.getInstance().getLogger().info("Bot connected to discord with name " + api.getYourself().getDiscriminatedName());
    }

    public void disconnectBot() {
        if (api != null) {
            api.disconnect();
            api = null;
        }
    }

    public void givePunishedRole(Punishment punishment) {
        Optional<Role> role = api.getRoleById(config.getString("discord.punishedRoleId"));
        //use getUserId method
        api.getCachedUserById("366508088108253184").ifPresent(user ->
                role.ifPresent(user::addRole)
        );
    }

    public String getUserId(String uuid) {
        try {
            PreparedStatement ps = discordSrvData.prepareStatement("SELECT * FROM `discordsrv_accounts` WHERE uuid = ?");
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
