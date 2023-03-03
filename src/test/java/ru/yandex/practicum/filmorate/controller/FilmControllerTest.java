package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserStorage userStorage;
    @Autowired
    private FilmStorage filmStorage;
    @Autowired
    private FilmService filmService;

    @Test
    @DisplayName("Test add film")
    public void addFilmTest() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Test name\", " +
                                "\"description\": \"Test description\", " +
                                "\"releaseDate\": \"1900-03-14\"," +
                                " \"duration\": \"100\"," +
                                " \"genres\": [{" +
                                " \"id\": 1," +
                                " \"name\": \"Комедия\"" +
                                " }]," +
                                " \"mpa\": {" +
                                " \"id\": 1," +
                                " \"name\": \"G\"" +
                                " }" +
                                "}"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").hasJsonPath())
                .andExpect(jsonPath("$.name").value("Test name"))
                .andExpect(jsonPath("$.description").value("Test description"))
                .andExpect(jsonPath("$.releaseDate").value("1900-03-14"))
                .andExpect(jsonPath("$.duration").value("100"))
                .andExpect(jsonPath("$.genres[0].id").value("1"))
                .andExpect(jsonPath("$.genres[0].name").value("Комедия"))
                .andExpect(jsonPath("$.mpa.id").value("1"))
                .andExpect(jsonPath("$.mpa.name").value("G"));
    }


    @Test
    @DisplayName("Test Valid date")
    public void addFilmCheckValidDateTest() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Test name\", " +
                                "\"description\": \"Test description\", " +
                                "\"releaseDate\": \"1800-03-14\"," +
                                " \"duration\": \"100\"}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Test valid name")
    public void addFilmCheckValidNameTest() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\", " +
                                "\"description\": \"Test description\", " +
                                "\"releaseDate\": \"1900-03-14\"," +
                                " \"duration\": \"100\"}"))
                .andExpect(status().is4xxClientError());

    }

    @Test
    @DisplayName("Test valid duration")
    public void addFilmCheckValidDurationTest() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Test name\", " +
                                "\"description\": \"Test description\", " +
                                "\"releaseDate\": \"1950-03-14\"," +
                                " \"duration\": \"-100\"}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Test valid description")
    public void addFilmCheckValidDescriptionTest() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Test name\", " +
                                "\"description\": \"Test description 200 aaaaaaaaaaaaaa" +
                                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                                "aaaaaaaaaaaaaaaaaaaaaaaa\", " +
                                "\"releaseDate\": \"1950-03-14\"," +
                                " \"duration\": \"100\"}"))
                .andExpect(status().is4xxClientError());
    }

   /* @Test
    @DisplayName("Test update film")
    public void updateFilmTest() throws Exception {
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 400, " +
                                "\"name\": \"Updated name\", " +
                                "\"description\": \"Updated description\", " +
                                "\"releaseDate\": \"1900-11-21\"," +
                                " \"duration\": \"100\"}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Test add like a non - existent film")
    public void shouldReturn404ThenAddScoreIfNoSuchFilm() throws Exception {
        User user = userStorage.add(User.builder().email("user@user.or").build());
        mockMvc.perform(put("/films/" + 777 + "/like/" + user.getId()))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Test add like")
    public void shouldReturn200ThenAddScoreIfNoSuchFilm() throws Exception {
        User user = userStorage.add(User.builder().email("user@user.or").build());
        Film film = filmStorage.add(Film.builder().build());
        mockMvc.perform(put("/films/" + film.getId() + "/like/" + user.getId()))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("Test remove like")
    void testRemoveLikeFromFilm() {
        Film film1 = filmStorage.add(Film.builder().build());
        User user1 = userStorage.add(User.builder().email("asd@sad.ru").build());
        filmService.addLike(film1.getId(), user1.getId());
        filmService.removeLike(film1.getId(), user1.getId());
        assertEquals(Collections.EMPTY_LIST, new ArrayList<>((Collection) filmStorage.getById(film1.getId())));
    }

    @Test
    @DisplayName("Test popular 1 film")
    void testGetPopularFilms() {
        Film film1 = filmStorage.add(Film.builder().id(1L).build());
        Film film2 = filmStorage.add(Film.builder().id(2L).build());
        User user1 = userStorage.add(User.builder().email("asd@sad.ru").build());
        filmService.addLike(film2.getId(), user1.getId());
        assertEquals(List.of(film2), filmService.getTop(1));
    }*/
}
