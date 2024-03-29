package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.webjars.NotFoundException;
import ru.yandex.practicum.filmorate.constant.SortType;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

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

        film.setLikes(getAllLikes(id));
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
    public void deleteFilmById(Long filmId) {
        getById(filmId);
        String sqlQuery = "DELETE FROM films WHERE id = ?;";
        jdbcTemplate.update(sqlQuery, filmId);
    }


    @Override
    public void addLike(Long id, Long userId) {
        if(hasLikeFromUser(id, userId)){
            return;
        }
        String sqlQuery = "INSERT INTO likes_list (user_id, film_id) VALUES (?, ?);";
        jdbcTemplate.update(sqlQuery, userId, id);
    }

    @Override
    public void removeLike(Long id, Long userId) {
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
                "SELECT f.id," +
                        "f.name," +
                        "f.description," +
                        "f.release_date," +
                        "f.duration," +
                        "f.mpa_id," +
                        "m.name AS mpa_name," +
                        "COUNT(user_id)" +
                        "FROM films AS f " +
                        "JOIN mpa_ratings AS m ON m.id = f.mpa_id " +
                        "LEFT JOIN LIKES_LIST as ll ON f.ID = ll.FILM_ID " +
                        "group by f.id " +
                        "ORDER BY COUNT(user_id) DESC " +
                        "LIMIT ?;";
        List<Film> films = jdbcTemplate.query(sqlQuery, (rs, rn) -> makeFilm(rs), count);
        return getFilms(films);
    }

    @Override
    public List<Film> getCommonFilm(Long userId, Long friendId) {
        List<Film> commonFilm;
        List<Film> filmsByUser = new ArrayList<>();
        List<Film> filmsByFriends =  new ArrayList<>();

        String sql = "SELECT * from likes_list where user_id = ? OR user_id = ?";
        SqlRowSet filmWithLike = jdbcTemplate.queryForRowSet(sql, userId, friendId);
        while (filmWithLike.next()){
            Long filmId = filmWithLike.getLong("film_id");
            if (filmWithLike.getLong("user_id") == userId){
                filmsByUser.add(getById(filmId));
            }
            filmsByFriends.add(getById(filmId));
        }

        commonFilm = filmsByUser.stream().filter(filmsByFriends::contains)
                .collect(Collectors.toList());
        commonFilm.sort(Comparator.comparingInt(film -> film.getLikes().size()));
        return commonFilm;
    }

    @Override
    public List<Film> getPopularWithGenreAndYear(Integer count, Long genreId, Integer year) {
        List<Film> films = getTop(count);
        List<Film> filmsFiltered;
        if (genreId != 0 && year != 0) {
            filmsFiltered = filmFilteredWithGenre(films, genreId);
            return filmFilteredWithYear(filmsFiltered, year);
        } else if (year != 0) {
            return filmFilteredWithYear(films, year);
        } else if (genreId != 0) {
            return filmFilteredWithGenre(films, genreId);
        } else {
            return films;
        }
    }

    @Override
    public List<Film> getRecommendations(Long userId) {
        String sqlQueryUsersRecommendation = "SELECT user_id FROM likes_list WHERE film_id IN " +
                "(SELECT film_id FROM likes_list WHERE user_id = ?) AND user_id != ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlQueryUsersRecommendation, userId, userId);
        if (!sqlRowSet.next()) {
            return new ArrayList<>();
        } else {
            Long usersRecommendationId = sqlRowSet.getLong("user_id");
            String sqlQueryRecommendations = "SELECT f.*, mpa_id, mr.name FROM films AS f INNER JOIN likes_list AS l " +
                    "ON f.id = l.film_id INNER JOIN mpa_ratings AS mr ON f.mpa_id = mr.id WHERE l.film_id NOT IN " +
                    "(SELECT film_id FROM likes_list WHERE user_id = ?) AND l.user_id = ?";
            List<Film> films = jdbcTemplate.query(sqlQueryRecommendations, (rs, rowNum) ->
                    makeFilm(rs), userId, usersRecommendationId);
            return getFilms(films);
        }
    }

    @Override
    public List<Film> searchFilms(String query, List<String> searchBy) {
        String searchTerm = "%" + query + "%";
        String baseSqlQuery = "SELECT DISTINCT f.id, " +
                "f.name, " +
                "f.description, " +
                "f.release_date, " +
                "f.duration, " +
                "f.mpa_id, " +
                "r.rate, " +
                "m.name AS mpa_name " +
                "FROM films AS f " +
                "JOIN mpa_ratings AS m ON m.id = f.mpa_id " +
                "LEFT JOIN films_genres AS fg ON fg.film_id = f.id " +
                "LEFT JOIN genres AS g ON fg.genre_id = g.id " +
                "LEFT JOIN films_directors AS fd ON f.id = fd.film_id " +
                "LEFT JOIN directors AS d ON fd.director_id = d.id " +
                "LEFT JOIN (SELECT film_id, COUNT(user_id) rate FROM likes_list GROUP BY film_id) r ON f.id = r.film_id ";

        List<Object> queryParams = new ArrayList<>();
        StringBuilder whereClause = new StringBuilder();

        for (String searchParam : searchBy) {
            if (searchParam.contains("title")) {
                whereClause.append("LOWER(f.name) LIKE ? OR LOWER(f.description) LIKE ? OR ");
                queryParams.add(searchTerm);
                queryParams.add(searchTerm);
            }
            if (searchParam.contains("director")) {
                whereClause.append("LOWER(d.name) LIKE ? OR ");
                queryParams.add(searchTerm);
            }
        }

        if (whereClause.length() == 0) {
            return getTop(10);
        } else {
            whereClause = new StringBuilder(whereClause.substring(0, whereClause.length() - 4));
            String sqlQuery = baseSqlQuery + "WHERE " + whereClause + "ORDER BY r.rate DESC ";
            return getFilms(jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs), queryParams.toArray()));
        }
    }

    @Override
    public Set<Long> getAllLikes(Long id) {
        String sql = "SELECT user_id from likes_list where film_id = ?";
        List<Long> list = jdbcTemplate.queryForList(sql, Long.class, id);
        return new HashSet<>(list);
    }

    @Override
    public List<Film> getFilmsByDirectorId(Long id, SortType sortBy) {
        String sqlQuery;
        switch (sortBy) {
            case year:
                sqlQuery =
                        "SELECT f.id, " +
                                "f.name, " +
                                "f.description, " +
                                "f.release_date, " +
                                "f.duration, " +
                                "f.mpa_id, " +
                                "m.name AS mpa_name " +
                                "FROM films_directors AS fd " +
                                "JOIN MPA_ratings AS m" +
                                "    ON m.id = f.mpa_id " +
                                "JOIN films AS f" +
                                "    ON f.id = fd.film_id " +
                                "WHERE fd.director_id = ?" +
                                "ORDER BY f.release_date;";
                break;
            case likes:
                sqlQuery =
                        "SELECT f.id, " +
                                "f.name, " +
                                "f.description, " +
                                "f.release_date, " +
                                "f.duration, " +
                                "f.mpa_id, " +
                                "m.name AS mpa_name " +
                                "FROM films_directors AS fd " +
                                "JOIN MPA_ratings AS m" +
                                "    ON m.id = f.mpa_id " +
                                "JOIN films AS f" +
                                "    ON f.id = fd.film_id " +
                                "LEFT JOIN (SELECT film_id, " +
                                "      COUNT(user_id) rate " +
                                "      FROM likes_list " +
                                "      GROUP BY film_id " +
                                ") r ON fd.id = r.film_id " +
                                "WHERE fd.director_id = ?" +
                                "ORDER BY r.rate DESC ";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + sortBy);
        }
        List<Film> films = jdbcTemplate.query(sqlQuery, (rs, rn) -> makeFilm(rs), id);
        return getFilms(films);
    }

    public Film makeFilm(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        Date releaseDate = rs.getDate("release_date");
        int duration = rs.getInt("duration");
        Set<Long> likes = new HashSet<>();

        List<Genre> genres = new ArrayList<>();
        Mpa mpa = new Mpa(
                rs.getLong("mpa_id"),
                rs.getString("mpa_ratings.name")
        );
        List<Director> directors = new ArrayList<>();
        Film film = new Film(id, name, description, releaseDate, duration, genres, mpa, likes, directors);
        film.setDirectors(new ArrayList<>());
        return film;
    }

    public List<Film> getFilms(List<Film> films) {
        Map<Long, List<Genre>> genreMap = getGenresByFilmIds(films.stream().map(Film::getId).collect(Collectors.toList()));
        films.forEach(film -> film.setGenres(genreMap.get(film.getId())));
        films.forEach(film -> {
            if (film.getGenres() == null) film.setGenres(new ArrayList<>());
        });
        Map<Long, List<Director>> directorMap = getDirectorByFilmIds(films.stream().map(Film::getId).collect(Collectors.toList()));
        films.forEach(film -> film.setDirectors(directorMap.get(film.getId())));
        films.forEach(film -> {
            if (film.getDirectors() == null) film.setDirectors(new ArrayList<>());
        });
        return addLikesFromDb(films);
    }

    private List<Film> addLikesFromDb(List<Film> films){
        String sql = "SELECT user_id, film_id FROM likes_list";
        SqlRowSet likesRow = jdbcTemplate.queryForRowSet(sql);
        while (likesRow.next()) {
            for (Film film : films) {
                if(film.getId() == likesRow.getLong("film_id")){
                    film.getLikes().add(likesRow.getLong("user_id"));
                }
            }
        }
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

    private Map<Long, List<Director>> getDirectorByFilmIds(List<Long> filmIds) {
        String sqlQuery = "SELECT film_id, director_id, name FROM films_directors JOIN directors ON films_directors.director_id = directors.id WHERE film_id IN (:filmIds)";
        MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("filmIds", filmIds);
        return namedParameterJdbcTemplate.query(sqlQuery, parameters, rs -> {
            Map<Long, List<Director>> result = new HashMap<>();
            while (rs.next()) {
                Long filmId = rs.getLong("film_id");
                Long directorId = rs.getLong("director_id");
                String name = rs.getString("name");
                Director director = new Director(directorId, name);
                result.computeIfAbsent(filmId, k -> new ArrayList<>()).add(director);
            }
            return result;
        });
    }

    private List<Film> filmFilteredWithGenre(List<Film> films, Long genreId) {
        return films.stream()
                .filter(f -> f.getGenres().stream()
                        .anyMatch(g -> Objects.equals(g.getId(), genreId)))
                .collect(Collectors.toList());
    }

    private List<Film> filmFilteredWithYear(List<Film> filmsFiltered, Integer year) {
        return filmsFiltered.stream().filter(film -> Objects.equals(year, parseDate(film.getReleaseDate())))
                .collect(Collectors.toList());
    }

    private Integer parseDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }
}