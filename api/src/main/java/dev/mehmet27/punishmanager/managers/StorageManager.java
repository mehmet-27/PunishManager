package dev.mehmet27.punishmanager.managers;

import dev.mehmet27.punishmanager.MethodProvider;
import dev.mehmet27.punishmanager.PunishManager;
import dev.mehmet27.punishmanager.configuration.Configuration;
import dev.mehmet27.punishmanager.objects.OfflinePlayer;
import dev.mehmet27.punishmanager.objects.Punishment;
import dev.mehmet27.punishmanager.storage.DBCore;
import dev.mehmet27.punishmanager.storage.H2Core;
import dev.mehmet27.punishmanager.storage.MySQLCore;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;

import static dev.mehmet27.punishmanager.objects.Punishment.PunishType;
import static dev.mehmet27.punishmanager.objects.Punishment.PunishType.*;

public class StorageManager {

    private final PunishManager punishManager = PunishManager.getInstance();
    private DBCore core;
    private final MethodProvider methods = punishManager.getMethods();

    public StorageManager() {
        initializeTables();
        checkNewColumns();
    }

    public void initializeTables() {
        Configuration config = punishManager.getConfigManager().getConfig();
        if (config.getBoolean("mysql.enable")) {
            core = new MySQLCore(config.getString("mysql.host"),
                    config.getString("mysql.database"),
                    config.getInt("mysql.port"),
                    config.getString("mysql.username"),
                    config.getString("mysql.password"));
        } else {
            core = new H2Core();
        }
        if (!core.existsTable("punishmanager_punishments")) {
            methods.getLogger().info("Creating table: punishmanager_punishments");
            String query = "CREATE TABLE IF NOT EXISTS punishmanager_punishments (" +
                    " id BIGINT(20) NOT NULL auto_increment," +
                    " name VARCHAR(16)," +
                    " uuid VARCHAR(72)," +
                    " ip VARCHAR(39)," +
                    " reason VARCHAR(255)," +
                    " operator VARCHAR(16)," +
                    " operator_uuid VARCHAR(72) DEFAULT 'CONSOLE' NOT NULL," +
                    " type VARCHAR(16)," +
                    " start LONGTEXT," +
                    " end LONGTEXT," +
                    " PRIMARY KEY (id))";
            core.execute(query);
        }
        if (!core.existsTable("punishmanager_punishmenthistory")) {
            methods.getLogger().info("Creating table: punishmanager_punishmenthistory");
            String query = "CREATE TABLE IF NOT EXISTS punishmanager_punishmenthistory (" +
                    " id BIGINT(20) NOT NULL auto_increment," +
                    " name VARCHAR(16)," +
                    " uuid VARCHAR(72)," +
                    " ip VARCHAR(39)," +
                    " reason VARCHAR(255)," +
                    " operator VARCHAR(16)," +
                    " operator_uuid VARCHAR(72) DEFAULT 'CONSOLE' NOT NULL," +
                    " type VARCHAR(16)," +
                    " start LONGTEXT," +
                    " end LONGTEXT," +
                    " PRIMARY KEY (id))";
            core.execute(query);
        }
        if (!core.existsTable("punishmanager_players")) {
            methods.getLogger().info("Creating table: punishmanager_players");
            String query = "CREATE TABLE IF NOT EXISTS punishmanager_players (" +
                    " uuid VARCHAR(72) NOT NULL," +
                    " name VARCHAR(16)," +
                    " ip VARCHAR(39)," +
                    " language VARCHAR(10)," +
                    " first_login LONGTEXT NOT NULL," +
                    " last_login LONGTEXT NOT NULL," +
                    " PRIMARY KEY (uuid))";
            core.execute(query);
        }
    }

