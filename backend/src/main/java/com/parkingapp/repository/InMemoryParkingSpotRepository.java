package com.parkingapp.repository;

import com.parkingapp.enums.SpotStatus;
import com.parkingapp.enums.SpotType;
import com.parkingapp.model.ParkingSpot;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Implementation in-memory đơn giản để dev UI/class làm song song với DB team
 */
@Repository 
public class InMemoryParkingSpotRepository implements ParkingSpotRepository {
    private final Map<Long, ParkingSpot> storage = new ConcurrentHashMap<>();

    @Override
    public Optional<ParkingSpot> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<ParkingSpot> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<ParkingSpot> findByTypeAndStatus(SpotType type, SpotStatus status) {
        return storage.values().stream()
                .filter(s -> (type == null || s.getType() == type) &&
                             (status == null || s.getStatus() == status))
                .collect(Collectors.toList());
    }

    @Override
    public void save(ParkingSpot spot) {
        storage.put(spot.getId(), spot);
    }

    @Override
    public void update(ParkingSpot spot) {
        storage.put(spot.getId(), spot);
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }

    // helper để seed data nhanh
    public void saveAll(List<ParkingSpot> spots) {
        for (ParkingSpot s : spots) storage.put(s.getId(), s);
    }
}