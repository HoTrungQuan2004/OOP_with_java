package com.parkingapp.repository;

import com.parkingapp.model.Resident;
import java.util.Optional;

public interface ResidentRepository {
    Optional<Resident> findById(Long id);
    void save(Resident resident);
    void deleteById(Long id);
}
