package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Test create user")
    public void addUserTest() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"test@test.com\", " +
                                "\"login\": \"test\", " +
                                "\"name\": \"test\"," +
                                "\"birthday\": \"2000-10-20\"}"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").hasJsonPath())
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.login").value("test"))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.birthday").value("2000-10-20"));
    }

    @Test
    @DisplayName("Test email")
    public void addUserCheckValidEmailTest() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"testtest.com\", " +
                                "\"login\": \"test\", " +
                                "\"name\": \"test\"," +
                                "\"birthday\": \"2000-10-20\"}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Test login")
    public void addUserCheckValidLoginTest() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"test@test.com\", " +
                                "\"login\": \" \", " +
                                "\"name\": \"test\"," +
                                "\"birthday\": \"2000-10-20\"}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Test birthday")
    public void addUserCheckValidBirthdayTest() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"test@test.com\", " +
                                "\"login\": \"test\", " +
                                "\"name\": \"test\"," +
                                "\"birthday\": \"2900-10-20\"}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Test update user")
    public void updateUserTest() throws Exception {
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1000, " +
                                "\"email\": \"updateTest@mas.rr\", " +
                                "\"login\": \"updateTest\", " +
                                "\"name\": \"updated name\"," +
                                "\"birthday\": \"2000-10-20\"}"))
                .andExpect(status().isBadRequest());
    }
}