    public void checkNewColumns() {
        core.execute(String.format("ALTER TABLE %s ADD COLUMN IF NOT EXISTS %s",
                "punishmanager_players", "first_login LONGTEXT NOT NULL"));
        core.execute(String.format("ALTER TABLE %s ADD COLUMN IF NOT EXISTS %s",
                "punishmanager_players", "last_login LONGTEXT NOT NULL"));
        core.execute(String.format("ALTER TABLE %s ADD COLUMN IF NOT EXISTS %s",
                "punishmanager_punishments", "server LONGTEXT DEFAULT 'ALL' NOT NULL AFTER operator"));
        core.execute(String.format("ALTER TABLE %s ADD COLUMN IF NOT EXISTS %s",
                "punishmanager_punishments", "operator_uuid VARCHAR(72) DEFAULT 'CONSOLE' NOT NULL AFTER operator"));
        core.execute(String.format("ALTER TABLE %s ADD COLUMN IF NOT EXISTS %s",
                "punishmanager_punishmenthistory", "server LONGTEXT DEFAULT 'ALL' NOT NULL AFTER operator"));
        core.execute(String.format("ALTER TABLE %s ADD COLUMN IF NOT EXISTS %s",
                "punishmanager_punishmenthistory", "operator_uuid VARCHAR(72) DEFAULT 'CONSOLE' NOT NULL AFTER operator"));
    }

    public String getStorageProvider() {
        return punishManager.getConfigManager().getConfig().getBoolean("mysql.enable") ? "MySQL" : "H2";
    }

    public void addPunishToPunishments(Punishment punishment) {
        String query = String.format("INSERT INTO punishmanager_punishments (name, uuid, ip, reason, operator, operator_uuid, type, start, end) VALUES ('%s','%s','%s','%s','%s','%s','%s','%s','%s')",
                punishment.getPlayerName(),
                punishment.getUuid().toString(),
                punishment.getIp(),
                punishment.getReason(),
                punishment.getOperator(),
                punishment.getOperatorUUID() != null ? punishment.getOperatorUUID() : "CONSOLE",
                punishment.getPunishType().toString(),
                punishment.getStart(),
                punishment.getEnd());
        core.executeUpdateAsync(query);
    }

    public void addPunishToHistory(Punishment punishment) {
        String query = String.format("INSERT INTO punishmanager_punishmenthistory (name, uuid, ip, reason, operator, operator_uuid, type, start, end) VALUES ('%s','%s','%s','%s','%s','%s','%s','%s','%s')",
                punishment.getPlayerName(),
                punishment.getUuid().toString(),
                punishment.getIp(),
                punishment.getReason(),
                punishment.getOperator(),
                punishment.getOperatorUUID() != null ? punishment.getOperatorUUID() : "CONSOLE",
                punishment.getPunishType().toString(),
                punishment.getStart(),
                punishment.getEnd());
        core.executeUpdateAsync(query);
    }

    public void unPunishPlayer(Punishment punishment) {
        String query = String.format("DELETE FROM punishmanager_punishments WHERE uuid = '%s' and type = '%s'",
                punishment.getUuid().toString(),
                punishment.getPunishType().toString());
        core.executeUpdateAsync(query);
        if (punishment.getPunishType().equals(IPBAN)) {
            PunishManager.getInstance().getBannedIps().remove(punishment.getIp());
        }
    }

    public void removePlayerAllPunishes(Punishment punishment) {
        String query = String.format("DELETE FROM punishmanager_punishments WHERE uuid = '%s'", punishment.getUuid().toString());
        core.executeUpdateAsync(query);
    }

