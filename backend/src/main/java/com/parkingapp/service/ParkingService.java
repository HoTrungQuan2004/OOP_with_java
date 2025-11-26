package com.parkingapp.service;

import com.parkingapp.model.ParkingSpot;
import com.parkingapp.model.Resident;
import java.util.List;
import java.util.Optional;

public interface ParkingService {
    List<ParkingSpot> listAllSpots();
    List<ParkingSpot> listFreeSpots();
    Optional<ParkingSpot> getSpotById(Long id);
    void addSpot(ParkingSpot spot);
    void removeSpot(Long spotId);
    void assignSpotToResident(Long spotId, Resident resident) throws Exception;
    void unassignSpot(Long spotId) throws Exception;
    void markSpotOutOfService(Long spotId);
    void markSpotFree(Long spotId);
    Optional<Resident> getResidentById(Long id);
}