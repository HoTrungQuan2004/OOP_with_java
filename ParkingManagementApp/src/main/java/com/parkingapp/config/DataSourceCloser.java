package com.parkingapp.config;

import org.springframework.lang.Nullable;
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
                closeDataSource(ds);
            } else {
                // try to fetch by type
                try {
                    DataSource dsTyped = ctx.getBean(DataSource.class);
                    closeDataSource(dsTyped);
                } catch (Exception ignored) {
                }
            }
        } finally {
            try {
                java.util.Enumeration<java.sql.Driver> drivers = java.sql.DriverManager.getDrivers();
                while (drivers.hasMoreElements()) {
                    java.sql.Driver driver = drivers.nextElement();
                    try {
                        java.sql.DriverManager.deregisterDriver(driver);
                        System.out.println("Deregistered JDBC driver: " + driver);
                    } catch (java.sql.SQLException ignored) {
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    private void closeDataSource(@Nullable Object ds) {
        if (ds == null) return;
        // Prefer AutoCloseable (covers many pool implementations)
        if (ds instanceof AutoCloseable) {
            try {
                ((AutoCloseable) ds).close();
                System.out.println("DataSource closed (AutoCloseable) by DataSourceCloser");
                return;
            } catch (Exception e) {
                // continue to reflection fallback
            }
        }

        // Fallback: try to call a close() method via reflection (some pools)
        try {
            var m = ds.getClass().getMethod("close");
            m.invoke(ds);
            System.out.println("DataSource closed via reflection by DataSourceCloser");
        } catch (NoSuchMethodException e) {
            // no close method - nothing to do
        } catch (Exception ignored) {
            // ignore any failures during close
        }
    }
}
