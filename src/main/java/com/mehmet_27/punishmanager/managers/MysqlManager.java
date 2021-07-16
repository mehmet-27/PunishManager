package com.mehmet_27.punishmanager.managers;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.utils.Utils;
import net.md_5.bungee.config.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MysqlManager {
    private PunishManager plugin;
    private Connection connection;

    private String host;
    private String port;
    private String database;
    private String username;
    private String password;

    Configuration config = ConfigManager.getConfig();

    public MysqlManager(PunishManager plugin) {
        this.plugin = plugin;

        host = config.getString("mysql.host");
        port = config.getString("mysql.port");
        database = config.getString("mysql.database");
        username = config.getString("mysql.username");
        password = config.getString("mysql.password");
        connect();
    }

    public void connect() {
        if (!isConnected()) {
            host = config.getString("mysql.host");
            port = config.getString("mysql.port");
            database = config.getString("mysql.database");
            username = config.getString("mysql.username");
            password = config.getString("mysql.password");

            try {
                connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false", username, password);
                plugin.getInstance().getLogger().info(Utils.color("&aDatabase is connected."));
            } catch (SQLException e) {
                plugin.getInstance().getLogger().info(Utils.color("&cDatabase not connected!"));
                e.printStackTrace();
            }
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

    public boolean isConnected() {
        return (connection == null ? false : true);
    }

    public void createTable(){
        try {
            String query = "CREATE TABLE IF NOT EXISTS  `punishmanager_punishments` ("
                    + " `id` BIGINT(20) NOT NULL auto_increment,"
                    + " `name` VARCHAR(16),"
                    + " `uuid` VARCHAR(72),"
                    + " `reason` VARCHAR(255),"
                    + " `operator` VARCHAR(16),"
                    + " `type` VARCHAR(16),"
                    + " PRIMARY KEY (`id`))";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
