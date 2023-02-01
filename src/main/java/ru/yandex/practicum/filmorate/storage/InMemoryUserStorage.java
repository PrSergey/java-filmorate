package ru.yandex.practicum.filmorate.storage;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Component
public class InMemoryUserStorage implements UserStorage {

    private final static Logger log = LoggerFactory.getLogger(UserController.class);
    private int id = 1;



    private Map<Long, User> users = new HashMap<>();


    public List<User> allUsers() {
        return new ArrayList<>(users.values());
    }

    public User createUser(User user) throws ValidationException {
        if (validationUser(user)) {
            throw new ValidationException("В логине присутсвует пробел.");
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        user.setId(id++);
        users.put(user.getId(), user);
        return user;
    }

    public User updateUser(User user) throws ValidationException {
        if (validationUser(user)) {
            throw new ValidationException("В логине присутсвует пробел.");
        }
        if (!users.containsKey(user.getId())){
            throw new ValidationException("Пользователь с id "+user.getId()+" не найден.");
        }
        users.put(user.getId(), user);
        return user;
    }

    public boolean validationUser(User user) {
        return user.getLogin().contains(" ");

    }

    public Map<Long, User> getUsers() {
        return users;
    }
}
