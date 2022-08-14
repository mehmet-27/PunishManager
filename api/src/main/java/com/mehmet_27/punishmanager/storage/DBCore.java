package com.mehmet_27.punishmanager.storage;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.ResultSet;

public interface DBCore {

    HikariDataSource getDataSource();

    ResultSet select(String query);

    void execute(String query);

    void executeUpdateAsync(String query);

    Boolean existsTable(String table);

    Boolean existsColumn(String table, String column);

}
