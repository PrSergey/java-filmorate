package ru.yandex.practicum.filmorate.storage.person;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Person;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class DbFriendStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbFriendStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public HashSet<Long> getFriends(long id) {
        log.debug("Выдача всех друзей в хранилище.");
        HashSet<Long> friends = new HashSet<>();

        String sqlFriends = "select friend_id from friendship where user_id = ?";
        SqlRowSet friendsRows = jdbcTemplate.queryForRowSet(sqlFriends, id);
        while (friendsRows.next()) {
            Long idFriend = friendsRows.getLong("friend_id");
            friends.add(idFriend);
        }
        return friends;
    }

    public boolean deleteFriend(Long userId, Long friendId) {
        String sqlDeleteFriend = "delete from friendship where user_id = ? and friend_id=?";
        return jdbcTemplate.update(sqlDeleteFriend, userId, friendId) > 0;
    }

    public void addFriends(Long userId, Long friendId) {
        log.debug("Добавление друзей в хранилище.");
        Set<Long> friendsId = new HashSet<>();
        Set<Long> friendsByFriendId = new HashSet<>();

        String sqlFriendsId = "select friend_id, user_id from friendship where user_id = ?";
        SqlRowSet friendsRows = jdbcTemplate.queryForRowSet(sqlFriendsId, userId);
        while (friendsRows.next()) {
            friendsId.add(friendsRows.getLong("friend_id"));
        }

        if (!friendsId.contains(friendId)) {
            String sqlAddFriend = "insert into friendship(user_id, friend_id) values (?,?)";
            jdbcTemplate.update(sqlAddFriend, userId, friendId);
        }

        SqlRowSet friendsByFriendRows = jdbcTemplate.queryForRowSet(sqlFriendsId, friendId);
        while (friendsByFriendRows.next()) {
            friendsByFriendId.add(friendsRows.getLong("user_id"));
        }

        if (friendsByFriendId.contains(userId)) {
            String sqlUpdateConfirmation = "update friendship set " +
                    "confirmation=true" +
                    "where user_id = ? AND friend_id = ?";
            jdbcTemplate.update(sqlUpdateConfirmation, userId, friendId);
            jdbcTemplate.update(sqlUpdateConfirmation, friendId, userId);
        }
    }

    public List<Person> setFriendsToPerson(List<Person> persons) {

        String sqlFriend = "select * " +
                "from friendship as f " +
                "left join person as p  ON f.friend_id = p.person_id " +
                "where confirmation = true";
        SqlRowSet sqlFriendsRow = jdbcTemplate.queryForRowSet(sqlFriend);
        while (sqlFriendsRow.next()) {
            for (Person person : persons) {
                if (person.getId() == sqlFriendsRow.getLong("person_id")) {
                    Person friend = new Person (sqlFriendsRow. getLong("friend_id"),
                            sqlFriendsRow.getString("email"),
                            sqlFriendsRow.getString("login"),
                            sqlFriendsRow.getString("name"),
                            sqlFriendsRow.getDate("birthday").toLocalDate(),
                            new HashSet<>());
                    person.getFriends().add(friend);
                }
            }
        }
        return persons;
    }


}
