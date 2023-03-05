package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.PersonController;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Person;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.FilmServiceImp;
import ru.yandex.practicum.filmorate.service.PersonService;
import ru.yandex.practicum.filmorate.service.PersonServiceImp;
import ru.yandex.practicum.filmorate.storage.DbFilmStorage;
import ru.yandex.practicum.filmorate.storage.DbPersonStorage;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

//@SpringBootTest
//@AutoConfigureTestDatabase
//@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DbFilmStorageTest extends FilmControllerTest {
//
//
//    private final DbFilmStorage filmStorage;
//    Validator validator;
//    private final DbPersonStorage personStorage;
//
//    @Override
//    @BeforeEach
//    void beforeEach() {
//        FilmService filmInMemory = new FilmServiceImp(filmStorage);
//        filmController = new FilmController(filmInMemory);
//        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
//        super.validator = factory.getValidator();
//    }
//
//    @BeforeEach
//    void beforeEachUser() {
//        PersonService userInMemory = new PersonServiceImp(personStorage);
//        PersonController personController = new PersonController(userInMemory);
//        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
//        super.validator = factory.getValidator();
//        personController.createPerson(createUser());
//    }
//
//    public Person createUser(){
//        return  Person.builder()
//                .id(1)
//                .login("Login")
//                .email("asdfe@mail.com")
//                .name("Name")
//                .birthday(LocalDate.of(2022, 12, 20))
//                .friends(new HashSet<>())
//                .build();
//    }
//
//    @Test
//    @Override
//    public Film createFilm() {
//        return super.createFilm();
//    }
//
//    @Test
//    @Override
//    public void createFilmWithFailReleaseDate() {
//        super.createFilmWithFailReleaseDate();
//    }
//
//    @Test
//    @Override
//    public void createFilmWithEmptyName() {
//        super.createFilmWithEmptyName();
//    }
//
//    @Test
//    @Override
//    public void createFilmWithFailDuration() {
//        super.createFilmWithFailDuration();
//    }
//
//    @Test
//    @Override
//    public void createFilmWithLongDescription() {
//        super.createFilmWithLongDescription();
//    }
//
//    @Test
//    @Override
//    public void createCorrectFilm() {
//        super.createCorrectFilm();
//    }
//
//    @Test
//    @Override
//    public void updateFilmWithCorrectId() {
//        Film film = createFilm();
//        filmController.createFilm(film);
//        Film filmUpdate = Film.builder()
//                .id(1)
//                .name("Film")
//                .description("description film")
//                .releaseDate(LocalDate.of(1896, 12, 27))
//                .duration(1000)
//                .mpa(new Mpa(2,"new"))
//                .genres(new HashSet<>())
//                .likes(new HashSet<>())
//                .build();
//        assertEquals(filmController.updateFilm(filmUpdate), filmController.getFilmById(1L));
//    }
//
//    @Test
//    @Override
//    public void updateFilmWithIncorrectId() {
//        Film film = createFilm();
//        filmController.createFilm(film);
//        Film filmUpdate = Film.builder()
//                .id(50)
//                .name("Film")
//                .description("description film")
//                .releaseDate(LocalDate.of(1896, 12, 27))
//                .duration(100)
//                .mpa(new Mpa(2,"new"))
//                .genres(new HashSet<>())
//                .likes(new HashSet<>())
//                .build();
//        ExistenceException exception = Assertions.assertThrows(ExistenceException.class,
//                () -> filmController.updateFilm(filmUpdate));
//        Assertions.assertEquals("При обновлении, фильм не найден в базе.", exception.getMessage());
//    }
//
//    @Test
//    @Override
//    public void getFilmByNegativeId() {
//        super.getFilmByNegativeId();
//    }
//
//    @Test
//    @Override
//    public void getFilmByIncorrectId() {
//        Film film = createFilm();
//        filmController.createFilm(film);
//        ExistenceException exception = Assertions.assertThrows(ExistenceException.class,
//                () -> filmController.getFilmById(50L));
//        Assertions.assertEquals("Данного фильма нет в базе.", exception.getMessage());
//    }
//
//    @Test
//    public void getSixFilms() {
//        Film film = createFilm();
//        filmController.createFilm(film);
//        Film filmTwo = createFilm();
//        filmController.createFilm(filmTwo);
//        Assertions.assertEquals(filmController.getAllFilms().size(), 6);
//    }
//
//    @Test
//    @Override
//    public void addLikeToFilm() {
//        super.addLikeToFilm();
//    }
//
//    @Test
//    @Override
//    public void deleteLike() {
//        Film film = createFilm();
//        filmController.createFilm(film);
//        filmController.addLike(1L, 1L);
//        Assertions.assertEquals(filmController.getFilmById(1L).getLikes().size(), 3);
//        filmController.addLike(1L, 2L);
//        Assertions.assertEquals(filmController.getFilmById(1L).getLikes().size(), 3);
//        filmController.deleteLike(1L, 1L);
//        Assertions.assertEquals(filmController.getFilmById(1L).getLikes().size(), 2);
//    }
//
//    @Test
//    @Override
//    public void deleteLikeByIncorrectId() {
//        super.deleteLikeByIncorrectId();
//    }
//
//    @Test
//    @Override
//    public void getPopularFilm() {
//        Film filmFirst = createFilm();
//        filmController.createFilm(filmFirst);
//        Film filmSecond = createFilm();
//        filmController.createFilm(filmSecond);
//        filmController.addLike(1L, 1L);
//        filmController.addLike(1L, 2L);
//        filmController.addLike(1L, 5L);
//        Assertions.assertEquals(filmController.getPopularFilms(1L).size(), 1);
//        Assertions.assertEquals(filmController.getPopularFilms(1L).get(0).getLikes().size(), 3);
//    }

}
