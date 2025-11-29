package com.parkingapp.repository;

import com.parkingapp.model.Resident;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@ConditionalOnProperty(name = "app.use-inmemory", havingValue = "true", matchIfMissing = true)
@Repository
public class InMemoryResidentRepository implements ResidentRepository {
    private final Map<Long, Resident> storage = new ConcurrentHashMap<>();
    private final java.util.concurrent.atomic.AtomicLong idSequence = new java.util.concurrent.atomic.AtomicLong(System.currentTimeMillis());

    @Override
    public Optional<Resident> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Resident save(Resident resident) {
        if (resident.getId() == null) {
            resident.setId(idSequence.incrementAndGet());
        }
        storage.put(resident.getId(), resident);
        return resident;
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }

    @Override
    public List<Resident> findAll() {
        return new java.util.ArrayList<>(storage.values());
    }

    @Override
    public List<Resident> search(String keyword) {
        if (keyword == null || keyword.isEmpty()) return findAll();
        String lower = keyword.toLowerCase();
        return storage.values().stream()
                .filter(r -> r.getName().toLowerCase().contains(lower) || 
                             r.getApartment().toLowerCase().contains(lower) ||
                             (r.getPhone() != null && r.getPhone().contains(lower)))
                .collect(Collectors.toList());
    }
}
