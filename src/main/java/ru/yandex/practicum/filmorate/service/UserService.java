package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User getById(Long id) {
        return userStorage.getById(id);
    }

    public User add(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.add(user);
    }

    public User update(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        return userStorage.update(user);
    }

    public void makeFriends(Long id, Long friendId) throws NotFoundException {
        userStorage.makeFriends(id, friendId);
    }

    public void removeFriends(Long id, Long friendId) throws NotFoundException {
        User user = getById(id);
        User friend = getById(friendId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id=" + id + " не существует");
        }
        if (friend == null) {
            throw new NotFoundException("Пользователь с id=" + id + " не существует");
        }
        userStorage.removeFriends(id, friendId);
    }

    public List<User> getAllFriends(Long id) throws NotFoundException {
        List<User> friends = new ArrayList<>();
        List<Long> friendsIds = userStorage.getUserFriendsById(id);
        if (friendsIds == null) {
            return friends;
        }
        for (Long friendId : friendsIds) {
            User friend = userStorage.getById(friendId);
            friends.add(friend);
        }
        return friends;
    }
}
