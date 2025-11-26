package com.parkingapp.repository;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public class OracleJdbcHelper {
    public static Connection getConnection() {
        Connection connection = null;

        try {
            // Try to load Oracle driver if available. Use reflection to avoid
            // compile-time dependency on Oracle JDBC classes.
            try {
                Class.forName("oracle.jdbc.driver.OracleDriver");
            } catch (ClassNotFoundException e) {
                // driver not on classpath; will attempt DriverManager.getConnection
            }

            String url = ""; // set your JDBC URL when using
            String username = "";
            String password = "";

            connection = DriverManager.getConnection(url, username, password);
        } catch (NoClassDefFoundError e) {
            System.err.println("Oracle JDBC driver class not found at runtime. Ensure ojdbc11.jar is on the classpath: "
                    + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Đang test lấy thông tin DB
    public static void printInfo(Connection connection) {
        if (connection != null) {
            try {
                DatabaseMetaData metaData = connection.getMetaData();
                System.out.println(metaData.getDatabaseProductName());
                System.out.println(metaData.getDatabaseProductVersion());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
