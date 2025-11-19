package com.parkingapp.repository;

import com.parkingapp.enums.SpotStatus;
import com.parkingapp.enums.SpotType;
import com.parkingapp.model.ParkingSpot;

import java.util.List;
import java.util.Optional;

/**
 * Giao diện truy xuất chỗ đỗ (DB team có thể implement bằng DB; dev ban đầu dùng InMemory)
 */
public interface ParkingSpotRepository {
    Optional<ParkingSpot> findById(Long id);
    List<ParkingSpot> findAll();
    List<ParkingSpot> findByTypeAndStatus(SpotType type, SpotStatus status);
    void save(ParkingSpot spot);
    void update(ParkingSpot spot);
    void deleteById(Long id);
}