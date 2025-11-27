package com.parkingapp.repository;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public class OracleJdbcHelper {
    // optional shared connection that can be closed on application shutdown
    private static volatile Connection sharedConnection = null;

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

    /**
     * Returns a shared connection instance. The connection is created lazily
     * and retained so callers can inspect it; the application shutdown listener
     * will close this shared connection.
     */
    public static synchronized Connection getSharedConnection() {
        try {
            if (sharedConnection == null || sharedConnection.isClosed()) {
                sharedConnection = getConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sharedConnection;
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

    /**
     * Close the shared connection (if any) and clear the reference.
     */
    public static synchronized void closeSharedConnection() {
        try {
            closeConnection(sharedConnection);
        } finally {
            sharedConnection = null;
        }
    }

    /**
     * Deregister JDBC drivers loaded by this classloader to avoid memory leaks
     * when context shuts down (useful in certain container environments).
     */
    public static void deregisterJdbcDrivers() {
        try {
            java.util.Enumeration<java.sql.Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                java.sql.Driver driver = drivers.nextElement();
                try {
                    DriverManager.deregisterDriver(driver);
                } catch (SQLException e) {
                    // ignore individual driver dereg failures
                }
            }
        } catch (Exception e) {
            // ignore
        }
    }

}
