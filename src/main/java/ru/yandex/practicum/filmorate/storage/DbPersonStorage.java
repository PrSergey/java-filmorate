package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Person;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
@Primary
public class DbPersonStorage implements PersonStorage {

    private final JdbcTemplate jdbcTemplate;

    public DbPersonStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Person> getAllPerson() {
        log.debug("Выдача всех пользователей из хранилища.");
        String sql = "select * from person";
        return jdbcTemplate.query(sql, this::mapRowToAllPerson);
    }

    @Override
    public Person getPersonById(Long id) {
        log.debug("Выдача пользователя по id из хранилища.");
        if (countPerson() < id) {
            throw new ExistenceException("Пользователь с id " + id + " не найден.");
        }

        String sql = "select * from person where id = ?";
        return jdbcTemplate.query(sql, this::mapRowToAllPerson, id).get(0);
    }

    @Override
    public Person createPerson(Person person) {
        log.debug("Создание пользователя в хранилище.");
        if (this.validationPerson(person)) {
            throw new ValidationException("В логине присутсвует пробел.");
        }
        if (person.getName() == null || person.getName().isBlank()) {
            person.setName(person.getLogin());
        }

        String sqlCreatePerson = "insert into person (email, login, name, birthday) values (?,?,?,?)";
        jdbcTemplate.update(sqlCreatePerson, person.getEmail(), person.getLogin(), person.getName(),
                person.getBirthday());

        person.setId(countPerson());
        return person;
    }

    private Long countPerson() {
        long countPerson = 0L;
        String sqlCountPerson = "select COUNT(id) from person";
        SqlRowSet countPersonRow = jdbcTemplate.queryForRowSet(sqlCountPerson);

        if (countPersonRow.next()) {
            countPerson = countPersonRow.getLong("COUNT(id)");
        }
        return countPerson;
    }


    @Override
    public Person updatePerson(Person person) {
        log.debug("Обновление пользователя из хранилище.");
        if (validationPerson(person)) {
            throw new ValidationException("В логине присутсвует пробел.");
        }

        String sqlUpdatePerson = "update person set " +
                "email = ?, login = ?, name = ?, birthday = ?" +
                "where id = ?";
        int numberOfRowsAffected = jdbcTemplate.update(sqlUpdatePerson
                , person.getEmail()
                , person.getLogin()
                , person.getName()
                , person.getBirthday()
                , person.getId());
        if (numberOfRowsAffected == 0) {
            throw new ExistenceException("Пользователь с id " + person.getId() + " не найден.");
        }
        if (person.getFriends() == null) {
            person.setFriends(new HashSet<>());
        }
        return person;
    }

    @Override
    public void addFriends(Long userId, Long friendId) {
        log.debug("Добавление друзей в хранилище.");
        Set<Long> friendsId = new HashSet<>();
        Set<Long> friendsByFriendId = new HashSet<>();

        String sqlFriendsId = "select friend_id from friendship where user_id = ?";
        SqlRowSet friendsRows = jdbcTemplate.queryForRowSet(sqlFriendsId, userId);
        while (friendsRows.next()) {
            friendsId.add(friendsRows.getLong("friend_id"));
        }

        if (!friendsId.contains(friendId)) {
            String sqlAddFriend = "insert into friendship(user_id, friend_id) values (?,?)";
            jdbcTemplate.update(sqlAddFriend, userId, friendId);
        }


        String sqlFriedsByFriendId = "select user_id from friendship where friend_id = ?";
        SqlRowSet friedsByFriendRows = jdbcTemplate.queryForRowSet(sqlFriendsId, friendId);
        while (friedsByFriendRows.next()) {
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

    public boolean deleteFriend(Long userId, Long friendId) {
        String sqlDeleteFriend = "delete from friendship where user_id = ? and friend_id=?";
        return jdbcTemplate.update(sqlDeleteFriend, userId, friendId) > 0;
    }

    @Override
    public boolean validationPerson(Person person) {
        log.debug("Валидация логина пользователя на пробел.");
        return person.getLogin().contains(" ");
    }

    @Override
    public Map<Long, Person> getPerson() {
        log.debug("Выдача всех друзей в хранилище с id.");
        Map<Long, Person> persons = new HashMap<>();
        for (Person person : getAllPerson()) {
            persons.put(person.getId(), person);
        }
        return persons;
    }

    private HashSet<Person> getFriends(long id) {
        log.debug("Выдача всех друзей в хранилище.");
        HashSet<Person> friends = new HashSet<>();

        String sqlFriends = "select friend_id from friendship where user_id = ?";
        SqlRowSet friendsRows = jdbcTemplate.queryForRowSet(sqlFriends, id);
        while (friendsRows.next()) {
            Long idFriend = friendsRows.getLong("friend_id");
            friends.add(getPersonById(idFriend));
        }
        return friends;
    }

    private Person mapRowToAllPerson(ResultSet rs, int rowNum) throws SQLException {

        long id = rs.getLong("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        LocalDate birthday = rs.getTimestamp("birthday").toLocalDateTime().toLocalDate();
        String login = rs.getString("login");
        HashSet<Person> friends = getFriends(id);

        return new Person(id, email, login, name, birthday, friends);
    }

}
