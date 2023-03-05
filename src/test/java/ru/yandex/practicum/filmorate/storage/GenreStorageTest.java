package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.webjars.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
public class GenreStorageTest {
    @Autowired
    private GenreStorage genreStorage;
    @Autowired
    private FilmStorage filmStorage;

    @Test
    void getByIdValidGenre() {
        Optional<Genre> genreOptional = Optional.ofNullable(genreStorage.getById(1L));
        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    void getByIdNotValidId() {
        assertThrows(NotFoundException.class, () -> genreStorage.getById(10L));
    }

    @Test
    void getAllExecuteItems() {
        List<Genre> genres = genreStorage.getAll();
        assertThat(genres).hasSize(6);
    }

    @Test
    void getByFilmId_filmIdCorrectGenres() {
        Film film = Film.builder()
                .name("MyName")
                .description("my description")
                .duration(150)
                .releaseDate(Date.valueOf("1999-01-01"))
                .mpa(Mpa.builder().id(1L).build())
                .build();
        Long filmId = filmStorage.add(film).getId();
        List<Genre> testGenres = genreStorage.getAll().subList(0,5);
        genreStorage.addAllToFilmId(filmId, testGenres);
        List<Genre> genres = genreStorage.getByFilmId(filmId);
        assertThat(genres).hasSize(5).containsAll(testGenres);
    }

}
