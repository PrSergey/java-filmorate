package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.webjars.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

   /* @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;

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
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Add friends")
    void testAddToFriends() {
        User user1 = userService.addUser(User.builder().email("asd@qw.re").build());
        User user2 = userService.addUser(User.builder().email("wsd@qw.re").build());

        userService.addFriends(user1.getId(), user2.getId());
        assertEquals(List.of(user2), userService.getUserFriends(user1.getId()));
        assertEquals(List.of(user1), userService.getUserFriends(user2.getId()));
    }

    @Test
    @DisplayName("Add non exist friend")
    void testAddToNonExistFriend() {
        User user1 = userService.add(User.builder().email("asd@qw.re").build());
        assertThrows(NotFoundException.class, () -> userService.makeFriends(user1.getId(), 20L));
    }

    @Test
    @DisplayName("Delete friend")
    void testRemoveFromFriends() {
        User user1 = userService.add(User.builder().email("asd@qw.re").build());
        User user2 = userService.add(User.builder().email("qwe@qw.re").build());
        userService.makeFriends(user1.getId(), user2.getId());
        userService.removeFriends(user1.getId(), user2.getId());
        assertEquals(Collections.EMPTY_LIST, userService.getAllFriends(user1.getId()));
    }

    @Test
    @DisplayName("Get common friends")
    void testGetCommonFriends() {
        User user1 = userService.add(User.builder().email("asd@qw.re").build());
        User user2 = userService.add(User.builder().email("qwe@qw.re").build());
        User user3 = userService.add(User.builder().email("ewq@qw.re").build());
        userService.makeFriends(user1.getId(), user2.getId());
        userService.makeFriends(user1.getId(), user3.getId());
        userService.makeFriends(user2.getId(), user3.getId());
        assertEquals(List.of(user1), userService.getAllFriends(user2.getId(), user3.getId()));
    }*/
}
