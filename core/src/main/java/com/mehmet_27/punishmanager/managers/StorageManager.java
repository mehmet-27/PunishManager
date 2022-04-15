package com.mehmet_27.punishmanager.managers;

import com.mehmet_27.punishmanager.ConfigurationAdapter;
import com.mehmet_27.punishmanager.MethodInterface;
import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.objects.OfflinePlayer;
import com.mehmet_27.punishmanager.objects.Punishment;
import com.mehmet_27.punishmanager.utils.SqlQuery;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.*;

import static com.mehmet_27.punishmanager.objects.Punishment.PunishType;
import static com.mehmet_27.punishmanager.objects.Punishment.PunishType.*;
import static com.mehmet_27.punishmanager.utils.UtilsCore.debug;

public class StorageManager {

    private final MethodInterface methods = PunishManager.getInstance().getMethods();
    private final HikariDataSource source = new HikariDataSource();

    public StorageManager() {
        ConfigurationAdapter config = methods.getConfig();
        String pluginName = methods.getPluginName();
        source.setPoolName("[" + pluginName + "]" + " Hikari");
        boolean mysqlEnabled = methods.getConfig().getBoolean("mysql.enable");
        String info = "Loading storage provider: %s";
        if (mysqlEnabled) {
            methods.getLogger().info(String.format(info, "MySQL"));
        } else {
            methods.getLogger().info(String.format(info, "H2"));
        }
        if (mysqlEnabled) {
            source.setJdbcUrl("jdbc:mysql://" + config.getString("mysql.host") + ":" + config.getString("mysql.port") + "/" + config.getString("mysql.database") + "?useSSL=false&characterEncoding=utf-8");
            source.setUsername(config.getString("mysql.username"));
            source.setPassword(config.getString("mysql.password"));
        } else {
            source.setDriverClassName("org.h2.Driver");
            source.setJdbcUrl("jdbc:h2:./plugins/" + pluginName + "/" + pluginName);
        }
        setup();
    }

    private void executeUpdate(String query) {
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setup() {
        executeUpdate(SqlQuery.CREATE_PUNISHMENTS_TABLE.getQuery());
        executeUpdate(SqlQuery.CREATE_PUNISHMENTHISTORY_TABLE.getQuery());
        executeUpdate(SqlQuery.CREATE_PLAYERS_TABLE.getQuery());
    }

    public void addPunishToPunishments(Punishment punishment) {
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

    public void addPunishToHistory(Punishment punishment) {
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(SqlQuery.ADD_PUNISH_TO_PUNISHMENTHISTORY.getQuery())) {
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
            ps.setString(1, punishment.getUuid().toString());
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
            ps.setString(1, punishment.getUuid().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Punishment getPunishment(UUID wantedPlayer) {
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(SqlQuery.GET_PUNISHMENT.getQuery())) {
            ps.setString(1, wantedPlayer.toString());
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
    public Punishment getPunishmentWithId(int punishmentId) {
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(SqlQuery.GET_PUNISHMENT_WITH_ID.getQuery())) {
            ps.setInt(1, punishmentId);
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

    public OfflinePlayer getOfflinePlayer(UUID wantedPlayer) {
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(SqlQuery.SELECT_PLAYER_WITH_UUID.getQuery())) {
            ps.setString(1, wantedPlayer.toString());
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                UUID uuid = UUID.fromString(result.getString("uuid"));
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
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(SqlQuery.SELECT_ALL_PLAYERS.getQuery())) {
            ResultSet result = ps.executeQuery();
            Map<String, OfflinePlayer> offlinePlayers = new HashMap<>();
            while (result.next()) {
                UUID uuid = UUID.fromString(result.getString("uuid"));
                String playerName = result.getString("name");
                String ip = result.getString("ip");
                String[] l = result.getString("language").split("_");
                Locale locale = new Locale(l[0], l[1]);
                offlinePlayers.put(playerName, new OfflinePlayer(uuid, playerName, ip, locale));
            }
            methods.getLogger().info(offlinePlayers.size() + " offline players loaded.");
            return offlinePlayers;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    public Punishment getBan(UUID wantedPlayer) {
        return getPunishment(wantedPlayer, "BAN");
    }

    public Punishment getMute(UUID wantedPlayer) {
        return getPunishment(wantedPlayer, "MUTE");
    }

    public Punishment getPunishment(UUID playerUuid, String type) {
        List<Punishment> punishments = new ArrayList<>();
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(SqlQuery.GET_PUNISHMENT.getQuery())) {
            ps.setString(1, playerUuid.toString());
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

    public boolean isLoggedServer(UUID wantedPlayer) {
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(SqlQuery.SELECT_PLAYER_WITH_UUID.getQuery())) {
            ps.setString(1, wantedPlayer.toString());
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
                UUID playerUuid = UUID.fromString(result.getString("uuid"));
                Punishment punishment = getPunishment(playerUuid);
                if (punishment.getPunishType().isTemp() && punishment.isExpired()) {
                    if (punishment.getPunishType().isMute()) {
                        PunishManager.getInstance().getDiscordManager().updateRole(punishment, DiscordManager.DiscordAction.REMOVE);
                    }
                    unPunishPlayer(punishment);
                    deleted++;
                }
            }
            if (deleted == 0) {
                methods.getLogger().info("No expired punish found.");
            } else {
                methods.getLogger().info(deleted + " expiring punish deleted.");
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

    public void addPlayer(OfflinePlayer player) {
        String ip = player.getPlayerIp();
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(SqlQuery.ADD_PLAYER_TO_PLAYERS_TABLE.getQuery())) {
            ps.setString(1, player.getUniqueId().toString());
            ps.setString(2, player.getName());
            ps.setString(3, ip);
            ps.setString(4, methods.getConfigManager().getDefaultLocale().toString());
            ps.setString(5, String.valueOf(System.currentTimeMillis()));
            ps.executeUpdate();
            debug(String.format("%s has been successfully added to the database.", player.getName()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePlayerName(OfflinePlayer player) {
        String oldName = PunishManager.getInstance().getOfflinePlayers().get(player.getName()).getName();
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(SqlQuery.UPDATE_PLAYER_NAME.getQuery())) {
            ps.setString(1, player.getName());
            ps.setString(2, player.getUniqueId().toString());
            debug(String.format("Update player name: %s -> %s", oldName, player.getName()));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void updatePlayerIp(OfflinePlayer player) {
        String oldIp = PunishManager.getInstance().getOfflinePlayers().get(player.getName()).getPlayerIp();
        String newIp = player.getPlayerIp();
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(SqlQuery.UPDATE_PLAYER_IP.getQuery())) {
            ps.setString(1, newIp);
            ps.setString(2, player.getUniqueId().toString());
            debug(String.format("Update player IP address: %s -> %s", oldIp, newIp));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateLanguage(OfflinePlayer player, Locale locale) {
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
            methods.getLogger().info(names.size() + " player names loaded.");
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

    public void updatePunishmentReason(int punishmentId, String newReason) {
        try (Connection connection = source.getConnection(); PreparedStatement ps = connection.prepareStatement(SqlQuery.UPDATE_PUNISHMENT_REASON.getQuery())) {
            ps.setString(1, newReason);
            ps.setInt(2, punishmentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
