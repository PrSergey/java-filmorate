package ru.yandex.practicum.filmorate.storage.user;

import org.webjars.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserStorage {

    List<User> getAll();

    User getById(Long id) throws NotFoundException;

    User add(User user);

    User update(User user);

    void deleteUserById(User user);

    void makeFriends(Long userId, Long friendId);

    void removeFriends(Long userId, Long friendId);

    Set<Long> getUserFriendsById(Long userId);
}
