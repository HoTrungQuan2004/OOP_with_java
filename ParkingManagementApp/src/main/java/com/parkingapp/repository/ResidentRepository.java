package com.parkingapp.repository;

import com.parkingapp.model.Resident;
import java.util.List;
import java.util.Optional;

public interface ResidentRepository {
    Optional<Resident> findById(Long id);
    void save(Resident resident);
    void deleteById(Long id);
    List<Resident> findAll();
    List<Resident> search(String keyword);
}
