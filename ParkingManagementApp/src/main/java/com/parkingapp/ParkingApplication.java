package com.parkingapp;

import java.sql.Connection;
import com.parkingapp.enums.SpotType;
import com.parkingapp.model.ParkingSpot;
import com.parkingapp.repository.InMemoryParkingSpotRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * Main Spring Boot application for Parking Management System
 * This replaces DemoMain.java for web application startup
 */
@SpringBootApplication(scanBasePackages = "com.parkingapp")
public class ParkingApplication {

    public static void main(String[] args) {
        // Do not create a shared JDBC connection here. Shared connection
        // will be initialized after Spring Boot creates the DataSource by
        // the `SharedConnectionManager` component.

        // Register a JVM shutdown hook as a last-resort cleanup (covers SIGINT/Ctrl+C)
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("JVM shutdown hook: closing shared DB connection and deregistering drivers...");
            try {
                com.parkingapp.repository.OracleJdbcHelper.closeSharedConnection();
            } catch (Exception ignored) {
            }
            try {
                com.parkingapp.repository.OracleJdbcHelper.deregisterJdbcDrivers();
            } catch (Exception ignored) {
            }
        }));

        SpringApplication.run(ParkingApplication.class, args);
    }

    /**
     * Seed initial parking spot data when application starts
     * (Since we're using in-memory repository, no database needed)
     */
    @Bean
    // Run seeding only when using the in-memory repository.
    // Set to 'true' so the bean activates when app.use-inmemory=true.
    @ConditionalOnProperty(name = "app.use-inmemory", havingValue = "true", matchIfMissing = false)
    public CommandLineRunner seedData(InMemoryParkingSpotRepository repository) {
        return args -> {
            // Seed some parking spots for demo
            repository.save(new ParkingSpot(1L, "B1-01", SpotType.CAR));
            repository.save(new ParkingSpot(2L, "B1-02", SpotType.CAR));
            repository.save(new ParkingSpot(3L, "B1-03", SpotType.MOTORBIKE));
            repository.save(new ParkingSpot(4L, "B2-01", SpotType.CAR));
            repository.save(new ParkingSpot(5L, "B2-02", SpotType.MOTORBIKE));
            repository.save(new ParkingSpot(6L, "B2-03", SpotType.CAR));
            
            System.out.println("✅ Seeded " + repository.findAll().size() + " parking spots");
            System.out.println("🌐 Web application started");
        };
    }
}