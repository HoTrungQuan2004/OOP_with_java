package com.parkingapp.service;

import com.parkingapp.model.ParkingSpot;
import com.parkingapp.model.Resident;

import java.util.List;
import java.util.Optional;

// management ui
public interface ParkingService {
    List<ParkingSpot> listAllSpots();
    List<ParkingSpot> listFreeSpots();
    List<ParkingSpot> searchSpots(String keyword);
    List<com.parkingapp.model.SpotHistory> getSpotHistory(Long spotId);
    Optional<ParkingSpot> getSpotById(Long id);

    void addSpot(ParkingSpot spot);
    void removeSpot(Long spotId);

    // assign/unassign spot to resident
    void assignSpotToResident(Long spotId, Resident resident);
    void unassignSpot(Long spotId);

    // change status of spot
    void markSpotOutOfService(Long spotId);
    void markSpotOccupied(Long spotId);
    void markSpotFree(Long spotId);
    Optional<Resident> getResidentById(Long id);
    List<Resident> getAllResidents();
    List<Resident> searchResidents(String keyword);
}