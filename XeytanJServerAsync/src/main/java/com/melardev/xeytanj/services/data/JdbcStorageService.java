package com.melardev.xeytanj.services.data;

import com.melardev.xeytanj.enums.DbType;
import com.melardev.xeytanj.maps.ClientGeoStructure;
import com.melardev.xeytanj.models.Client;
import com.melardev.xeytanj.services.logger.ILogger;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public abstract class JdbcStorageService implements IStorageService {

    private static final String COL_OS = "os";
    private static final String COL_PC_NAME = "pc_name";

    @Override
    public void setLogger(ILogger logger) {
        this.logger = logger;
    }

    private String userDB;
    private String passwordDB;
    private String hostDB;
    protected final String DB_NAME = "XeytanJ_DB";
    private String portDB;
    protected java.sql.Connection conDB;
    private Statement statement;
    private ResultSet resultSet;
    private ILogger logger;

    public JdbcStorageService() {

    }

    abstract String getDriverName();

    abstract String getUserName();

    abstract String getPassword();

    abstract String getConnectionString();


    @Override
    public void initialize() {
        try {
            Class.forName(getDriverName());
            conDB = DriverManager.getConnection(getConnectionString(), getUserName(), getPassword());
            statement = conDB.createStatement();

            if (!databaseExists())
                createDatabase();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    abstract boolean databaseExists();

    private void createDatabase() {
        String sqlCreateDb = getCreateDatabaseSQL();
        String sqlCreateTable = getCreateTableSQL();// ENGINE = InnoDB;";
        try {
            statement.execute(sqlCreateDb);
            statement.execute(sqlCreateTable);
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Could not create DataBase or Table");
        }
    }

    protected abstract String getCreateDatabaseSQL();

    protected abstract String getCreateTableSQL();


    private <T extends DataSource> T buildDataSource(Class<T> clazz) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        return null;
        /*
        Constructor<T> ctor = clazz.getDeclaredConstructor();
        T data = ctor.newInstance();
*/
    }


    @Override
    public Client save(Client client) {
        return null;
    }


    public boolean testConnection(DbType dbType, String host, Long port, String userName, String password) {
        logger.traceCurrentMethodName();
        String connectionString = "jdbc:%s://%s:%s";
        String driverName = "";
        try {
            if (dbType == DbType.MYSQL) {
                driverName = "mysql";
                String url = String.format(connectionString, driverName, host, port);
                //check if Database exists
                java.sql.Connection tempConnection = DriverManager.getConnection(url, userName, password);
                Statement temp = null;

                temp = tempConnection.createStatement();

                ResultSet result = temp.executeQuery(
                        "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME='users' AND TABLE_SCHEMA='XeytanJ_DB'");
                if (!result.next())
                    createDatabase();
                return true;
            }


        } catch (SQLException e) {
            return false;
        }
        return false;
    }

    private static java.sql.Timestamp getCurrentTimeStamp() {

        java.util.Date today = new java.util.Date();
        return new java.sql.Timestamp(today.getTime());

    }

    @Override
    public void insertClient(Client client) {
        logger.traceCurrentMethodName();
        try {

            PreparedStatement preparedStatement = getInsertClientPreparedStatement(conDB, client);
            ResultSet rs = preparedStatement.executeQuery();

            String writeSql;
            if (rs.next()) {
                // Client already connected in the past, only update now
                String globalIp = rs.getString("global_ip");
                String localIp = rs.getString("local_ip");

                logger.debug("Client with same ips found, only update");

                writeSql = String.format(
                        "update `%s`.users set `last-Connected`=? where global_ip=? AND local_ip= ?",
                        DB_NAME);

                preparedStatement = conDB.prepareStatement(writeSql);
                preparedStatement.setString(1, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
                preparedStatement.setString(2, globalIp);
                preparedStatement.setString(3, localIp);

            } else {
                writeSql = String.format("INSERT INTO %s.users"
                                + "(`os`,`pcName`, `country`, `city` , `global_ip`, `local_ip`, `lat`, " +
                                "`lon`, `last-Connected`, `first-Connected`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        DB_NAME);

                preparedStatement = conDB.prepareStatement(writeSql);

                preparedStatement.setString(1, client.getId().toString());
                preparedStatement.setString(1, client.getOs());
                preparedStatement.setString(2, client.getPcName());
                preparedStatement.setString(3, client.getGeoData().getCountry());
                preparedStatement.setString(4, client.getGeoData().getCity());
                preparedStatement.setString(5, client.getGlobalIp());
                preparedStatement.setString(6, client.getLocalIp());
                preparedStatement.setDouble(7, client.getGeoData().getLat());
                preparedStatement.setDouble(8, client.getGeoData().getLon());
                preparedStatement.setString(9, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
                preparedStatement.setString(10, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            }

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    protected abstract PreparedStatement getInsertClientPreparedStatement(Connection conDB, Client client) throws SQLException;

    @Override
    public List<Client> getAllClients() {
        logger.traceCurrentMethodName();
        ArrayList<Client> clients = new ArrayList<>();
        String sql = "SELECT os, pcName,city,country, global_ip, lat, lon FROM XeytanJ_DB.users";

        ResultSet result = executeQueryForResult(sql);
        if (result != null) {
            try {
                while (result.next()) {
                    Client client = new Client(null); // We don't store UUIDs in Db
                    ClientGeoStructure clientGeo = new ClientGeoStructure(
                            result.getString("city"), result.getString("country"),
                            result.getDouble("lat"),
                            result.getDouble("lon"));

                    client.setGlobalIp(result.getString("global_ip"));

                    client.setGeoData(clientGeo);

                    clients.add(client);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return clients;
    }

    private synchronized ResultSet executeQueryForResult(String sql) {
        try {
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getName() {
        return JdbcStorageService.class.getSimpleName();
    }


    @Override
    public void dispose() {

    }


}