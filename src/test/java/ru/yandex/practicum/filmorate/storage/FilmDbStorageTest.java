package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
public class FilmDbStorageTest {
    @Autowired
    private FilmStorage filmStorage;

    @Autowired
    private FilmService filmService;

    @Autowired
    private UserStorage userStorage;

    @Autowired
    private MpaStorage mpaStorage;


    @Test
    public void testGetAll() {
        List<Film> films = filmStorage.getAll();
        assertNotNull(films);
        assertFalse(films.isEmpty());
    }

    @Test
    public void testGetById() throws NotFoundException {
        filmStorage.add(Film.builder().name("test").description("test").releaseDate(Date.valueOf("2020-10-10"))
                .duration(100).mpa(mpaStorage.getById(1L)).build());
        Long id = 1L;
        Film film = filmStorage.getById(id);
        assertNotNull(film);
        assertEquals(id, film.getId());
    }

    @Test
    public void testDeleteById() {
        filmStorage.add(Film.builder().name("test").description("test").releaseDate(Date.valueOf("2020-10-10"))
                .duration(100).mpa(mpaStorage.getById(1L)).build());
        Long id = 1L;
        filmStorage.deleteFilmById(id);
        try {
            Film film = filmStorage.getById(id);
            Assertions.fail("Фильм не удален");
        } catch (NotFoundException e) {
            Assertions.assertEquals(e.getMessage(), "Фильм с id=" + id + " не существует");
        }
    }

    @Test
    public void testGetByIdNotFound() throws NotFoundException {
        assertThrows(NotFoundException.class, () -> filmStorage.getById(1000L));
    }

    @Test
    public void testAdd() {
        Film film = Film.builder().name("test").description("test").releaseDate(Date.valueOf("2020-10-10"))
                .duration(100).mpa(mpaStorage.getById(1L)).build();
        Film savedFilm = filmStorage.add(film);
        assertNotNull(savedFilm);
        assertNotNull(savedFilm.getId());
        assertEquals(film.getName(), savedFilm.getName());
        assertEquals(film.getDescription(), savedFilm.getDescription());
        assertEquals(film.getReleaseDate(), savedFilm.getReleaseDate());
        assertEquals(film.getDuration(), savedFilm.getDuration());
        assertEquals(film.getMpa().getId(), savedFilm.getMpa().getId());
    }

    @Test
    public void testUpdate() throws NotFoundException {
        Film film = Film.builder().name("test").description("test").releaseDate(Date.valueOf("2020-10-10"))
                .duration(100).mpa(mpaStorage.getById(1L)).build();
        Film savedFilm = filmStorage.add(film);
        String newName = "New film name";
        savedFilm.setName(newName);
        Film updatedFilm = filmStorage.update(savedFilm);
        assertNotNull(updatedFilm);
        assertEquals(newName, updatedFilm.getName());
    }

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

    @Test
    public void testAddLike() {
        User user = User.builder().email("test1@a.re").login("test").name("test").build();
        userStorage.add(user);

        Film film = Film.builder().name("test").releaseDate(Date.valueOf("2000-10-10")).duration(100)
                .description("123").mpa(mpaStorage.getById(1L)).build();
        filmService.add(film);

        assertFalse(filmStorage.hasLikeFromUser(film.getId(), user.getId()));

        filmService.addLike(film.getId(), user.getId());

        assertTrue(filmStorage.hasLikeFromUser(film.getId(), user.getId()));
    }

    @Test
    public void testRemoveLike() {
        User user = User.builder().email("test100@a.re").login("test10").name("test10").build();
        userStorage.add(user);

        Film film = Film.builder().name("test").releaseDate(Date.valueOf("2000-10-10")).duration(100)
                .description("123").mpa(mpaStorage.getById(1L)).build();
        filmService.add(film);

        assertFalse(filmStorage.hasLikeFromUser(film.getId(), user.getId()));

        filmService.addLike(film.getId(), user.getId());

        assertTrue(filmStorage.hasLikeFromUser(film.getId(), user.getId()));

        filmService.removeLike(film.getId(), user.getId());

        assertFalse(filmStorage.hasLikeFromUser(film.getId(), user.getId()));
    }

    @Test
    @Transactional
    public void testGetTop() {

        Film film1 = Film.builder().name("test1").releaseDate(Date.valueOf("2000-10-10"))
                .duration(100).description("123").mpa(mpaStorage.getById(1L)).build();
        filmService.add(film1);

        Film film2 = Film.builder().name("test2").releaseDate(Date.valueOf("2000-10-10"))
                .duration(100).description("222").mpa(mpaStorage.getById(2L)).build();
        filmService.add(film2);

        Film film3 = Film.builder().name("test3").releaseDate(Date.valueOf("2000-10-10"))
                .duration(100).description("321").mpa(mpaStorage.getById(3L)).build();
        filmService.add(film3);

        User user1 = User.builder().email("test11@a.re").login("test11").name("test11").build();
        userStorage.add(user1);

        User user2 = User.builder().email("test12@a.re").login("test12").name("test12").build();
        userStorage.add(user2);

        filmService.addLike(film1.getId(), user1.getId());
        filmService.addLike(film1.getId(), user2.getId());
        filmService.addLike(film2.getId(), user1.getId());

        film1 = filmService.getById(film1.getId());
        film2 = filmService.getById(film2.getId());

        List<Film> topFilms = filmService.getTop(2);

        film1 = filmService.getById(film1.getId());
        film2 = filmService.getById(film2.getId());

        assertEquals(2, topFilms.size());
        assertEquals(film1.getId(), topFilms.get(0).getId());
    }

    @Test
    public void searchTest() {

        Film film1 = Film.builder().name("test1").releaseDate(Date.valueOf("2000-10-10"))
                .duration(100).description("123").mpa(mpaStorage.getById(1L)).build();
        filmService.add(film1);

        Film film2 = Film.builder().name("test2").releaseDate(Date.valueOf("2000-10-10"))
                .duration(100).description("222").mpa(mpaStorage.getById(2L)).build();
        filmService.add(film2);

        Film film3 = Film.builder().name("test3").releaseDate(Date.valueOf("2000-10-10"))
                .duration(100).description("321").mpa(mpaStorage.getById(3L)).build();
        filmService.add(film3);

        List<Film> filmList = filmService.searchFilms("test", "G");

        assertFalse(filmList.isEmpty());
    }

    @Test
    public void testCommonFilm() {

        Film film1 = Film.builder().name("common").releaseDate(Date.valueOf("2000-10-10"))
                .duration(100).description("123").mpa(mpaStorage.getById(1L)).build();
        Long idFilm1 = filmService.add(film1).getId();

        User user1 = User.builder().email("test11@a.re").login("common").name("test11").build();
        Long idUser1 = userStorage.add(user1).getId();

        User user2 = User.builder().email("test1121@a.re").login("common2").name("test2").build();
        Long idUser2 = userStorage.add(user2).getId();

        filmStorage.addLike(idFilm1, idUser1);
        filmStorage.addLike(idFilm1, idUser2);
        assertEquals(filmStorage.getCommonFilm(idUser1, idUser2).get(0), filmStorage.getById(idFilm1));
        filmStorage.deleteFilmById(idFilm1);
        userStorage.deleteUserById(idUser1);
        userStorage.deleteUserById(idUser2);
    }
}
