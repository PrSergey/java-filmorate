package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.webjars.NotFoundException;
import ru.yandex.practicum.filmorate.constant.EventOperation;
import ru.yandex.practicum.filmorate.constant.EventType;
import ru.yandex.practicum.filmorate.model.EventUser;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.eventFeed.EventFeedDBStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private final EventFeedDBStorage eventFeedDBStorage;

    @Override
    public List<User> getAll() {
        String sqlQuery =
                "SELECT u.id, " +
                        "u.email, " +
                        "u.login, " +
                        "u.name, " +
                        "u.birthday, " +
                        "FROM users AS u;";
        return jdbcTemplate.query(sqlQuery, (rs, rn) -> makeUser(rs));
    }

    @Override
    public User getById(Long id) throws NotFoundException {
        String sqlQuery =
                "SELECT u.id, " +
                        "u.email, " +
                        "u.login, " +
                        "u.name, " +
                        "u.birthday, " +
                        "FROM users AS u " +
                        "WHERE u.id = ?;";
        return jdbcTemplate.query(sqlQuery, (rs, rn) -> makeUser(rs), id)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не существует"));
    }

    @Override
    public User add(User user) {
        String sqlQuery = "INSERT INTO users (email, login, name, birthday) " +
                "VALUES (?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"id"});
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getLogin());
            statement.setString(3, user.getName());
            statement.setDate(4, user.getBirthday());
            return statement;
        }, keyHolder);

        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return user;
    }

    @Override
    public User update(User user) {
        String sqlQuery = "UPDATE users " +
                "SET email = ?, login = ?, name = ?, birthday = ? " +
                "WHERE id = ?;";
        getById(user.getId());
        jdbcTemplate.update(
                sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return getById(user.getId());
    }

    @Override
    public void deleteUserById(User user) {
        getById(user.getId());
        String sqlQuery = "DELETE FROM users WHERE id = ?;";
        jdbcTemplate.update(sqlQuery, user.getId());

    }

    @Override
    public void makeFriends(Long userId, Long friendId) {
        String sqlQuery = "INSERT INTO friendships (user_id, friend_id) VALUES (?, ?);";
        jdbcTemplate.update(sqlQuery, userId, friendId);
        EventUser eventUser = new EventUser(userId, friendId, EventType.FRIEND, EventOperation.ADD);
        eventFeedDBStorage.setEventFeed(eventUser);
    }

    @Override
    public void removeFriends(Long userId, Long friendId) {
        String sqlQuery = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?;";
        jdbcTemplate.update(sqlQuery, userId, friendId);
        EventUser eventUser = new EventUser(userId, friendId, EventType.FRIEND, EventOperation.REMOVE);
        eventFeedDBStorage.setEventFeed(eventUser);
    }

    @Override
    public Set<Long> getUserFriendsById(Long userId) throws NotFoundException {
        String sqlQuery =
                "SELECT fr.friend_id, " +
                        "FROM friendships AS fr " +
                        "WHERE fr.user_id = ?;";
        List<Long> list = jdbcTemplate.queryForList(sqlQuery, Long.class, userId);
        return new HashSet<>(list);
    }

    @Override
    public void deleteUserById(Long userId) {
        getById(userId);
        String sqlQuery = "DELETE FROM users WHERE id = ?;";
        jdbcTemplate.update(sqlQuery, userId);
    }

    private User makeUser(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        Date birthday = rs.getDate("birthday");

        Set<Long> friends = getUserFriendsById(id);
        return new User(id, email, login, name, birthday, friends);
    }
}
