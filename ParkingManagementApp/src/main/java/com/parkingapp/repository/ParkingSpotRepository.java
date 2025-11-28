package com.parkingapp.repository;

import com.parkingapp.enums.SpotStatus;
import com.parkingapp.enums.SpotType;
import com.parkingapp.model.ParkingSpot;

import java.util.List;
import java.util.Optional;

/*
  Giao diện truy xuất chỗ đỗ (có thể implement bằng DB; ban đầu dùng InMemory )
 */
public interface ParkingSpotRepository {
    Optional<ParkingSpot> findById(Long id);

    List<ParkingSpot> findAll();

    List<ParkingSpot> findByTypeAndStatus(SpotType type, SpotStatus status);
    List<ParkingSpot> search(String keyword);

    ParkingSpot save(ParkingSpot spot);

    ParkingSpot update(ParkingSpot spot);

    void deleteById(Long id);
}