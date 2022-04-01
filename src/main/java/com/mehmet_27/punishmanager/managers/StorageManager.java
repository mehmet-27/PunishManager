package com.mehmet_27.punishmanager.managers;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.objects.OfflinePlayer;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.utils.SqlQuery;
import com.zaxxer.hikari.HikariDataSource;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import java.sql.*;
import java.util.*;

import static com.mehmet_27.punishmanager.managers.DiscordAction.REMOVE;
import static com.mehmet_27.punishmanager.objects.Punishment.PunishType;
import static com.mehmet_27.punishmanager.objects.Punishment.PunishType.IPBAN;
import static com.mehmet_27.punishmanager.utils.SqlQuery.*;
import static com.mehmet_27.punishmanager.utils.Utils.debug;

public class StorageManager {

    private final PunishManager punishManager;
    private final HikariDataSource source = new HikariDataSource();
    private final ConfigManager configManager;

    public StorageManager(PunishManager plugin) {
        punishManager = plugin;
        configManager = plugin.getConfigManager();
        Configuration config = configManager.getConfig();

        source.setPoolName("[" + plugin.getDescription().getName() + "]" + " Hikari");
        boolean mysqlEnabled = config.getBoolean("mysql.enable");
        String info = "Loading storage provider: %s";
        if (mysqlEnabled) {
            punishManager.getLogger().info(String.format(info, "MySQL"));
        } else {
            punishManager.getLogger().info(String.format(info, "H2"));
        }
        if (mysqlEnabled) {
            source.setJdbcUrl("jdbc:mysql://" + config.getString("mysql.host") + ":" + config.getString("mysql.port") + "/" + config.getString("mysql.database") + "?useSSL=false&characterEncoding=utf-8");
            source.setUsername(config.getString("mysql.username"));
            source.setPassword(config.getString("mysql.password"));
        } else {
            String pluginName = plugin.getDescription().getName();
            source.setDriverClassName("org.h2.Driver");
            source.setJdbcUrl("jdbc:h2:./plugins/" + pluginName + "/" + pluginName);
        }
        setup();
    }

