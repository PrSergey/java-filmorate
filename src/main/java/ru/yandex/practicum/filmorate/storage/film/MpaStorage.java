package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    public MpaStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Mpa> getAllMpa() {
        log.debug("Выдача всех возрастных ограничений.");
        List<Mpa> mpas = new ArrayList<>();

        String sql = "select * from mpa";
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sql);
        while (genreRows.next()) {
            Mpa mpa = new Mpa(genreRows.getInt("mpa_id"), genreRows.getString("mpa_name"));
            mpas.add(mpa);
        }

        return mpas;
    }

    public String getNameMpa(Integer id) {

        String nameOfMpa = "";

        String sql = "select mpa_name from mpa where mpa_id = ?";
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(sql, id);
        while (mpaRows.next()) {
            nameOfMpa = mpaRows.getString("mpa_name");
        }

        return nameOfMpa;
    }
}