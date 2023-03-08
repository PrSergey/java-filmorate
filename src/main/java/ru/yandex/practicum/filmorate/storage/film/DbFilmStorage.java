package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component("dbFilmStorage")
public class DbFilmStorage implements FilmStorage {
    private final static LocalDate BIRTH_OF_CINEMA = LocalDate.of(1895, 12, 28);
    private final JdbcTemplate jdbcTemplate;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    @Autowired
    public DbFilmStorage(JdbcTemplate jdbcTemplate, MpaStorage mpaStorage, GenreStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    @Override
    public List<Film> getAllFilms() {
        log.debug("Выдача всех фильмов из хранилища.");
        List<Film> films;

        String sql = " select * " +
                "from (select * from film left join mpa ON film.mpa=mpa.mpa_id) as f " +
                "left join (select film_id, COUNT(user_id) as c from likes group by film_id) as l ON f.film_id=l.film_id";
        films = jdbcTemplate.query(sql, this::mapRowToAllFilms);

        return genreStorage.setGenreToFilms(films);
    }

    @Override
    public Film getFilmById(Long id) {
        log.debug("Выдача фильма по id из хранилища.");
        List<Film> films;

        String sql = "select * from (select * from film " +
                "left join mpa ON film.mpa=mpa.mpa_id) as f " +
                "left join (select film_id, COUNT(user_id) as c " +
                "from likes group by film_id) as l ON f.film_id=l.film_id " +
                "where f.film_id = ?";
        films = jdbcTemplate.query(sql, this::mapRowToAllFilms, id);

        if (films.isEmpty()) {
            throw new ExistenceException("Данного фильма нет в базе.");
        }

        Film film = films.get(0);
        film.setGenres(genreStorage.getGenresOfFilm(id));

        return film;
    }

    @Override
    public Film createFilm(Film film) {
        log.debug("Создание фильма в хранилище.");
        if (validationFilm(film)) {
            throw new ValidationException("Некорректная дата релиза фильма.");
        }

        String sql = "insert into film (name, description, release_date, duration, mpa) values (?,?,?,?,?)";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getMpa().getId());
        String sqlGetId = "select film_id from film where name = ?";
        SqlRowSet getIdRow = jdbcTemplate.queryForRowSet(sqlGetId, film.getName());
        if (getIdRow.next()) {
            film.setId(getIdRow.getLong("film_id"));
        }

        if (film.getGenres() != null) {
            String sqlGenre = "insert into film_genre (genre_id, film_id) values (?,?)";
            for (Genre genreId : film.getGenres()) {
                jdbcTemplate.update(sqlGenre, genreId.getId(), film.getId());
            }
        } else {
            film.setGenres(new HashSet<>());
        }

        return film;
    }

    @Override
    public boolean validationFilm(Film film) {
        log.debug("Валидация даты релиза фильма.");
        return film.getReleaseDate().isBefore(BIRTH_OF_CINEMA);
    }

    @Override
    public Film updateFilm(Film film) {
        log.debug("Обновление фильма в хранилище.");
        if (validationFilm(film)) {
            throw new ValidationException("Некорректная дата релиза фильма.");
        }

        String sqlQuery = "update film set " +
                "name = ?, description = ?, release_date = ?, mpa = ?, duration = ? " +
                "where film_id = ?";
        int numberOfRowsAffected = jdbcTemplate.update(sqlQuery
                , film.getName()
                , film.getDescription()
                , film.getReleaseDate()
                , film.getMpa().getId()
                , film.getDuration()
                , film.getId());
        if (numberOfRowsAffected == 0) {
            throw new ExistenceException("При обновлении, фильм не найден в базе.");
        }

        if (film.getGenres() != null) {
            String sqlDeleteGenre = "delete from film_genre where film_id = ?";
            jdbcTemplate.update(sqlDeleteGenre, film.getId());
            String sqlGenre = "insert into film_genre (genre_id, film_id) values (?,?)";
            for (Genre genreId : film.getGenres()) {
                jdbcTemplate.update(sqlGenre, genreId.getId(), film.getId());
            }
        }

        return getFilmById(film.getId());
    }

    @Override
    public void addLikeByFilm(Long filmId, Long userId) {
        log.debug("Добавление лайка в хранилище.");

        String sqlCheckLikeFromUser = "select * from likes where film_id = ? and user_id = ?";
        SqlRowSet checkLikeRow = jdbcTemplate.queryForRowSet(sqlCheckLikeFromUser, filmId, userId);
        if (checkLikeRow.next()) {
            return;
        }

        String sqlAddLike = "insert into likes (film_id, user_id) values (?,?)";
        jdbcTemplate.update(sqlAddLike, filmId, userId);
    }

    @Override
    public boolean deleteLikeByFilm(Long filmId, Long userId) {
        log.debug("Удаление лайка в хранилище.");
        String sqlDeleteLike = "delete from likes where film_id = ? and user_id = ?";
        return jdbcTemplate.update(sqlDeleteLike, filmId, userId) >= 1;
    }

    @Override
    public List<Film> getTopFilm(Long countFilm) {
        log.debug("Выдача рейтинга фильмл");
        List<Film> topFilm;

        String sqlTopLike = "select * " +
                "from (select * from film left join mpa ON film.mpa=mpa.mpa_id) as f " +
                "left join (select film_id, COUNT(user_id) as c from likes group by film_id) as l ON f.film_id=l.film_id " +
                "order by c Desc limit ?";
        topFilm = jdbcTemplate.query(sqlTopLike, this::mapRowToAllFilms, countFilm);

        return topFilm;
    }

    @Override
    public Map<Long, Film> getFilms() {
        log.debug("Выдача всех фильмов с id в хранилище.");
        HashMap<Long, Film> films = new HashMap<>();

        for (Film film : getAllFilms()) {
            films.put(film.getId(), film);
        }

        return films;
    }

    private Film mapRowToAllFilms(ResultSet rs, int rowNum) throws SQLException {

        long id = rs.getLong("film_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        int mpa = rs.getInt("mpa"); //no
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        int duration = rs.getInt("duration");
        long likes = rs.getLong("c");
        HashSet<Genre> genres = new HashSet<>();

        return new Film(id, name, description, new Mpa(mpa, mpaStorage.getNameMpa(mpa)), releaseDate, duration,
                genres, likes);
    }
}