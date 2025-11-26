package com.parkingapp.repository;

import com.parkingapp.enums.SpotStatus;
import com.parkingapp.enums.SpotType;
import com.parkingapp.model.ParkingSpot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Stub JDBC-based repository. Activated when app.use-inmemory=false.
 *
 * NOTE: methods are minimal implementations so the application can run
 * while you implement real DB interactions later. They currently do not
 * persist data to a database.
 */
@ConditionalOnProperty(name = "app.use-inmemory", havingValue = "false")
@Repository
public class JdbcParkingSpotRepository implements ParkingSpotRepository {

    @Autowired(required = false)
    public JdbcParkingSpotRepository() {
        // later you can inject JdbcTemplate or use jdbc helper
    }

    @Override
    public Optional<ParkingSpot> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<ParkingSpot> findAll() {
        return new ArrayList<>();
    }

    @Override
    public List<ParkingSpot> findByTypeAndStatus(SpotType type, SpotStatus status) {
        return new ArrayList<>();
    }

    @Override
    public ParkingSpot save(ParkingSpot spot) {
        // no-op stub: return as-is
        return spot;
    }

    @Override
    public ParkingSpot update(ParkingSpot spot) {
        // no-op stub: return as-is
        return spot;
    }

    @Override
    public void deleteById(Long id) {
        // no-op
    }
}
