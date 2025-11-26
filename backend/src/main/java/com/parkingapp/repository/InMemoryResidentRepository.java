package com.parkingapp.repository;

import com.parkingapp.model.Resident;
import org.springframework.stereotype.Repository;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryResidentRepository implements ResidentRepository {
    private final Map<Long, Resident> storage = new ConcurrentHashMap<>();

    @Override
    public Optional<Resident> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public void save(Resident resident) {
        storage.put(resident.getId(), resident);
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }
}
