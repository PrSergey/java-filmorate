package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.webjars.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private static final String SELECT_ALL_QUERY =
            "SELECT m.id, " +
                    "m.name " +
                    "FROM mpa_ratings AS m;";

    private static final String SELECT_BY_ID_QUERY =
            "SELECT m.id, " +
                    "m.name " +
                    "FROM mpa_ratings AS m " +
                    "WHERE m.id = ?;";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> getAll() {
        return jdbcTemplate.query(SELECT_ALL_QUERY, (rs, rn) -> makeMpa(rs));
    }

    @Override
    public Mpa getById(Long id) throws NotFoundException {
        return jdbcTemplate.query(SELECT_BY_ID_QUERY, (rs, rn) -> makeMpa(rs), id)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Рейтинг с id=" + id + " не существует"));
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        return new Mpa(id, name);
    }
}
