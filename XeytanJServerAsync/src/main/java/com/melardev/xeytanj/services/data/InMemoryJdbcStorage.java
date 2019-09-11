package com.melardev.xeytanj.services.data;

import com.melardev.xeytanj.models.Client;
import com.melardev.xeytanj.services.config.ConfigService;
import org.springframework.stereotype.Component;

import java.sql.*;

@Component
public class InMemoryJdbcStorage extends JdbcStorageService {

    InMemoryJdbcStorage() {
        super();
    }

    @Override
    String getDriverName() {
        return "org.h2.Driver";
    }

    @Override
    String getUserName() {
        return "sa";
    }

    @Override
    String getPassword() {
        return "";
    }

    @Override
    String getConnectionString() {
        return "jdbc:h2:mem:xeytanj_db;DB_CLOSE_DELAY=-1";
    }

    @Override
    boolean databaseExists() {
        ResultSet result = null;
        try {
            Statement statement = conDB.createStatement();
            // result = statement.executeQuery( "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME='users' AND TABLE_SCHEMA='xeytaJDB'");
            result = statement.executeQuery("SHOW SCHEMAS");
            boolean exists = false;

            if (result != null) {

                while (result.next()) {
                    String dbName = result.getNString(1);
                    if (dbName.equalsIgnoreCase("XeytanJ_DB"))
                        exists = true;
                }
            }

            result.close();
            return exists;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    protected String getCreateTableSQL() {
        return "CREATE TABLE IF NOT EXISTS `xeytanj_db`.`users` ( `id` INT NOT NULL AUTO_INCREMENT , " +
                "`os` VARCHAR(75) NOT NULL , `pcName` VARCHAR(80) NOT NULL , `country` VARCHAR(30) NOT NULL ," +
                " `city` VARCHAR(40) NOT NULL , `global_ip` VARCHAR(15) NOT NULL , `local_ip` VARCHAR(15) NOT NULL ," +
                " `lat` VARCHAR(10) NOT NULL , `lon` VARCHAR(10) NOT NULL ," +
                " `first-Connected` VARCHAR(50) NOT NULL , `last-Connected` VARCHAR(50) NOT NULL , " +
                "PRIMARY KEY (`id`))";
    }

    @Override
    protected PreparedStatement getInsertClientPreparedStatement(Connection conDB, Client client) throws SQLException {
        String selectSQL = String.format("SELECT * from %s.users where global_ip=? AND local_ip=?", DB_NAME);
        PreparedStatement preparedStatement = conDB.prepareStatement(selectSQL);
        preparedStatement.setString(1, client.getGlobalIp());
        preparedStatement.setString(2, client.getLocalIp());

        return preparedStatement;
    }


    @Override
    protected String getCreateDatabaseSQL() {
        return "CREATE SCHEMA IF NOT EXISTS " + DB_NAME + ";";
    }

    @Override
    public ConfigService.ConfigModel loadConfig() {
        return null;
    }

    @Override
    public void saveConfig(ConfigService.ConfigModel configModel) {

    }
}
