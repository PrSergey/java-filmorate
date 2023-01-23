
package ru.yandex.practicum.filmorate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

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
        filmController = new FilmController();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void createFilmWithFailReleaseDate (){
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
    public void createFilmWithEmptyName (){
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
    public void createFilmWithFailDuration (){
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
    public void createFilmWithLongDescription (){
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
    public void createCorrectFilm (){
        Film film = Film.builder()
                .id(1)
                .name("Film")
                .description("description film")
                .releaseDate(LocalDate.of(1896, 12, 27))
                .duration(100)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
        filmController.createFilm(film);
        assertEquals(filmController.createFilm(film), film);
    }

}