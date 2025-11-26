package com.parkingapp.config;

import com.parkingapp.repository.OracleJdbcHelper;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@Component
public class DbShutdownListener implements ApplicationListener<ContextClosedEvent> {

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        System.out.println("Shutdown: closing DB shared connection and deregistering drivers...");
        try {
            OracleJdbcHelper.closeSharedConnection();
        } catch (Exception e) {
            // log and continue
            e.printStackTrace();
        }
        try {
            OracleJdbcHelper.deregisterJdbcDrivers();
        } catch (Exception e) {
            // ignore
        }
    }
}
