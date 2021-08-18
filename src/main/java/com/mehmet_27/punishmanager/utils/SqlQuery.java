package com.mehmet_27.punishmanager.utils;

public enum SqlQuery {
    CREATE_PUNISHMENTS_TABLE(
            "CREATE TABLE IF NOT EXISTS `asdddd` (" +
                    " `id` BIGINT(20) NOT NULL auto_increment," +
                    " `name` VARCHAR(16)," +
                    " `uuid` VARCHAR(72)," +
                    " `ip` VARCHAR(25)," +
                    " `reason` VARCHAR(255)," +
                    " `operator` VARCHAR(16)," +
                    " `type` VARCHAR(16)," +
                    " `start` LONGTEXT," +
                    " `end` LONGTEXT," +
                    " PRIMARY KEY (`id`))" +
                    " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci"
    ),
    CREATE_PLAYERS_TABLE(
            "CREATE TABLE IF NOT EXISTS `asda` (" +
                    " `uuid` VARCHAR(72) NOT NULL," +
                    " `name` VARCHAR(16)," +
                    " `ip` VARCHAR(25)," +
                    " `language` VARCHAR(10)," +
                    " PRIMARY KEY (`uuid`))" +
                    " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci"
    ),
    ADD_PLAYER_TO_PLAYERS_TABLE(
            "INSERT IGNORE INTO `punishmanager_players` (" +
                    " `uuid`, `name`, `ip`, `language`)" +
                    " VALUES (?,?,?,?)"
    ),
    ADD_PUNISH_TO_PUNISHMENTS(
            "INSERT IGNORE INTO `punishmanager_punishments` (" +
                    "`name`, `uuid`, `ip`, `reason`, `operator`, `type`, `start`, `end`)" +
                    " VALUES (?,?,?,?,?,?,?,?)"
    );
    private final String query;

    SqlQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}
