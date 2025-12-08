package umd.cis296.Database;

import java.sql.Connection;
import java.sql.DriverManager;

import org.tinylog.Logger;

import umd.cis296.objects.Idable;

public class Database {

    private static final String DATABASE_PATH = "./database.sqlite";
    private static final String CONNECTION_PREFIX = "jdbc:sqlite:";

    private static Database INSTANCE = null;

    private static Database openDatabase(String path) {
        Logger.info("Opening Database");

        try {
            Database database = new Database(
                DriverManager.getConnection(CONNECTION_PREFIX + DATABASE_PATH)
            );

            return database;
        } catch (Exception ex) {
            Logger.error("Failed To Open Database");

            return null;
        }
    }

    public static Database instance() {
        if (INSTANCE == null) {
            INSTANCE = openDatabase(DATABASE_PATH);
        }
        return INSTANCE;
    }

    public static <T extends Idable> Table<T> getTable(String name, Class<T> clazz) {
        return new Table<T>(name, clazz);
    }

    public static <T extends Idable> Table<T> getTable(Class<T> clazz) {
        return new Table<T>(clazz);
    }

    //
    //

    private Connection connection;

    public Database(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return this.connection;
    }
}
