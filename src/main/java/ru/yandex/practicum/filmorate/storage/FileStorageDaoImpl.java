package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
@Primary
public class FileStorageDaoImpl implements FilmStorage{

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FileStorageDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public List<Film> allFilms() {
        String sql = "select * from film";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public Film getFilmById(Long id) {
        return null;
    }

    @Override
    public Film createFilm(Film film) {
        return null;
    }

    @Override
    public Film updateFilm(Film film) {
        return null;
    }

    @Override
    public boolean validationFilm(Film film) {
        return false;
    }

    @Override
    public Map<Long, Film> getFilms() {
        return null;
    }
    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {

        long id = rs.getLong("film_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        String mpa = rs.getString("mpa");
        LocalDate releaseDate = rs.getDate("releaseDate").toLocalDate();
        int duration = rs.getInt("duration");
        HashSet<Long> likes=getLikesByFilm(id);

        return new Film(id, name, description, mpa, releaseDate, duration, likes);
    }

    private HashSet<Long> getLikesByFilm(Long id) throws SQLException {

        HashSet<Long> likesByFilm = new HashSet<>();

        String sql = "select user_id from likes where film_id = ?";
        SqlRowSet likesRows = jdbcTemplate.queryForRowSet(sql, id);
        if(likesRows.next()) {
            likesByFilm.add(likesRows.getLong("user_id"));
        }

        return likesByFilm;
    }

}
