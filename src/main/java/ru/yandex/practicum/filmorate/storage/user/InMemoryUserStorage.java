/*
package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import org.webjars.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    private Long id = 1L;

    private final Map<Long, User> users = new HashMap<>();


    @Override
    public User addUser(User user) {
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(id);
        user.setFriends(new HashSet<>());
        users.put(user.getId(), user);
        id++;

        return user;
    }

    @Override
    public User updateUser(User user) {
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (users.containsKey(user.getId())) {
            if (user.getFriends() == null || user.getFriends().isEmpty()) {
                user.setFriends(new HashSet<>());
            }
            users.put(user.getId(), user);
        } else {
            throw new NotFoundException("Такого ID нет");
        }
        return user;
    }

    @Override
    public void deleteUser(User user) {
        if (users.containsValue(user)) {
            users.remove(user.getId());
        } else {
            throw new NotFoundException("Такого пользователя нет");
        }

    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(Long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new NotFoundException("Такого ID нет");
        }

    }
}
*/
