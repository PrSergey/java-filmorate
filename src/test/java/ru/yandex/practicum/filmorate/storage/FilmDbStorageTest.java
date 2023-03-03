package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.webjars.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
public class FilmDbStorageTest {
    @Autowired
    private FilmStorage filmStorage;

    @Test
    void getByIdValidFilm() {
        Film testFilm = Film.builder()
                .name("test")
                .description("test")
                .duration(100)
                .releaseDate(Date.valueOf("1990-10-10"))
                .mpa(Mpa.builder().id(1L).build())
                .build();

        Long filmId = filmStorage.add(testFilm).getId();

        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getById(filmId));
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", filmId)
                );
    }

    @Test
    void getByIdNotValid() {
        assertThrows(NotFoundException.class, () -> filmStorage.getById(100L));
    }

}
