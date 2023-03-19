package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void addUser() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"test@test.com\", " +
                                "\"login\": \"test\", " +
                                "\"name\": \"test\"," +
                                "\"birthday\": \"1988-04-01\"}"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").hasJsonPath())
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.login").value("test"))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.birthday").value("1988-04-01"));
    }

    @Test
    public void addUser_whereLoginEqualsName() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"test11@test.com\", " +
                                "\"login\": \"test-1\", " +
                                "\"birthday\": \"1988-04-01\"}"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.login").value("test-1"))
                .andExpect(jsonPath("$.name").value("test-1"));
    }

    @Test
    public void errorIfInvalidEmail() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"test\", " +
                                "\"login\": \"test\", " +
                                "\"name\": \"test\"," +
                                "\"birthday\": \"1988-04-01\"}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void errorIfNoLogin() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"test\"," +
                                "\"birthday\": \"1988-04-01\"}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void errorIfInvalidLogin() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"test@test.com\", " +
                                "\"login\": \"test test\", " +
                                "\"name\": \"test\"," +
                                "\"birthday\": \"1988-04-01\"}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void errorIfInvalidBirthday() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"test@test.com\", " +
                                "\"login\": \"test\", " +
                                "\"name\": \"test\"," +
                                "\"birthday\": \"2088-04-01\"}"))
                .andExpect(status().is4xxClientError());
    }

}
