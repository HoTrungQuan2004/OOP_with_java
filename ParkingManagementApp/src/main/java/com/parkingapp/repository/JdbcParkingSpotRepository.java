package com.parkingapp.repository;

import com.parkingapp.enums.SpotStatus;
import com.parkingapp.enums.SpotType;
import com.parkingapp.model.ParkingSpot;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@ConditionalOnProperty(name = "app.use-inmemory", havingValue = "false")
@Repository
public class JdbcParkingSpotRepository implements ParkingSpotRepository {

    private final JdbcTemplate jdbc;

    public JdbcParkingSpotRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private ParkingSpot mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        Long id = rs.getLong("id");
        String code = rs.getString("code");
        String typeStr = rs.getString("type");
        SpotType type = null;
        if (typeStr != null) {
            try {
                type = SpotType.valueOf(typeStr.trim().toUpperCase());
            } catch (Exception e) {
                type = null;
            }
        }
        ParkingSpot spot = new ParkingSpot(id, code, type == null ? SpotType.CAR : type);
        String status = rs.getString("status");
        if (status != null) {
            SpotStatus parsedStatus = null;
            try {
                parsedStatus = SpotStatus.valueOf(status.trim().toUpperCase());
            } catch (Exception e) {
                parsedStatus = SpotStatus.FREE;
            }
            Long assignedId = rs.getObject("assigned_resident_id") == null ? null : rs.getLong("assigned_resident_id");
            spot = applyStatus(spot, parsedStatus, assignedId);
        }
        return spot;
    }

    private ParkingSpot applyStatus(ParkingSpot spot, SpotStatus status, Long assignedResidentId) {
        switch (status) {
            case ASSIGNED:
                if (assignedResidentId != null) spot.assignToResident(assignedResidentId);
                break;
            case OCCUPIED:
                spot.occupy();
                break;
            case OUT_OF_SERVICE:
                spot.markOutOfService();
                break;
            case FREE:
            default:
                spot.release();
                break;
        }
        return spot;
    }

    @Override
    public Optional<ParkingSpot> findById(Long id) {
        try {
            ParkingSpot spot = jdbc.queryForObject(
                    "SELECT id, code, type, status, assigned_resident_id FROM parking_spot WHERE id = ?",
                    this::mapRow, id);
            return Optional.ofNullable(spot);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<ParkingSpot> findAll() {
        return jdbc.query("SELECT id, code, type, status, assigned_resident_id FROM parking_spot ORDER BY id", this::mapRow);
    }

    @Override
    public List<ParkingSpot> findByTypeAndStatus(SpotType type, SpotStatus status) {
        StringBuilder sql = new StringBuilder("SELECT id, code, type, status, assigned_resident_id FROM parking_spot WHERE 1=1");
        java.util.ArrayList<Object> params = new java.util.ArrayList<>();
        if (type != null) {
            sql.append(" AND type = ?");
            params.add(type.name());
        }
        if (status != null) {
            sql.append(" AND status = ?");
            params.add(status.name());
        }
        sql.append(" ORDER BY id");
        if (params.isEmpty()) {
            return jdbc.query(sql.toString(), this::mapRow);
        } else {
            return jdbc.query(sql.toString(), this::mapRow, params.toArray());
        }
    }

    @Override
    public List<ParkingSpot> search(String keyword) {
        String sql = "SELECT id, code, type, status, assigned_resident_id FROM parking_spot " +
                     "WHERE LOWER(code) LIKE ? OR LOWER(status) LIKE ? OR LOWER(type) LIKE ? ORDER BY id";
        String term = "%" + keyword.toLowerCase() + "%";
        return jdbc.query(sql, this::mapRow, term, term, term);
    }

    @Override
    public ParkingSpot save(ParkingSpot spot) {
        // insert - assume id provided
        jdbc.update("INSERT INTO parking_spot (id, code, type, status, assigned_resident_id) VALUES (?, ?, ?, ?, ?)",
                spot.getId(), spot.getCode(), spot.getType().name(), spot.getStatus().name(), spot.getAssignedResidentId());
        return spot;
    }

    @Override
    public ParkingSpot update(ParkingSpot spot) {
        jdbc.update("UPDATE parking_spot SET code = ?, type = ?, status = ?, assigned_resident_id = ? WHERE id = ?",
                spot.getCode(), spot.getType().name(), spot.getStatus().name(), spot.getAssignedResidentId(), spot.getId());
        return spot;
    }

    @Override
    public void deleteById(Long id) {
        jdbc.update("DELETE FROM parking_spot WHERE id = ?", id);
    }
}
