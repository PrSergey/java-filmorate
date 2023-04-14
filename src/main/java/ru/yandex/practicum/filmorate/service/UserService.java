package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import ru.yandex.practicum.filmorate.constant.EventOperation;
import ru.yandex.practicum.filmorate.constant.EventType;
import ru.yandex.practicum.filmorate.model.EventUser;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.EventFeedStorage;
import ru.yandex.practicum.filmorate.storage.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final EventFeedStorage eventFeedStorage;
    private final FilmDbStorage filmDbStorage;
    private final JdbcTemplate jdbcTemplate;

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
        checkUserById(id, friendId);
        userStorage.makeFriends(id, friendId);
        EventUser eventUser = new EventUser(id, friendId, EventType.FRIEND, EventOperation.ADD);
        eventFeedStorage.setEventFeed(eventUser);
    }

    public void removeFriends(Long id, Long friendId) throws NotFoundException {
        checkUserById(id, friendId);
        userStorage.removeFriends(id, friendId);
        EventUser eventUser = new EventUser(id, friendId, EventType.FRIEND, EventOperation.REMOVE);
        eventFeedStorage.setEventFeed(eventUser);
    }

    public List<User> getAllFriends(Long id) throws NotFoundException {
        getById(id);
        List<User> friends = new ArrayList<>();
        Set<Long> friendsIds = userStorage.getUserFriendsById(id);
        if (friendsIds == null) {
            return friends;
        }
        for (Long friendId : friendsIds) {
            User friend = userStorage.getById(friendId);
            friends.add(friend);
        }
        return friends;
    }

    public void deleteUserById(Long userId) {
        userStorage.deleteUserById(userId);
    }

    private void checkUserById(Long id, Long friendId) {
        User user = getById(id);
        User friend = getById(friendId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id=" + id + " не существует");
        }
        if (friend == null) {
            throw new NotFoundException("Пользователь с id=" + id + " не существует");
        }
    }

    public List<EventUser>  getEventFeed(long userId){
        return eventFeedStorage.getEventFeed(userId);
    }

    public List<Film> getRecommendations(Long userId) {
        return filmDbStorage.getRecommendations(userId);
        }
}