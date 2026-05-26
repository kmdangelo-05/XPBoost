package me.titanet.kmdangelo.xPBoost.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AllArgsConstructor;
import lombok.Data;
import me.titanet.kmdangelo.xPBoost.XPBoost;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;

@Data
public class MySQLDatabaseConnector {

    private JavaPlugin plugin;
    private HikariDataSource dataSource;

    public MySQLDatabaseConnector(XPBoost plugin) {
        this.plugin = plugin;
    }

    public void connect() {

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(plugin.getConfig().getString("database.url", ""));
        config.setUsername(plugin.getConfig().getString("database.username", ""));
        config.setPassword(plugin.getConfig().getString("database.password", ""));
        config.addDataSourceProperty("characterEncoding", plugin.getConfig().getString("database.character_encoding", ""));
        config.addDataSourceProperty("useSSL", plugin.getConfig().getString("database.use_ssl", ""));

        dataSource = new HikariDataSource(config);
    }

    public void disconnect() {

        if (!dataSource.isClosed()) {
            dataSource.close();
        }

    }

    public synchronized Connection getConnection() throws SQLException {

        if (dataSource == null) {
            throw new SQLException("Unable to get a connection");
        }
        Connection connection = dataSource.getConnection();

        if (connection == null) {
            throw new SQLException("Unable to get a connection");
        }

        return connection;
    }


}
