package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Component
@Primary
public class GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public GenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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

    HashSet<Genre> getGenresOfFilm(Long id) {

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

    public List<Film> setGenreToFilms(List<Film> films) {
        log.debug("Запись жанров ко всем фильмам.");
        SqlRowSet sqlRowSetGenres = jdbcTemplate.queryForRowSet("SELECT f.film_id,\n" +
                "       g.genre_id,\n" +
                "       g.name\n" +
                "FROM film AS f\n" +
                "INNER JOIN film_genre AS gf on f.film_id = gf.film_id\n" +
                "INNER JOIN GENRE AS g on gf.genre_id = g.genre_id");
        while (sqlRowSetGenres.next()) {
            for (Film film : films) {
                if (film.getId() == sqlRowSetGenres.getLong("film_id")) {
                    film.getGenres().add(new Genre(sqlRowSetGenres.getInt("genre_id"),
                            sqlRowSetGenres.getString("name")));
                }
            }
        }
        return films;
    }
}