    public Punishment getPunishment(UUID wantedPlayer) {
        String query = String.format("SELECT * FROM punishmanager_punishments WHERE uuid = '%s'", wantedPlayer);
        try (Connection connection = core.getDataSource().getConnection()) {
            ResultSet result = connection.createStatement().executeQuery(query);
            if (result.next()) {
                String playerName = result.getString("name");
                UUID uuid = UUID.fromString(result.getString("uuid"));
                String ip = result.getString("ip");
                String reason = result.getString("reason");
                String operator = result.getString("operator");
                UUID operator_uuid = result.getString("operator_uuid").equals("CONSOLE") ? null : UUID.fromString(result.getString("operator_uuid"));
                String server = result.getString("server");
                long start = result.getLong("start");
                long end = result.getLong("end");
                PunishType punishType = PunishType.valueOf(result.getString("type"));
                int id = result.getInt("id");
                return new Punishment(playerName, uuid, ip, punishType, reason, operator, operator_uuid, server, start, end, id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Punishment getPunishmentWithId(int punishmentId) {
        String query = String.format("SELECT * FROM punishmanager_punishments WHERE id = '%s'", punishmentId);
        try (Connection connection = core.getDataSource().getConnection()) {
            ResultSet result = connection.createStatement().executeQuery(query);
            if (result.next()) {
                String playerName = result.getString("name");
                UUID uuid = UUID.fromString(result.getString("uuid"));
                String ip = result.getString("ip");
                String reason = result.getString("reason");
                String operator = result.getString("operator");
                UUID operator_uuid = result.getString("operator_uuid").equals("CONSOLE") ? null : UUID.fromString(result.getString("operator_uuid"));
                String server = result.getString("server");
                long start = result.getLong("start");
                long end = result.getLong("end");
                PunishType punishType = PunishType.valueOf(result.getString("type"));
                int id = result.getInt("id");
                return new Punishment(playerName, uuid, ip, punishType, reason, operator, operator_uuid, server, start, end, id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public OfflinePlayer getOfflinePlayer(UUID wantedPlayer) {
        String query = String.format("SELECT * FROM punishmanager_players WHERE uuid = '%s'", wantedPlayer);
        try (Connection connection = core.getDataSource().getConnection()) {
            ResultSet result = connection.createStatement().executeQuery(query);
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

    public @Nullable OfflinePlayer getOfflinePlayer(String wantedPlayer) {
        String query = String.format("SELECT * FROM punishmanager_players WHERE name = '%s'", wantedPlayer);
        try (Connection connection = core.getDataSource().getConnection()) {
            ResultSet result = connection.createStatement().executeQuery(query);
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

    public Map<UUID, OfflinePlayer> getAllOfflinePlayers() {
        String query = "SELECT * FROM punishmanager_players";
        try (Connection connection = core.getDataSource().getConnection()) {
            ResultSet result = connection.createStatement().executeQuery(query);
            Map<UUID, OfflinePlayer> offlinePlayers = new HashMap<>();
            while (result.next()) {
                UUID uuid = UUID.fromString(result.getString("uuid"));
                String playerName = result.getString("name");
                String ip = result.getString("ip");
                String[] l = result.getString("language").split("_");
                Locale locale = new Locale(l[0], l[1]);
                offlinePlayers.put(uuid, new OfflinePlayer(uuid, playerName, ip, locale));
            }
            methods.getLogger().info(offlinePlayers.size() + " offline players loaded.");
            return offlinePlayers;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new HashMap<>();
    }

    public @Nullable Punishment getBan(UUID wantedPlayer) {
        return getPunishment(wantedPlayer, BAN);
    }

    public @Nullable Punishment getIpBan(UUID wantedPlayer) {
        return getPunishment(wantedPlayer, IPBAN);
    }

    public @Nullable Punishment getMute(UUID wantedPlayer) {
        return getPunishment(wantedPlayer, MUTE);
    }

    public @Nullable Punishment getPunishment(UUID playerUuid, PunishType type) {
        String query = String.format("SELECT * FROM punishmanager_punishments WHERE uuid = '%s'", playerUuid.toString());
        List<Punishment> punishments = new ArrayList<>();
        try (Connection connection = core.getDataSource().getConnection()) {
            ResultSet result = connection.createStatement().executeQuery(query);
            while (result.next()) {
                String playerName = result.getString("name");
                UUID uuid = UUID.fromString(result.getString("uuid"));
                String ip = result.getString("ip");
                String reason = result.getString("reason");
                String operator = result.getString("operator");
                UUID operator_uuid = result.getString("operator_uuid").equals("CONSOLE") ? null : UUID.fromString(result.getString("operator_uuid"));
                String server = result.getString("server");
                long start = result.getLong("start");
                long end = result.getLong("end");
                int id = result.getInt("id");
                PunishType punishType = PunishType.valueOf(result.getString("type"));

                punishments.add(new Punishment(playerName, uuid, ip, punishType, reason, operator, operator_uuid, server, start, end, id));
            }
        } catch (SQLException exception) {
            punishManager.getLogger().log(Level.SEVERE, exception.getMessage(), exception);
        }

        for (Punishment punishment : punishments) {
            // Todo: it's ambiguous.
            // What if I want to unban only by IP and not by name?
            // (imagine the situation, where 2 players have the same IP)
            if (punishment.getPunishType().toString().contains(type.name())) {
                return punishment;
            }
        }

        return null;
    }

    public Boolean isLoggedServer(UUID wantedPlayer) {
        String query = String.format("SELECT * FROM punishmanager_players WHERE uuid = '%s'", wantedPlayer);
        try (Connection connection = core.getDataSource().getConnection()) {
            ResultSet result = connection.createStatement().executeQuery(query);
            if (result.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<String> getBannedIps() {
        String query = "SELECT * FROM punishmanager_punishments";
        List<String> ips = new ArrayList<>();
        try (Connection connection = core.getDataSource().getConnection()) {
            ResultSet result = connection.createStatement().executeQuery(query);
            if (result.next() && result.getString("type").equals(IPBAN.name())) {
                ips.add(result.getString("ip"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ips;
    }

    public void removeAllExpiredPunishes() {
        String query = "SELECT * FROM punishmanager_punishments";
        try (Connection connection = core.getDataSource().getConnection()) {
            ResultSet result = connection.createStatement().executeQuery(query);
            int deleted = 0;
            while (result.next()) {
                UUID playerUuid = UUID.fromString(result.getString("uuid"));
                Punishment punishment = getPunishment(playerUuid);
                if (punishment.getPunishType().isTemp() && punishment.isExpired()) {
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
        String query = "SELECT COUNT(*) FROM punishmanager_punishments";
        int count = 0;
        try (Connection connection = core.getDataSource().getConnection()) {
            ResultSet result = connection.createStatement().executeQuery(query);
            result.next();
            count = result.getInt(1);
            return count;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public void addPlayer(OfflinePlayer player) {
        String query = String.format("INSERT INTO punishmanager_players ( uuid, name, ip, language, first_login, last_login) VALUES ('%s','%s','%s','%s','%s','%s')",
                player.getUniqueId().toString(),
                player.getName(),
                player.getPlayerIp(),
                punishManager.getConfigManager().getDefaultLocale().toString(),
                System.currentTimeMillis(),
                System.currentTimeMillis());
        core.executeUpdateAsync(query);
        punishManager.debug(String.format("%s has been successfully added to the database.", player.getName()));
    }

    public void updatePlayerName(OfflinePlayer player) {
        String query = String.format("UPDATE punishmanager_players SET name = '%s' WHERE uuid = '%s'",
                player.getName(),
                player.getUniqueId().toString());
        core.executeUpdateAsync(query);
        String oldName = PunishManager.getInstance().getOfflinePlayers().get(player.getUniqueId()).getName();
        punishManager.debug(String.format("Update player name: %s -> %s", oldName, player.getName()));
    }

    public void updatePlayerLastLogin(UUID uuid) {
        String query = String.format("UPDATE punishmanager_players SET last_login = '%s' WHERE uuid = '%s'",
                System.currentTimeMillis(),
                uuid.toString());
        core.executeUpdateAsync(query);
    }

    public void updatePlayerIp(OfflinePlayer player) {
        String newIp = player.getPlayerIp();
        String query = String.format("UPDATE punishmanager_players SET ip = '%s' WHERE uuid = '%s'",
                newIp,
                player.getUniqueId().toString());
        core.executeUpdateAsync(query);
    }

    public void updateLanguage(UUID uuid, Locale locale) {
        String query = String.format("UPDATE punishmanager_players SET language = '%s' WHERE uuid = '%s'",
                locale.toString(),
                uuid.toString());
        core.executeUpdateAsync(query);
    }

    public List<Punishment> getAllPunishments() {
        String query = "SELECT * FROM punishmanager_punishments";
        List<Punishment> punishments = new ArrayList<>();
        try (Connection connection = core.getDataSource().getConnection()) {
            ResultSet result = connection.createStatement().executeQuery(query);
            while (result.next()) {
                String playerName = result.getString("name");
                UUID uuid = UUID.fromString(result.getString("uuid"));
                String ip = result.getString("ip");
                String reason = result.getString("reason");
                String operator = result.getString("operator");
                UUID operator_uuid = result.getString("operator_uuid").equals("CONSOLE") ? null : UUID.fromString(result.getString("operator_uuid"));
                String server = result.getString("server");
                long start = result.getLong("start");
                long end = result.getLong("end");
                int id = result.getInt("id");
                PunishType punishType = PunishType.valueOf(result.getString("type"));
                Punishment punishment = new Punishment(playerName, uuid, ip, punishType, reason, operator, operator_uuid, server, start, end, id);
                if (!punishment.isExpired()) {
                    punishments.add(punishment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return punishments;
    }

    public void updatePunishmentReason(int punishmentId, String newReason) {
        String query = String.format("UPDATE punishmanager_punishments SET reason = '%s' WHERE id = '%s'",
                newReason,
                punishmentId);
        core.executeUpdateAsync(query);
    }

    public DBCore getCore() {
        return core;
    }
}