    private void createTable(String query) {
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setup() {
        createTable(SqlQuery.CREATE_PUNISHMENTS_TABLE.getQuery());
        createTable(SqlQuery.CREATE_PLAYERS_TABLE.getQuery());
    }

    public void AddPunish(Punishment punishment) {
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(SqlQuery.ADD_PUNISH_TO_PUNISHMENTS.getQuery())) {
            ps.setString(1, punishment.getPlayerName());
            ps.setString(2, punishment.getUuid().toString());
            ps.setString(3, punishment.getIp());
            ps.setString(4, punishment.getReason());
            ps.setString(5, punishment.getOperator());
            ps.setString(6, punishment.getPunishType().toString());
            ps.setString(7, String.valueOf(punishment.getStart()));
            ps.setString(8, String.valueOf(punishment.getEnd()));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void unPunishPlayer(Punishment punishment) {
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(SqlQuery.DELETE_PUNISHMENT_WITH_TYPE.getQuery())) {
            ps.setString(1, punishment.getPlayerName());
            ps.setString(2, punishment.getPunishType().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (punishment.getPunishType().equals(IPBAN)) {
            PunishManager.getInstance().getBannedIps().remove(punishment.getIp());
        }
    }

    public void removeAllPunishes(Punishment punishment) {
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(SqlQuery.DELETE_PUNISHMENT.getQuery())) {
            ps.setString(1, punishment.getPlayerName());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Punishment getPunishment(String wantedPlayer) {
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(SqlQuery.GET_PUNISHMENT.getQuery())) {
            ps.setString(1, wantedPlayer);
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                String playerName = result.getString("name");
                String uuid = result.getString("uuid");
                String ip = result.getString("ip");
                String reason = result.getString("reason");
                String operator = result.getString("operator");
                long start = result.getLong("start");
                long end = result.getLong("end");
                PunishType punishType = PunishType.valueOf(result.getString("type"));
                int id = result.getInt("id");
                return new Punishment(playerName, UUID.fromString(uuid), ip, punishType, reason, operator, start, end, id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public OfflinePlayer getOfflinePlayer(String wantedPlayer) {
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(SELECT_PLAYER_WITH_NAME.getQuery())) {
            ps.setString(1, wantedPlayer);
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                String uuid = result.getString("uuid");
                String playerName = result.getString("name");
                String ip = result.getString("ip");
                String[] l = result.getString("language").split("_");
                Locale locale = new Locale(l[0], l[1]);
                return new OfflinePlayer(uuid, playerName, ip, locale);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, OfflinePlayer> getAllOfflinePlayers() {
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(SELECT_ALL_PLAYERS.getQuery())) {
            ResultSet result = ps.executeQuery();
            Map<String, OfflinePlayer> offlinePlayers = new HashMap<>();
            while (result.next()) {
                String uuid = result.getString("uuid");
                String playerName = result.getString("name");
                String ip = result.getString("ip");
                String[] l = result.getString("language").split("_");
                Locale locale = new Locale(l[0], l[1]);
                offlinePlayers.put(playerName, new OfflinePlayer(uuid, playerName, ip, locale));
            }
            PunishManager.getInstance().getLogger().info(offlinePlayers.size() + " offline players loaded.");
            return offlinePlayers;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    public Punishment getBan(String wantedPlayer) {
        return getPunishment(wantedPlayer, "BAN");
    }

    public Punishment getMute(String wantedPlayer) {
        return getPunishment(wantedPlayer, "MUTE");
    }

    public Punishment getPunishment(String wantedPlayer, String type) {
        List<Punishment> punishments = new ArrayList<>();
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(SqlQuery.GET_PUNISHMENT.getQuery())) {
            ps.setString(1, wantedPlayer);
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                String playerName = result.getString("name");
                String uuid = result.getString("uuid");
                String ip = result.getString("ip");
                String reason = result.getString("reason");
                String operator = result.getString("operator");
                long start = result.getLong("start");
                long end = result.getLong("end");
                int id = result.getInt("id");
                PunishType punishType = PunishType.valueOf(result.getString("type"));
                punishments.add(new Punishment(playerName, UUID.fromString(uuid), ip, punishType, reason, operator, start, end, id));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (Punishment punishment : punishments) {
            if (punishment.getPunishType().toString().contains(type.toUpperCase())) {
                return punishment;
            }
        }
        return null;
    }

    public boolean isLoggedServer(String wantedPlayer) {
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(SqlQuery.SELECT_PLAYER_WITH_NAME.getQuery())) {
            ps.setString(1, wantedPlayer);
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<String> getBannedIps() {
        List<String> ips = new ArrayList<>();
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(SqlQuery.SELECT_ALL_PUNISHMENTS.getQuery())) {
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                if (result.getString("type").equals("IPBAN")) {
                    ips.add(result.getString("ip"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ips;
    }

    public void removeAllExpiredPunishes() {
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(SqlQuery.SELECT_ALL_PUNISHMENTS.getQuery())) {
            int deleted = 0;
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                String playerName = result.getString("name");
                Punishment punishment = getPunishment(playerName);
                if (punishment.getPunishType().isTemp() && punishment.isExpired()) {
                    if (punishment.getPunishType().isMute()) {
                        punishManager.getDiscordManager().updateRole(punishment, REMOVE);
                    }
                    unPunishPlayer(punishment);
                    deleted++;
                }
            }
            if (deleted == 0) {
                punishManager.getLogger().info("No expired punish found.");
            } else {
                punishManager.getLogger().info(deleted + " expiring punish deleted.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getPunishmentsCount() {
        int count = 0;
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) FROM `punishmanager_punishments`")) {
            ResultSet result = ps.executeQuery();
            result.next();
            count = result.getInt(1);
            return count;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public void addPlayer(ProxiedPlayer player) {
        String ip = player.getSocketAddress().toString().substring(1).split(":")[0];
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(SqlQuery.ADD_PLAYER_TO_PLAYERS_TABLE.getQuery())) {
            ps.setString(1, player.getUniqueId().toString());
            ps.setString(2, player.getName());
            ps.setString(3, ip);
            ps.setString(4, configManager.getDefaultLocale().toString());
            ps.executeUpdate();
            debug(String.format("%s has been successfully added to the database.", player.getName()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePlayerName(ProxiedPlayer player) {
        String oldName = punishManager.getOfflinePlayers().get(player.getName()).getPlayerName();
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(UPDATE_PLAYER_NAME.getQuery())) {
            ps.setString(1, player.getName());
            ps.setString(2, player.getUniqueId().toString());
            debug(String.format("Update player name: %s -> %s", oldName, player.getName()));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateLanguage(ProxiedPlayer player, Locale locale) {
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(SqlQuery.UPDATE_PLAYER_LOCALE.getQuery())) {
            ps.setString(1, locale.toString());
            ps.setString(2, player.getUniqueId().toString());
            ps.executeUpdate();
            debug(String.format("The locale of %s is set to %s.", player.getName(), locale));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getUserDiscordId(UUID uuid) {
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(SqlQuery.SELECT_DISCORDSRV_WITH_UUID.getQuery())) {
            ps.setString(1, uuid.toString());
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                return result.getString("discord");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getAllLoggedNames() {
        List<String> names = new ArrayList<>();
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(SqlQuery.GET_ALL_LOGGED_NAMES.getQuery())) {
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                names.add(result.getString("name"));
            }
            PunishManager.getInstance().getLogger().info(names.size() + " player names loaded.");
            return names;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isDiscordSRVTableExits() {
        try (Connection connection = source.getConnection()) {
            DatabaseMetaData dbm = connection.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "discordsrv_accounts", null);
            return tables.next();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public List<Punishment> getAllPunishments() {
        List<Punishment> punishments = new ArrayList<>();
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(SqlQuery.SELECT_ALL_PUNISHMENTS.getQuery())) {
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                String playerName = result.getString("name");
                String uuid = result.getString("uuid");
                String ip = result.getString("ip");
                String reason = result.getString("reason");
                String operator = result.getString("operator");
                long start = result.getLong("start");
                long end = result.getLong("end");
                int id = result.getInt("id");
                PunishType punishType = PunishType.valueOf(result.getString("type"));
                Punishment punishment = new Punishment(playerName, UUID.fromString(uuid), ip, punishType, reason, operator, start, end, id);
                if (!punishment.isExpired()){
                    punishments.add(punishment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return punishments;
    }
}
