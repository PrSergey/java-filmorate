package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.webjars.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
public class UserDbStorageTest {
    @Autowired
    private UserStorage userStorage;

    @Test
    void getByIdValidUser() {
        User testUser = User.builder()
                .email("test@ya.ru")
                .login("test")
                .name("test")
                .birthday(Date.valueOf("1990-10-10"))
                .build();

        Long userId = userStorage.add(testUser).getId();

        Optional<User> userOptional = Optional.ofNullable(userStorage.getById(userId));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", userId)
                );
    }

    @Test
    void getByIdNotValidId() {
        assertThrows(NotFoundException.class, () -> userStorage.getById(100L));
    }
}
