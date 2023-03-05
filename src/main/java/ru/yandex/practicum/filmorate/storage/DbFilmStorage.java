package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
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
@Component
@Primary
public class DbFilmStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    final static LocalDate BIRTH_OF_CINEMA = LocalDate.of(1895, 12, 28);

    @Autowired
    public DbFilmStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> getAllFilms() {
        log.debug("Выдача всех фильмов из хранилища.");
        String sql = "select * from film";
        return jdbcTemplate.query(sql, this::mapRowToAllFilms);
    }

    @Override
    public Film getFilmById(Long id) {
        log.debug("Выдача фильма по id из хранилища.");
        if (id > countFilms()) {
            throw new ExistenceException("Данного фильма нет в базе.");
        }

        String sql = "select * from film where id = ?";
        return jdbcTemplate.query(sql, this::mapRowToAllFilms, id).get(0);
    }

    @Override
    public Film createFilm(Film film) {
        log.debug("Создание фильма в хранилище.");
        if (validationFilm(film)) {
            throw new ValidationException("Некорректная дата релиза фильма.");
        }
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }

        String sql = "insert into film (name, description, release_date, duration, mpa) values (?,?,?,?,?)";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getMpa().getId());
        film.setId(countFilms());

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

    public Long countFilms() {
        String sqlFilms = "SELECT COUNT(id) FROM film";
        SqlRowSet filmsRows = jdbcTemplate.queryForRowSet(sqlFilms);
        long countFilm = 0L;
        if (filmsRows.next()) {
            countFilm = filmsRows.getLong("COUNT(id)");
        }
        return countFilm;
    }

    @Override
    public Film updateFilm(Film film) {
        log.debug("Обновление фильма в хранилище.");
        if (validationFilm(film)) {
            throw new ValidationException("Некорректная дата релиза фильма.");
        }

        String sqlQuery = "update film set " +
                "name = ?, description = ?, release_date = ?, mpa = ?, duration = ? " +
                "where id = ?";
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
    public boolean addLikeByFilm(Long filmId, Long userId) {
        log.debug("Добавление лайка в хранилище.");
        String sqlAddLike = "insert into likes (film_id, user_id) values (?,?)";
        return jdbcTemplate.update(sqlAddLike, filmId, userId) > 0;
    }

    @Override
    public boolean deleteLikeByFilm(Long filmId, Long userId) {
        log.debug("Удаление лайка в хранилище.");
        String sqlDeleteLike = "delete from likes where film_id = ? and user_id = ?";
        return jdbcTemplate.update(sqlDeleteLike, filmId, userId) > 0;
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

        long id = rs.getLong("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        int mpa = rs.getInt("mpa"); //no
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        int duration = rs.getInt("duration");
        HashSet<Genre> genres = getGenresOfFilm(id);
        HashSet<Long> likes = getLikesByFilm(id);

        return new Film(id, name, description, new Mpa(mpa, getNameMpa(mpa)), releaseDate, duration, genres, likes);
    }

    private String getNameMpa(Integer id) {

        String nameOfMpa = "";

        String sql = "select name from mpa where id = ?";
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(sql, id);
        while (mpaRows.next()) {
            nameOfMpa = mpaRows.getString("name");
        }
        return nameOfMpa;
    }

    private HashSet<Genre> getGenresOfFilm(Long id) {

        HashSet<Genre> genresOfFilm = new HashSet<>();

        String sql = "select g.GENRE_ID, name\n" +
                "from film_genre AS fg\n" +
                "left join genre AS g ON fg.genre_id=g.genre_id" +
                " where film_id = ? order by g.GENRE_ID";
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sql, id);
        while (genreRows.next()) {
            Genre genre = new Genre(genreRows.getInt("genre_id"), genreRows.getString("name"));
            genresOfFilm.add(genre);
        }
        return genresOfFilm;
    }

    private HashSet<Long> getLikesByFilm(Long id) {

        HashSet<Long> likesByFilm = new HashSet<>();

        String sql = "select user_id from likes where film_id = ?";
        SqlRowSet likesRows = jdbcTemplate.queryForRowSet(sql, id);
        while (likesRows.next()) {
            likesByFilm.add(likesRows.getLong("user_id"));
        }
        return likesByFilm;
    }

    @Override
    public List<Genre> getAllGenre() {
        log.debug("Выдача всех жанров.");
        List<Genre> genres = new ArrayList<>();

        String sql = "select * from genre";
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sql);
        while (genreRows.next()) {
            Genre genre = new Genre(genreRows.getInt("genre_id"), genreRows.getString("name"));
            genres.add(genre);
        }
        return genres;
    }

    @Override
    public List<Mpa> getAllMpa() {
        log.debug("Выдача всех возрастных ограничений.");
        List<Mpa> mpas = new ArrayList<>();

        String sql = "select * from mpa";
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sql);
        while (genreRows.next()) {
            Mpa mpa = new Mpa(genreRows.getInt("id"), genreRows.getString("name"));
            mpas.add(mpa);
        }
        return mpas;
    }

}
