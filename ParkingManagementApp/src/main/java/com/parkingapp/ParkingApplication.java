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

@SpringBootApplication(scanBasePackages = "com.parkingapp")
public class ParkingApplication {

    public static void main(String[] args) {

        // Register a JVM shutdown hook as a last-resort cleanup (Ctrl+C)
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("JVM shutdown hook: closing shared DB connection and deregistering drivers...");
        }));

        SpringApplication.run(ParkingApplication.class, args);
    }

    // seed data for quick test (no db, only in-memory repo)
    @Bean
    // only run when using in-memory repo
    // Set to 'true' in application.properties so the bean activates when app.use-inmemory=true.
    @ConditionalOnProperty(name = "app.use-inmemory", havingValue = "true", matchIfMissing = false)
    public CommandLineRunner seedData(InMemoryParkingSpotRepository repository) {
        return args -> {
            // Seed some parking spots for demo
            repository.save(new ParkingSpot(1L, "P001", SpotType.CAR));
            repository.save(new ParkingSpot(2L, "P002", SpotType.CAR));
            repository.save(new ParkingSpot(3L, "P003", SpotType.MOTORBIKE));
            repository.save(new ParkingSpot(4L, "P004", SpotType.CAR));
            repository.save(new ParkingSpot(5L, "P005", SpotType.MOTORBIKE));
            repository.save(new ParkingSpot(6L, "P006", SpotType.CAR));
            
            System.out.println("Seeded " + repository.findAll().size() + " parking spots");
            System.out.println("Web application started");
        };
    }
}