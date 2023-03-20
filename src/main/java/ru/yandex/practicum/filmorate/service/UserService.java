package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import ru.yandex.practicum.filmorate.model.EventUser;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.eventFeed.EventFeedDBStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final EventFeedDBStorage eventFeedDBStorage;
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
    }

    public void removeFriends(Long id, Long friendId) throws NotFoundException {
        checkUserById(id, friendId);
        userStorage.removeFriends(id, friendId);
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
        return eventFeedDBStorage.getEventFeed(userId);
    }

    public List<Film> getRecommendations(Long userId) {
        String sqlQueryUsersRecommendation = "SELECT user_id FROM likes_list WHERE film_id IN " +
                "(SELECT film_id FROM likes_list WHERE user_id = ?) AND user_id != ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlQueryUsersRecommendation, userId, userId);
        if (!sqlRowSet.next()) {
            return new ArrayList<>();
        } else {
            Long usersRecommendationId = sqlRowSet.getLong("user_id");
            String sqlQueryRecommendations = "SELECT f.*, mpa_id, mr.name FROM films AS f INNER JOIN likes_list AS l " +
                    "ON f.id = l.film_id INNER JOIN mpa_ratings AS mr ON f.mpa_id = mr.id WHERE l.film_id NOT IN " +
                    "(SELECT film_id FROM likes_list WHERE user_id = ?) AND l.user_id = ?";
            return jdbcTemplate.query(sqlQueryRecommendations, (rs, rowNum) ->
                    filmDbStorage.makeFilm(rs), userId, usersRecommendationId);
        }
    }

}