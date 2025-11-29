package com.parkingapp.repository;

import com.parkingapp.model.Resident;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@ConditionalOnProperty(name = "app.use-inmemory", havingValue = "false")
@Repository
public class JdbcResidentRepository implements ResidentRepository {

    private final JdbcTemplate jdbc;

    public JdbcResidentRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private Resident mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Resident(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("apartment"),
            rs.getString("phone")
        );
    }

    @Override
    public Optional<Resident> findById(Long id) {
        try {
            Resident r = jdbc.queryForObject("SELECT * FROM resident WHERE id = ?", this::mapRow, id);
            return Optional.ofNullable(r);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Resident save(Resident resident) {
        if (resident.getId() == null) {
            // Insert
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbc.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO resident (name, apartment, phone) VALUES (?, ?, ?)", 
                    new String[]{"id"});
                ps.setString(1, resident.getName());
                ps.setString(2, resident.getApartment());
                ps.setString(3, resident.getPhone());
                return ps;
            }, keyHolder);
            // If DB returned generated key, set it back on the model
            if (keyHolder.getKey() != null) {
                resident.setId(keyHolder.getKey().longValue());
            }
        } else {
            // Update
            int rows = jdbc.update("UPDATE resident SET name=?, apartment=?, phone=? WHERE id=?",
                resident.getName(), resident.getApartment(), resident.getPhone(), resident.getId());
            if (rows == 0) {
                 jdbc.update("INSERT INTO resident (id, name, apartment, phone) VALUES (?, ?, ?, ?)",
                     resident.getId(), resident.getName(), resident.getApartment(), resident.getPhone());
            }
        }
        return resident;
    }

    @Override
    public void deleteById(Long id) {
        jdbc.update("DELETE FROM resident WHERE id = ?", id);
    }

    @Override
    public List<Resident> findAll() {
        return jdbc.query("SELECT * FROM resident ORDER BY name", this::mapRow);
    }

    @Override
    public List<Resident> search(String keyword) {
        String pattern = "%" + keyword + "%";
        return jdbc.query("SELECT * FROM resident WHERE LOWER(name) LIKE LOWER(?) OR LOWER(apartment) LIKE LOWER(?) OR phone LIKE ?", 
            this::mapRow, pattern, pattern, pattern);
    }
}
