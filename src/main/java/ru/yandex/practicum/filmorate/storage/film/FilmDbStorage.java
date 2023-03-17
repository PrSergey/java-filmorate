package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Not;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.webjars.NotFoundException;
import ru.yandex.practicum.filmorate.constant.EventOperation;
import ru.yandex.practicum.filmorate.constant.EventType;
import ru.yandex.practicum.filmorate.model.EventUser;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.eventFeed.EventFeedDBStorage;
import ru.yandex.practicum.filmorate.storage.eventFeed.EventFeedDBStorageImp;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final GenreStorage genreStorage;
    private final EventFeedDBStorage eventFeedDBStorage;

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
        List<Film> films = jdbcTemplate.query(sqlQuery, (rs, rn) -> makeFilm(rs));
        return getFilms(films);
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
        Film film = jdbcTemplate.query(sqlQuery, (rs, rn) -> makeFilm(rs), id)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не существует"));
        film.setGenres(genreStorage.getByFilmId(id));
        return film;
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
        if (film.getGenres() == null) {
            film.setGenres(new ArrayList<>());
        }
        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return film;
    }

    @Override
    public Film update(Film film) {
        getById(film.getId());
        String sqlQuery = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
        jdbcTemplate.update(
                sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        return getById(film.getId());
    }

    @Override
    public void delete(Film film) {
        getById(film.getId());
        String sqlQuery = "DELETE FROM films WHERE id = ?;";
        jdbcTemplate.update(sqlQuery, film.getId());
    }

    @Override
    public void deleteFilmById(Long filmId) {
        getById(filmId);
        String sqlQuery = "DELETE FROM films WHERE id = ?;";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    @Override
    public void addLike(Long id, Long userId) {
        String sqlQuery = "INSERT INTO likes_list (user_id, film_id) VALUES (?, ?);";
        jdbcTemplate.update(sqlQuery, userId, id);
        EventUser eventUser = new EventUser(userId, id, EventType.LIKE, EventOperation.ADD);
        eventFeedDBStorage.setEventFeed(eventUser);
    }

    @Override
    public void removeLike(Long id, Long userId) {
        String sqlQuery = "DELETE FROM likes_list WHERE film_id = ? AND user_id = ?;";
        EventUser eventUser = new EventUser(userId, id, EventType.LIKE, EventOperation.REMOVE);
        eventFeedDBStorage.setEventFeed(eventUser);
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
                        "f." +
                        "description, " +
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
        List<Film> films = jdbcTemplate.query(sqlQuery, (rs, rn) -> makeFilm(rs), count);
        return getFilms(films);
    }

    @Override
    public Set<Long> getAllLikes(Long id) {
        String sql = "SELECT user_id from likes_list where film_id = ?";
        List<Long> list = jdbcTemplate.queryForList(sql, Long.class, id);
        return new HashSet<>(list);
    }

    public Film makeFilm(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        Date releaseDate = rs.getDate("release_date");
        int duration = rs.getInt("duration");
        Set<Long> likes = getAllLikes(id);
        List<Genre> genres = genreStorage.getByFilmId(rs.getLong("id"));
        Mpa mpa = new Mpa(
                rs.getLong("mpa_id"),
                rs.getString("mpa_ratings.name")
        );
        return new Film(id, name, description, releaseDate, duration, genres, mpa, likes);
    }

    private List<Film> getFilms(List<Film> films) {
        Map<Long, List<Genre>> genreMap = getGenresByFilmIds(films.stream().map(Film::getId).collect(Collectors.toList()));
        films.forEach(film -> film.setGenres(genreMap.get(film.getId())));
        films.forEach(film -> {
            if (film.getGenres() == null) film.setGenres(new ArrayList<>());
        });
        return films;
    }

    private Map<Long, List<Genre>> getGenresByFilmIds(List<Long> filmIds) {
        String sqlQuery = "SELECT film_id, genre_id, name FROM films_genres JOIN genres ON genres.id = films_genres.genre_id WHERE film_id IN (:filmIds)";
        MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("filmIds", filmIds);
        return namedParameterJdbcTemplate.query(sqlQuery, parameters, rs -> {
            Map<Long, List<Genre>> result = new HashMap<>();
            while (rs.next()) {
                Long filmId = rs.getLong("film_id");
                Long genreId = rs.getLong("genre_id");
                String name = rs.getString("name");
                Genre genre = new Genre(genreId, name);
                result.computeIfAbsent(filmId, k -> new ArrayList<>()).add(genre);
            }
            return result;
        });
    }
}
