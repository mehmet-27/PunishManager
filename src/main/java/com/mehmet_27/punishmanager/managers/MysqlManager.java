package com.mehmet_27.punishmanager.managers;

import com.mehmet_27.punishmanager.PunishManager;
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

    public boolean isConnected(){
        return (connection != null);
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
        String address = player.getSocketAddress().toString();
        String withoutSlash = address.replaceAll("/", "");
        String[] split = withoutSlash.split(":");
        String ip = split[0];

        try {
            PreparedStatement ps = connection.prepareStatement("INSERT IGNORE INTO `punishmanager_players` (`uuid`, `name`, `ip`) VALUES (?,?,?)");
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
            String query = "CREATE TABLE IF NOT EXISTS `punishmanager_punishments` ("
                    + " `id` BIGINT(20) NOT NULL auto_increment,"
                    + " `name` VARCHAR(16),"
                    + " `uuid` VARCHAR(72),"
                    + " `ip` VARCHAR(25),"
                    + " `reason` VARCHAR(255),"
                    + " `operator` VARCHAR(16),"
                    + " `type` VARCHAR(16),"
                    + " `start` LONGTEXT,"
                    + " `end` LONGTEXT,"
                    + " PRIMARY KEY (`id`))";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createPlayersTable() {
        try {
            String query = "CREATE TABLE IF NOT EXISTS `punishmanager_players` ("
                    + " `uuid` VARCHAR(72) NOT NULL,"
                    + " `name` VARCHAR(16),"
                    + " `ip` VARCHAR(25),"
                    + " PRIMARY KEY (`uuid`))";
            PreparedStatement ps = connection.prepareStatement(query);
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
