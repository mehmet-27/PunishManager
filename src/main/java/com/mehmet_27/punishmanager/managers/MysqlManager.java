package com.mehmet_27.punishmanager.managers;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.utils.SqlQuery;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MysqlManager {

    private final PunishManager plugin;
    private Connection connection;

    public MysqlManager(PunishManager plugin) {
        this.plugin = plugin;
        connect();
        setup();
    }

    public void connect() {
        Configuration config = plugin.getConfigManager().getConfig();

        String host = config.getString("mysql.host");
        String port = config.getString("mysql.port");
        String database = config.getString("mysql.database");
        String username = config.getString("mysql.username");
        String password = config.getString("mysql.password");

        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false", username, password);
            plugin.getLogger().info(Utils.color("&aDatabase is connected!"));
        } catch (SQLException e) {
            plugin.getLogger().severe(Utils.color("&cDatabase is not connected: ") + e.getMessage());
        }
    }

    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void addPlayer(ProxiedPlayer player) {
        String ip = player.getSocketAddress().toString().substring(1).split(":")[0];

        try {
            PreparedStatement ps = connection.prepareStatement(SqlQuery.ADD_PLAYER_TO_PLAYERS_TABLE.toString());
            ps.setString(1, player.getUniqueId().toString());
            ps.setString(2, player.getName());
            ps.setString(3, ip);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createPunishmentsTable() {
        try {
            PreparedStatement ps = connection.prepareStatement(SqlQuery.CREATE_PUNISHMENTS_TABLE.getQuery());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createPlayersTable() {
        try {
            PreparedStatement ps = connection.prepareStatement(SqlQuery.CREATE_PLAYERS_TABLE.getQuery());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setup() {
        createPlayersTable();
        createPunishmentsTable();
    }
}
