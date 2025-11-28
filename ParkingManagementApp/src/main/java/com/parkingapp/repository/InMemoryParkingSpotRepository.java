package com.parkingapp.repository;

import com.parkingapp.enums.SpotStatus;
import com.parkingapp.enums.SpotType;
import com.parkingapp.model.ParkingSpot;
import org.springframework.stereotype.Repository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Implementation in-memory đơn giản để dev UI/class làm song song với DB team
 */
@Repository
@ConditionalOnProperty(name = "app.use-inmemory", havingValue = "true", matchIfMissing = false)
public class InMemoryParkingSpotRepository implements ParkingSpotRepository {
    private final Map<Long, ParkingSpot> storage = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(1);

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
    public List<ParkingSpot> search(String keyword) {
        String term = keyword.toLowerCase();
        return storage.values().stream()
                .filter(s -> s.getCode().toLowerCase().contains(term) ||
                             s.getStatus().name().toLowerCase().contains(term) ||
                             s.getType().name().toLowerCase().contains(term))
                .collect(Collectors.toList());
    }

    @Override
    public ParkingSpot save(ParkingSpot spot) {
        long id = spot.getId();
        if (id == 0) {
            id = idSequence.getAndIncrement();
            ParkingSpot newSpot = new ParkingSpot(id, spot.getCode(), spot.getType());
            switch (spot.getStatus()) {
                case ASSIGNED:
                    if (spot.getAssignedResidentId() != null)
                        newSpot.assignToResident(spot.getAssignedResidentId());
                    break;

                case OCCUPIED:
                    newSpot.occupy();
                    break;

                case OUT_OF_SERVICE:
                    newSpot.markOutOfService();
                    break;

                default:
                    break;
            }
            storage.put(id, newSpot);
            return newSpot;
        } else {
            storage.put(id, spot);
            return spot;
        }
    }

    @Override
    public ParkingSpot update(ParkingSpot spot) {
        if (spot.getId() == null || !storage.containsKey(spot.getId())) {
            throw new IllegalArgumentException("Spot not found for update");
        }
        storage.put(spot.getId(), spot);
        return spot;
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }

    // helper để seed data nhanh
    public void saveAll(List<ParkingSpot> spots) {
        for (ParkingSpot s : spots) {
            if (s.getId() == null)
                save(s);
            else
                storage.put(s.getId(), s);
        }
    }
}