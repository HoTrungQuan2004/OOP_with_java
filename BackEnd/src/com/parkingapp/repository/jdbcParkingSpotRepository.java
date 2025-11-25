package com.parkingapp.repository;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public class jdbcParkingSpotRepository {
    public static Connection getConnection() {
        Connection connection = null;

        try {
            DriverManager.registerDriver(new oracle.jdbc.OracleDriver());

            String url = "";
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
