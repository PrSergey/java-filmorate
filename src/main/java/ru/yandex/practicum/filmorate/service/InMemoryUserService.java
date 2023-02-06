package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class InMemoryUserService implements UserService {
    UserStorage userInMemory;

    @Autowired
    public InMemoryUserService(UserStorage userInMemory) {
        this.userInMemory = userInMemory;
    }

    @Override
    public List<User> getAllUsers() {
        log.debug("Выдача всех пользователей из сервиса.");
        return userInMemory.allUsers();
    }

    @Override
    public User getUserById(Long id) {
        log.debug("Выдача пользователя из сервиса.");
        if (!userInMemory.getUsers().containsKey(id)) {
            throw new ExistenceException("Пользователь с id " + id + " не найден");
        }
        return userInMemory.getUsers().get(id);
    }

    @Override
    public User createUser(User user) {
        log.debug("Создание пользователя в сервисе.");
        return userInMemory.createUser(user);
    }

    @Override
    public User updateUser(User user) {
        log.debug("Обновление пользователя в сервисе.");
        return userInMemory.updateUser(user);
    }

    @Override
    public Long addFriend(Long userId, Long friendId) {
        log.debug("Добавление друга в сервисе.");
        getUserFriends(userId).add(friendId);
        getUserFriends(friendId).add(userId);
        return friendId;

    }

    @Override
    public Long deleteFriend(Long userId, Long friendId) {
        log.debug("Удаление друга в сервисе.");
        if (!getUserFriends(userId).contains(friendId)) {
            throw new ExistenceException("У пользователя с id " + userId + " нет друга с id " + friendId);
        } else {
            getUserFriends(userId).remove(friendId);
            getUserFriends(friendId).remove(userId);
            return friendId;
        }
    }

    @Override
    public List<User> getFriends(Long userId) throws ValidationException {
        log.debug("Выдача друзей в сервисе.");
        List<User> userFriends = new ArrayList<>();
        for (Long friend : getUserById(userId).getFriends()) {
            userFriends.add(getUserById(friend));
        }
        return userFriends;
    }

    @Override
    public List<User> getMutualFriends(Long userId, Long friendId) throws ValidationException {
        log.debug("Выдача общих друзей в сервисе.");
        List<User> mutualFriends = new ArrayList<>();
        if (getUserFriends(userId) == null) {
            return mutualFriends;
        }
        for (Long friends : getUserFriends(userId)) {
            if (getUserFriends(friendId).contains(friends)) {
                mutualFriends.add(getUserById(friends));
            }
        }

        return mutualFriends;
    }

    public Set<Long> getUserFriends(Long userId) throws ValidationException {
        if (!userInMemory.getUsers().containsKey(userId)) {
            throw new ExistenceException("Пользователь c id" + userId + " не найден в базе");
        } else {
            return userInMemory.getUsers().get(userId).getFriends();
        }
    }
}
