package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.webjars.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreService genreService;

    private final UserStorage userStorage;

    @Override
    public List<Film> getAll() {
        String sqlQuery =
                "SELECT f.id, " +
                        "f.name, " +
                        "f.description, " +
                        "f.release_date, " +
                        "f.duration, " +
                        "f.mpa_id, " +
                        "m.name AS mpa_name " +
                        "FROM films AS f " +
                        "JOIN mpa_ratings AS m" +
                        "    ON m.id = f.mpa_id;";
        return jdbcTemplate.query(sqlQuery, (rs, rn) -> makeFilm(rs));
    }

    @Override
    public Film getById(Long id) throws NotFoundException {
        String sqlQuery =
                "SELECT f.id, " +
                        "f.name, " +
                        "f.description, " +
                        "f.release_date, " +
                        "f.duration, " +
                        "f.mpa_id, " +
                        "m.name AS mpa_name " +
                        "FROM films AS f " +
                        "JOIN mpa_ratings AS m" +
                        "    ON m.id = f.mpa_id " +
                        "WHERE f.id = ?;";
        return jdbcTemplate.query(sqlQuery, (rs, rn) -> makeFilm(rs), id)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не существует"));
    }

    @Override
    public Film add(Film film) {
        String sqlQuery = "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"id"});
            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setDate(3, film.getReleaseDate());
            statement.setLong(4, film.getDuration());
            statement.setLong(5, film.getMpa().getId());
            return statement;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return film;
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
        int updatedRows = jdbcTemplate.update(
                sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        if (updatedRows == 0) {
            throw new RuntimeException("Film with id " + film.getId() + " does not exist");
        }
        return getById(film.getId());
    }

    @Override
    public void delete(Film film) {
        String sqlQuery = "DELETE FROM films WHERE id = ?;";
        int rowsAffected = jdbcTemplate.update(sqlQuery, film.getId());
        if (rowsAffected == 0) {
            throw new RuntimeException("Фильм с id " + film.getId() + " не найден в базе данных");
        }
    }

    @Override
    public void addLike(Long id, Long userId) {
        User user = userStorage.getById(userId);
        if (user == null) {
            throw new RuntimeException("Пользователь с id " + userId + " не найден в базе данных");
        }
        String sqlQuery = "INSERT INTO likes_list (user_id, film_id) VALUES (?, ?);";
        jdbcTemplate.update(sqlQuery, userId, id);
    }

    @Override
    public void removeLike(Long id, Long userId) {
        User user = userStorage.getById(userId);
        if (user == null) {
            throw new RuntimeException("Пользователь с id " + userId + " не найден в базе данных");
        }
        String sqlQuery = "DELETE FROM likes_list WHERE film_id = ? AND user_id = ?;";
        jdbcTemplate.update(sqlQuery, id, userId);
    }

    @Override
    public boolean hasLikeFromUser(Long id, Long userId) {
        String sqlQuery = "SELECT COUNT(user_id) FROM likes_list WHERE film_id = ? AND user_id = ?;";
        int like = jdbcTemplate.queryForObject(sqlQuery, Integer.class, id, userId);
        return like != 0;
    }

    @Override
    public List<Film> getTop(Integer count) {
        String sqlQuery =
                "SELECT f.id, " +
                        "f.name, " +
                        "f.description, " +
                        "f.release_date, " +
                        "f.duration, " +
                        "f.mpa_id, " +
                        "m.name AS mpa_name " +
                        "FROM films AS f " +
                        "JOIN mpa_ratings AS m" +
                        "    ON m.id = f.mpa_id " +
                        "LEFT JOIN (SELECT film_id, " +
                        "      COUNT(user_id) rate " +
                        "      FROM likes_list " +
                        "      GROUP BY film_id " +
                        ") r ON f.id = r.film_id " +
                        "ORDER BY r.rate DESC " +
                        "LIMIT ?;";
        return jdbcTemplate.query(sqlQuery, (rs, rn) -> makeFilm(rs), count);
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        Date releaseDate = rs.getDate("release_date");
        int duration = rs.getInt("duration");
        Set<Genre> genres = genreService.getByFilmId(id);
        Mpa mpa = new Mpa(
                rs.getLong("mpa_id"),
                rs.getString("mpa_name")
        );
        return new Film(id, name, description, releaseDate, duration, genres, mpa);
    }
}
