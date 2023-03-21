package ru.yandex.practicum.filmorate.storage.director;


import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.webjars.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Director> getAll() {
        String sql =
                "select d.id," +
                        "d.name " +
                        "from directors as d;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs));
    }

    @Override
    public Director getById(Long id) throws NotFoundException {
        String sql =
                "select d.id," +
                        "d.name " +
                        "from directors as d " +
                        "where d.id = ?;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs), id)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Ружиссёра с id " + id + "не существует"));
    }

    @Override
    public List<Director> getByFilmId(Long filmId) throws NotFoundException {
        String sql =
                "select d.id," +
                        "d.name " +
                        "from films_directors as fd " +
                        "join directors as d on fd.director_id = d.id " +
                        "where fd.film_id = ?;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs), filmId);
    }

    @Override
    public Director add(Director director) {
        String sqlQuery = "INSERT INTO directors (name) VALUES (?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"id"});
            statement.setString(1, director.getName());
            return statement;
        }, keyHolder);
        director.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return director;
    }

    @Override
    public Director update(Director director) {
        String sql = "update directors set name = ? where id = ?;";
        jdbcTemplate.update(sql, director.getName(), director.getId());
        return getById(director.getId());
    }

    @Override
    public void delete(Long id) {
        String sql = "delete from directors where id = ?;";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void addAllToFilmId(Long filmId, List<Director> directors) {
        List<Director> directorsDistinct = directors.stream().distinct().collect(Collectors.toList());
        for (Director director : directorsDistinct) {
            getById(director.getId());
        }
        jdbcTemplate.batchUpdate(
                "INSERT INTO films_directors (director_id, film_id) VALUES (?, ?);",
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement statement, int i) throws SQLException {
                        statement.setLong(1, directorsDistinct.get(i).getId());
                        statement.setLong(2, filmId);
                    }

                    public int getBatchSize() {
                        return directorsDistinct.size();
                    }
                }
        );
    }

    @Override
    public void deleteAllByFilmId(Long filmId) {
        String sqlQuery = "DELETE FROM films_directors WHERE film_id = ?;";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        return new Director(id, name);
    }

}