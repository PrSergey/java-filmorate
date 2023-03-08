package ru.yandex.practicum.filmorate.storage.person;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@Component("dbPersonStorage")
public class DbPersonStorage implements PersonStorage {

    private final JdbcTemplate jdbcTemplate;


    private final DbFriendStorage dbFriendStorage;
    @Autowired
    public DbPersonStorage(JdbcTemplate jdbcTemplate, DbFriendStorage dbFriendStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.dbFriendStorage = dbFriendStorage;
    }

    @Override
    public List<Person> getAllPerson() {
        log.debug("Выдача всех пользователей из хранилища.");
        List<Person> persons;

        String sql = "select * from person";
        persons = jdbcTemplate.query(sql, this::mapRowToAllPerson);

        return dbFriendStorage.setFriendsToPerson(persons);
    }

    private Person mapRowToAllPerson(ResultSet rs, int rowNum) throws SQLException {

        long id = rs.getLong("person_id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        LocalDate birthday = rs.getTimestamp("birthday").toLocalDateTime().toLocalDate();
        String login = rs.getString("login");
        Set<Person> friends = new HashSet<>();

        return new Person(id, email, login, name, birthday, friends);
    }

    @Override
    public Person getPersonById(Long id) {
        log.debug("Выдача пользователя по id из хранилища.");
        Set<Person> friends = new HashSet<>();

        String sql = "select * from person where person_id = ?";
        List <Person> pesron = jdbcTemplate.query(sql, this::mapRowToAllPerson, id);
        if (pesron.isEmpty()) {
            throw new ExistenceException("Пользователь с id " + id + " не найден.");
        }

        Person personById = pesron.get(0);
        Set<Long> friendsId = dbFriendStorage.getFriends(id);
        for (Long friendId: friendsId){
            friends.add(getPersonById(friendId));
        }
        personById.setFriends(friends);
        return personById;
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

        String sqlGetId = "select person_id from person where login = ?";
        SqlRowSet getIdRow = jdbcTemplate.queryForRowSet(sqlGetId, person.getLogin());
        if (getIdRow.next()){
            person.setId(getIdRow.getLong("person_id"));
        }
        return person;
    }

    @Override
    public Person updatePerson(Person person) {
        log.debug("Обновление пользователя из хранилище.");

        if (validationPerson(person)) {
            throw new ValidationException("В логине присутсвует пробел.");
        }

        String sqlUpdatePerson = "update person set " +
                "email = ?, login = ?, name = ?, birthday = ?" +
                "where person_id = ?";
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





}
