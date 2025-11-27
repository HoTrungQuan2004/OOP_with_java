package com.parkingapp.repository;

import java.sql.Connection;
import java.sql.SQLException;

    /**
     * Lightweight helper that holds a shared Connection instance when the
     * application needs to keep one for diagnostic or legacy reasons.
     *
     * The preferred way to obtain JDBC connections in this Spring Boot app is
     * to use the Spring-managed DataSource (injected into beans). This helper
     * only stores/returns a shared Connection created elsewhere (by a
     * startup component) and provides utility methods to close it and
     * deregister drivers on shutdown.
     */
    public class OracleJdbcHelper {
        // optional shared connection that can be closed on application shutdown
        private static volatile Connection sharedConnection = null;

        /**
         * Return the shared Connection if initialized; may be null.
         */
        public static Connection getSharedConnection() {
            return sharedConnection;
        }

        /**
         * Set the shared Connection instance. Previous connection will be
         * closed if present.
         */
        public static synchronized void setSharedConnection(Connection connection) {
            try {
                if (sharedConnection != null && !sharedConnection.isClosed()) {
                    try {
                        sharedConnection.close();
                    } catch (Exception ignored) {
                    }
                }
            } catch (SQLException ignored) {
            }
            sharedConnection = connection;
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
                java.util.Enumeration<java.sql.Driver> drivers = java.sql.DriverManager.getDrivers();
                while (drivers.hasMoreElements()) {
                    java.sql.Driver driver = drivers.nextElement();
                    try {
                        java.sql.DriverManager.deregisterDriver(driver);
                    } catch (SQLException e) {
                        // ignore individual driver dereg failures
                    }
                }
            } catch (Exception e) {
                // ignore
            }
        }

    }
