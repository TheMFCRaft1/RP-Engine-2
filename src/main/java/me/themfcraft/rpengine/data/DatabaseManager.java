package me.themfcraft.rpengine.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

public class DatabaseManager {

    private static final Logger LOGGER = LogUtils.getLogger();
    private Connection connection;
    private final String dbPath;

    public DatabaseManager(String dbPath) {
        this.dbPath = dbPath;
    }

    public void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            LOGGER.info("Connected to SQLite database: {}", dbPath);
            initializeTables();
        } catch (ClassNotFoundException | SQLException e) {
            LOGGER.error("Failed to connect to database", e);
        }
    }

    private void initializeTables() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            // Characters table
            statement.execute("CREATE TABLE IF NOT EXISTS characters ("
                    + "uuid TEXT PRIMARY KEY,"
                    + "first_name TEXT,"
                    + "last_name TEXT,"
                    + "age INTEGER,"
                    + "gender TEXT,"
                    + "backstory TEXT"
                    + ")");

            // Economy table
            statement.execute("CREATE TABLE IF NOT EXISTS economy ("
                    + "uuid TEXT PRIMARY KEY,"
                    + "cash REAL,"
                    + "bank REAL,"
                    + "diamonds INTEGER DEFAULT 0,"
                    + "emeralds INTEGER DEFAULT 0"
                    + ")");

            // Migration: Try to add columns if they don't exist (for existing databases)
            try {
                statement.execute("ALTER TABLE economy ADD COLUMN diamonds INTEGER DEFAULT 0");
            } catch (SQLException ignored) {
            }
            try {
                statement.execute("ALTER TABLE economy ADD COLUMN emeralds INTEGER DEFAULT 0");
            } catch (SQLException ignored) {
            }

            // Player Jobs table
            statement.execute("CREATE TABLE IF NOT EXISTS player_jobs ("
                    + "uuid TEXT PRIMARY KEY,"
                    + "job_id TEXT,"
                    + "rank_id TEXT"
                    + ")");

            // Locked Objects table
            statement.execute("CREATE TABLE IF NOT EXISTS locked_objects ("
                    + "location TEXT PRIMARY KEY,"
                    + "owner_uuid TEXT,"
                    + "is_locked INTEGER"
                    + ")");

            // Character Keys table
            statement.execute("CREATE TABLE IF NOT EXISTS character_keys ("
                    + "uuid TEXT,"
                    + "key_id TEXT,"
                    + "PRIMARY KEY (uuid, key_id)"
                    + ")");

            // Corporate Accounts table
            statement.execute("CREATE TABLE IF NOT EXISTS corporate_accounts ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "name TEXT UNIQUE,"
                    + "balance REAL,"
                    + "owner_uuid TEXT,"
                    + "members TEXT" // Stored as comma-separated UUIDs for simplicity
                    + ")");

            // Jail Time table
            statement.execute("CREATE TABLE IF NOT EXISTS jail_time ("
                    + "uuid TEXT PRIMARY KEY,"
                    + "remaining_time INTEGER"
                    + ")");

            // Attributes table
            statement.execute("CREATE TABLE IF NOT EXISTS attributes ("
                    + "uuid TEXT PRIMARY KEY,"
                    + "strength INTEGER DEFAULT 10,"
                    + "stamina INTEGER DEFAULT 10"
                    + ")");

            LOGGER.info("Database tables initialized.");
        }
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to check connection status", e);
        }
        return connection;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to close database connection", e);
        }
    }
}
