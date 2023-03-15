package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
public class UserDbStorageTest {
    @Autowired
    private UserStorage userStorage;

    @Test
    void getAllTest() {
        User user1 = new User(null, "email1@example.com", "user1", "User 1", Date.valueOf("1990-01-01"), new HashSet<>());
        User user2 = new User(null, "email2@example.com", "user2", "User 2", Date.valueOf("1995-01-01"), new HashSet<>());
        User user3 = new User(null, "email3@example.com", "user3", "User 3", Date.valueOf("2000-01-01"), new HashSet<>());
        user1 = userStorage.add(user1);
        user2 = userStorage.add(user2);
        user3 = userStorage.add(user3);
        List<User> allUsers = userStorage.getAll();

        assertThat(allUsers)
                .hasSize(3)
                .extracting(User::getId)
                .contains(user1.getId(), user2.getId(), user3.getId());
    }

    @Test
    void getByIdUserTest() {
        User user = new User(null, "email@example.com", "user", "User", Date.valueOf("1990-01-01"), new HashSet<>());
        user = userStorage.add(user);

        User retrievedUser = userStorage.getById(user.getId());

        assertThat(retrievedUser)
                .isNotNull()
                .isEqualToComparingFieldByField(user);
    }

    @Test
    void getByIdNotFoundTest() {
        Long nonExistingId = Long.MAX_VALUE;

        assertThatThrownBy(() -> userStorage.getById(nonExistingId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь с id=" + nonExistingId + " не существует");
    }

    @Test
    void addNewUser() {
        User user = new User(null, "email@example.com", "user", "User", Date.valueOf("1990-01-01"), new HashSet<>());

        User addedUser = userStorage.add(user);

        assertThat(addedUser)
                .isNotNull()
                .isEqualToIgnoringGivenFields(user, "id");
        assertThat(addedUser.getId())
                .isNotNull()
                .isPositive();
    }

    @Test
    void updatesUser() {
        User user = new User(null, "email@example.com", "user", "User", Date.valueOf("1990-01-01"), new HashSet<>());
        user = userStorage.add(user);
        user.setEmail("new_email@example.com");
        user.setName("New Name");

        User updatedUser = userStorage.update(user);

        assertThat(updatedUser)
                .isNotNull()
                .isEqualToComparingFieldByField(user);
    }

    @Test
    void makeFriends() {
        User user1 = new User(null, "email1@example.com", "user1", "User 1", Date.valueOf("1990-01-01"), new HashSet<>());
        User user2 = new User(null, "email2@example.com", "user2", "User 2", Date.valueOf("1995-01-01"), new HashSet<>());
        user1 = userStorage.add(user1);
        user2 = userStorage.add(user2);

        userStorage.makeFriends(user1.getId(), user2.getId());
        Set<Long> friends = userStorage.getUserFriendsById(user1.getId());
        assertTrue(friends.contains(user2.getId()));
    }

    @Test
    void removeFriends() {
        User user1 = new User(null, "email1@example.com", "user1", "User 1", Date.valueOf("1990-01-01"), new HashSet<>());
        User user2 = new User(null, "email2@example.com", "user2", "User 2", Date.valueOf("1995-01-01"), new HashSet<>());
        user1 = userStorage.add(user1);
        user2 = userStorage.add(user2);

        userStorage.makeFriends(user1.getId(), user2.getId());
        userStorage.removeFriends(user1.getId(), user2.getId());
        Set<Long> friends = userStorage.getUserFriendsById(user1.getId());
        assertFalse(friends.contains(user2.getId()));
    }

}
