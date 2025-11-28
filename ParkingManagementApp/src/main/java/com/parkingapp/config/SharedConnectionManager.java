package com.parkingapp.config;

import com.parkingapp.repository.OracleJdbcHelper;
import javax.annotation.PreDestroy;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
public class SharedConnectionManager implements ApplicationListener<ApplicationReadyEvent> {

    private final DataSource dataSource;
    private Connection conn;

    public SharedConnectionManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            // create one shared connection for diagnostic/legacy usage
            conn = dataSource.getConnection();
            OracleJdbcHelper.setSharedConnection(conn);
            System.out.println("Shared DB connection initialized from Spring DataSource");
        } catch (Exception e) {
            System.err.println("Failed to initialize shared DB connection from DataSource: " + e.getMessage());
        }
    }

    @PreDestroy
    public void destroy() {
        try {
            OracleJdbcHelper.closeSharedConnection();
        } catch (Exception ignored) {
        }
    }
}
