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
