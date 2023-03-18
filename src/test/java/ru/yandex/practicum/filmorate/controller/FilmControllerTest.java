package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
public class FilmControllerTest {
    private final MockMvc mockMvc;

    @Autowired
    public FilmControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    public void addAndReturnFilm() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"test\", " +
                                "\"description\": \"test\", " +
                                "\"releaseDate\": \"1975-03-14\"," +
                                "\"duration\": 90," +
                                "\"mpa\": {\"id\": 1}}"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").hasJsonPath())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("test"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("test"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.releaseDate").value("1975-03-14"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.duration").value(90));
    }

    @Test
    public void returnNoName() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\": \"test\", " +
                                "\"releaseDate\": \"1975-03-14\"," +
                                "\"duration\": 90," +
                                "\"mpa\": {\"id\": 1}}"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void returnErrorIfBlankName() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"   \", " +
                                "\"description\": \"test\", " +
                                "\"releaseDate\": \"1975-03-14\"," +
                                "\"duration\": 90," +
                                "\"mpa\": {\"id\": 1}}"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void errorIfDescriptionLengthIs201() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"test\", " +
                                "\"description\": \"aaaaa aaaaa aaaaa aaa aaa, " +
                                "aaaaaaaaaaa aaaaaaaaaa aaaa, aaa aa aaaaaaa aaaaaa " +
                                "aaaaaaaaaa aa aaaaaa aa aaaaaa aaaaa aaaaaa. aa aaaa " +
                                "aa aaaaa aaaaaa, aaaa aaaaaaa aaaaaaaaaaa aaaaaaa " +
                                "aaaaaaa aaaaa aaaa aa\", " +
                                "\"releaseDate\": \"1975-03-14\"," +
                                "\"duration\": 90," +
                                "\"mpa\": {\"id\": 1}}"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void addIfDescriptionLengthIs200() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"test\", " +
                                "\"description\": \"aaaaa aaaaa aaaaa aaa aaa, " +
                                "aaaaaaaaaaa aaaaaaaaaa aaaa, aaa aa aaaaaaa aaaaaa " +
                                "aaaaaaaaaa aa aaaaaa aa aaaaaa aaaaa aaaaaa. aa aaaa " +
                                "aa aaaaa aaaaaa, aaaa aaaaaaa aaaaaaaaaaa aaaaaaa " +
                                "aaaaaaa aaaaa aa aa\", " +
                                "\"releaseDate\": \"1975-03-14\"," +
                                "\"duration\": 90," +
                                "\"mpa\": {\"id\": 1}}"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @Test
    public void errorIfNoReleaseDate() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"test\", " +
                                "\"description\": \"test\", " +
                                "\"duration\": 90," +
                                "\"mpa\": {\"id\": 1}}"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void errorIfReleaseDateBefore1895_12_28() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"test\", " +
                                "\"description\": \"test\", " +
                                "\"releaseDate\": \"1895-12-27\"," +
                                "\"duration\": 90," +
                                "\"mpa\": {\"id\": 1}}"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void errorIfNegativeDuration() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"test\", " +
                                "\"description\": \"test\", " +
                                "\"releaseDate\": \"1975-03-14\", " +
                                "\"duration\": -1," +
                                "\"mpa\": {\"id\": 1}}"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void updateFilm() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1, " +
                                "\"name\": \"Updated name\", " +
                                "\"description\": \"Updated description\", " +
                                "\"releaseDate\": \"2000-03-14\"," +
                                "\"duration\": 191," +
                                "\"mpa\": {\"id\": 1}}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Updated name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Updated description"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.releaseDate").value("2000-03-14"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.duration").value(191));
    }
}
