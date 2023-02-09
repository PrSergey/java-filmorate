
package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.InMemoryFilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


class FilmControllerTest {
    static FilmController filmController;
    private Validator validator;

    @BeforeEach
    void beforeEach() {
        InMemoryFilmStorage inMemoryFilmStorage = new InMemoryFilmStorage();
        InMemoryFilmService inMemoryFilmService = new InMemoryFilmService(inMemoryFilmStorage);
        filmController = new FilmController(inMemoryFilmService);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public Film createFilm() {
        return Film.builder()
                .id(1)
                .name("Film")
                .description("description film")
                .releaseDate(LocalDate.of(1896, 12, 27))
                .duration(100)
                .build();
    }

    @Test
    public void createFilmWithFailReleaseDate() {
        Film film = Film.builder()
                .id(1)
                .name("Film")
                .description("Description film")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(100)
                .build();
        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> filmController.createFilm(film));
        Assertions.assertEquals("Некорректная дата релиза фильма.", exception.getMessage());
    }

    @Test
    public void createFilmWithEmptyName() {
        Film film = Film.builder()
                .id(1)
                .name("")
                .description("Description film")
                .releaseDate(LocalDate.of(1896, 12, 27))
                .duration(100)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void createFilmWithFailDuration() {
        Film film = Film.builder()
                .id(1)
                .name("Film")
                .description("Description film")
                .releaseDate(LocalDate.of(1896, 12, 27))
                .duration(-100)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void createFilmWithLongDescription() {
        String descriptionFilm = new String(new char[201]).replace('\0', ' ');
        Film film = Film.builder()
                .id(1)
                .name("Film")
                .description(descriptionFilm)
                .releaseDate(LocalDate.of(1896, 12, 27))
                .duration(100)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void createCorrectFilm() {
        Film film = createFilm();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
        filmController.createFilm(film);
        assertEquals(filmController.createFilm(film), film);
    }

    @Test
    public void updateFilmWithCorrectId() {
        Film film = createFilm();
        filmController.createFilm(film);
        assertEquals(film, filmController.getFilmById(1L));
        Film filmUpdate = Film.builder()
                .id(1)
                .name("Film")
                .description("description film")
                .releaseDate(LocalDate.of(1896, 12, 27))
                .duration(1000)
                .build();
        assertEquals(filmController.updateFilm(filmUpdate), filmController.getFilmById(1L));
    }

    @Test
    public void updateFilmWithIncorrectId() {
        Film film = createFilm();
        filmController.createFilm(film);
        assertEquals(film, filmController.getFilmById(1L));
        Film filmUpdate = Film.builder()
                .id(2)
                .name("Film")
                .description("description film")
                .releaseDate(LocalDate.of(1896, 12, 27))
                .duration(100)
                .build();
        ExistenceException exception = Assertions.assertThrows(ExistenceException.class,
                () -> filmController.updateFilm(filmUpdate));
        Assertions.assertEquals("При обновлении, фильм не найден в базе.", exception.getMessage());
    }

    @Test
    public void getFilmByNegativeId() {
        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> filmController.getFilmById(-1L));
        Assertions.assertEquals("Id не может быть отрицательным.", exception.getMessage());
    }

    @Test
    public void getFilmByIncorrectId() {
        Film film = createFilm();
        filmController.createFilm(film);
        ExistenceException exception = Assertions.assertThrows(ExistenceException.class,
                () -> filmController.getFilmById(2L));
        Assertions.assertEquals("Данного фильма нет в базе.", exception.getMessage());
    }

    @Test
    public void getTwoFilms() {
        Film film = createFilm();
        filmController.createFilm(film);
        Film filmTwo = createFilm();
        filmController.createFilm(filmTwo);
        Assertions.assertEquals(filmController.getAllFilms().size(), 2);
    }

    @Test
    public void addLikeToFilm() {
        Film film = createFilm();
        filmController.createFilm(film);
        filmController.addLike(1L, 1L);
        Assertions.assertEquals(filmController.getFilmById(1L).getLikes().size(), 1);
        filmController.addLike(1L, 2L);
        Assertions.assertEquals(filmController.getFilmById(1L).getLikes().size(), 2);
        filmController.addLike(1L, 1L);
        Assertions.assertEquals(filmController.getFilmById(1L).getLikes().size(), 2);
    }

    @Test
    public void deleteLike() {
        Film film = createFilm();
        filmController.createFilm(film);
        filmController.addLike(1L, 1L);
        Assertions.assertEquals(filmController.getFilmById(1L).getLikes().size(), 1);
        filmController.addLike(1L, 2L);
        Assertions.assertEquals(filmController.getFilmById(1L).getLikes().size(), 2);
        filmController.deleteLike(1L, 1L);
        Assertions.assertEquals(filmController.getFilmById(1L).getLikes().size(), 1);
    }

    @Test
    public void deleteLikeByIncorrectId() {
        Film film = createFilm();
        filmController.createFilm(film);
        filmController.addLike(1L, 1L);
        ExistenceException exception = Assertions.assertThrows(ExistenceException.class,
                () -> filmController.deleteLike(1L, 2L));
        Assertions.assertEquals("У фильма с id 1 нет лайка от пользователся с id 2", exception.getMessage());
    }

    @Test
    public void getPopularFilm() {
        Film filmFirst = createFilm();
        filmController.createFilm(filmFirst);
        Film filmSecond = createFilm();
        filmController.createFilm(filmSecond);
        filmController.addLike(1L, 1L);
        filmController.addLike(1L, 2L);
        filmController.addLike(1L, 5L);
        Assertions.assertEquals(filmController.getPopularFilms(1L).size(), 1);
        Assertions.assertEquals(filmController.getPopularFilms(1L).get(0), filmFirst);
    }

}