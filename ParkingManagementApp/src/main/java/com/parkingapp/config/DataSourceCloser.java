package com.parkingapp.config;

import com.parkingapp.repository.OracleJdbcHelper;
import jakarta.annotation.Nullable;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class DataSourceCloser implements ApplicationListener<ContextClosedEvent> {

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        try {
            var ctx = event.getApplicationContext();
            if (ctx.containsBean("dataSource")) {
                Object ds = ctx.getBean("dataSource");
                closeIfHikari(ds);
            } else {
                // try to fetch by type
                try {
                    DataSource dsTyped = ctx.getBean(DataSource.class);
                    closeIfHikari(dsTyped);
                } catch (Exception ignored) {
                }
            }
        } finally {
            // also ensure our shared connection and drivers are cleaned up
            try {
                OracleJdbcHelper.closeSharedConnection();
            } catch (Exception ignored) {
            }
            try {
                OracleJdbcHelper.deregisterJdbcDrivers();
            } catch (Exception ignored) {
            }
        }
    }

    private void closeIfHikari(@Nullable Object ds) {
        if (ds == null) return;
        try {
            // avoid static compile dependency on Hikari if not present
            Class<?> hikariClass = Class.forName("com.zaxxer.hikari.HikariDataSource");
            if (hikariClass.isInstance(ds)) {
                try {
                    hikariClass.getMethod("close").invoke(ds);
                    System.out.println("HikariDataSource closed by DataSourceCloser");
                } catch (Exception e) {
                    // ignore
                }
            }
        } catch (ClassNotFoundException e) {
            // Hikari not on classpath — nothing to close
        }
    }
}
