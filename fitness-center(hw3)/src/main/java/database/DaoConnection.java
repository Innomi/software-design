package database;

import com.github.jasync.sql.db.Connection;
import com.github.jasync.sql.db.ConnectionPoolConfigurationBuilder;
import com.github.jasync.sql.db.postgresql.PostgreSQLConnectionBuilder;

public class DaoConnection {
    static public Connection createConnection() {
        ConnectionPoolConfigurationBuilder config = new ConnectionPoolConfigurationBuilder();
        config.setHost("localhost");
        config.setDatabase("fitness_center");
        config.setUsername("postgres");
        config.setPort(5432);
        return PostgreSQLConnectionBuilder.createConnectionPool(config);
    }
}