package com.parkingapp.service;

import com.parkingapp.model.ParkingSpot;
import com.parkingapp.model.Resident;

import java.util.List;
import java.util.Optional;

/**
 * Các nghiệp vụ quản lý chỗ đỗ đơn giản
 */
public interface ParkingService {
    List<ParkingSpot> listAllSpots();
    List<ParkingSpot> listFreeSpots();
    Optional<ParkingSpot> getSpotById(Long id);

    void addSpot(ParkingSpot spot);
    void removeSpot(Long spotId);

    // assign/unassign spot cho cư dân
    void assignSpotToResident(Long spotId, Resident resident) throws Exception;
    void unassignSpot(Long spotId) throws Exception;

    // change status
    void markSpotOutOfService(Long spotId);
    void markSpotFree(Long spotId);
        Optional<Resident> getResidentById(Long id);
}