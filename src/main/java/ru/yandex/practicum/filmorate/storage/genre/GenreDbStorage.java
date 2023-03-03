package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.webjars.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage{

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getAll() {
        String sqlQuery =
                "SELECT g.id, " +
                        "g.name " +
                        "FROM genres AS g;";
        return jdbcTemplate.query(sqlQuery, (rs, rn) -> makeGenre(rs));
    }

    @Override
    public Genre getById(Long id) throws NotFoundException {
        String sqlQuery =
                "SELECT g.id, " +
                        "g.name " +
                        "FROM genres AS g " +
                        "WHERE g.id = ?;";
        return jdbcTemplate.query(sqlQuery, (rs, rn) -> makeGenre(rs), id)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Жанр с id=" + id + " не существует"));
    }

    @Override
    public List<Genre> getByFilmId(Long filmId) throws NotFoundException {
        String sqlQuery =
                "SELECT g.id, " +
                        "g.name " +
                        "FROM films_genres AS fg " +
                        "JOIN genres AS g ON fg.genre_id = g.id " +
                        "WHERE fg.film_id = ?;";
        return jdbcTemplate.query(sqlQuery, (rs, rn) -> makeGenre(rs), filmId);
    }

    @Override
    public void addAllToFilmId(Long filmId, List<Genre> genres) {
        List<Genre> genresDistinct = genres.stream().distinct().collect(Collectors.toList());
        jdbcTemplate.batchUpdate(
                "INSERT INTO films_genres (genre_id, film_id) VALUES (?, ?);",
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement statement, int i) throws SQLException {
                        statement.setLong(1, genresDistinct.get(i).getId());
                        statement.setLong(2, filmId);
                    }
                    public int getBatchSize() {
                        return genresDistinct.size();
                    }
                }
        );
    }

    @Override
    public void deleteAllByFilmId(Long filmId) {
        String sqlQuery = "DELETE FROM films_genres WHERE film_id = ?;";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        return new Genre(id, name);
    }
}
