package org.restaurant.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @Author: Group HN-Team2
 * <p>
 * Database connection utility class.
 */
public class DbConnection {
    private  static final String DATABASE = "jdbc:sqlite:restaurant.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE);
    }
}
