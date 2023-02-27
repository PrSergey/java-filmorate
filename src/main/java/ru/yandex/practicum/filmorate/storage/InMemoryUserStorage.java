package ru.yandex.practicum.filmorate.storage;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Data
@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private int id = 1;
    private Map<Long, User> users = new HashMap<>();

    public List<User> allUsers() {
        log.debug("Выдача всех пользователей из хранилища.");
        return new ArrayList<>(users.values());
    }

    public User createUser(User user) throws ValidationException {
        log.debug("Создание пользователя в хранилище.");
        if (validationUser(user)) {
            throw new ValidationException("В логине присутсвует пробел.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setFriends(new HashSet<>());
        user.setId(id++);
        users.put(user.getId(), user);
        return user;
    }

    public User updateUser(User user) throws ValidationException {
        log.debug("Обновление пользователя из хранилище.");
        if (validationUser(user)) {
            throw new ValidationException("В логине присутсвует пробел.");
        }
        if (!users.containsKey(user.getId())) {
            throw new ExistenceException("Пользователь с id " + user.getId() + " не найден.");
        }
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        users.put(user.getId(), user);
        return user;
    }

    public boolean validationUser(User user) {
        log.debug("Валидация логина пользователя на пробел.");
        return user.getLogin().contains(" ");

    }

    public Map<Long, User> getUsers() {
        return users;
    }
}
