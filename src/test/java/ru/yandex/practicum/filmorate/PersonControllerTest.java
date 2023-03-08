
package ru.yandex.practicum.filmorate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.controller.PersonController;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Person;
import ru.yandex.practicum.filmorate.service.person.PersonServiceImp;
import ru.yandex.practicum.filmorate.service.person.PersonService;
import ru.yandex.practicum.filmorate.storage.person.DbFriendStorage;
import ru.yandex.practicum.filmorate.storage.person.InMemoryPersonStorage;
import ru.yandex.practicum.filmorate.storage.person.PersonStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


class PersonControllerTest {

    static PersonController personController;

    public Validator validator;

    @BeforeEach
    void beforeEach() {
        DbFriendStorage dbFriendStorage = new DbFriendStorage(new JdbcTemplate());
        PersonStorage userStorage = new InMemoryPersonStorage();
        PersonService userInMemory = new PersonServiceImp(userStorage);
        personController = new PersonController(userInMemory);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }



    public Person createUser(){
        return  Person.builder()
                .id(1)
                .login("Login")
                .email("asdfe@mail.com")
                .name("Name")
                .birthday(LocalDate.of(2022, 12, 20))
                .friends(new HashSet<>())
                .build();
    }

    @Test
    public void createUserWithFailLogin (){
        Person person = Person.builder()
                .id(1)
                .login("Login ")
                .email("asdf@email.com")
                .birthday(LocalDate.of(2022, 12, 20))
                .name("Name")
                .build();
        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> personController.createPerson(person));
        Assertions.assertEquals("В логине присутсвует пробел.", exception.getMessage());
    }

    @Test
    public void createUserWithIncorrectEmail (){
        Person person = Person.builder()
                .id(1)
                .login("Login")
                .email("asdfemail.com")
                .birthday(LocalDate.of(2022, 12, 20))
                .name("Name")
                .build();
        Set<ConstraintViolation<Person>> violations = validator.validate(person);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void createUserWithoutEmail (){
        Person person = Person.builder()
                .id(1)
                .login("Login")
                .birthday(LocalDate.of(2022, 12, 20))
                .name("Name")
                .build();
        Set<ConstraintViolation<Person>> violations = validator.validate(person);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void createUserWithEmptyLogin (){
        Person person = Person.builder()
                .id(1)
                .email("asdf@email.com")
                .birthday(LocalDate.of(2022, 12, 20))
                .name("Name")
                .build();
        Set<ConstraintViolation<Person>> violations = validator.validate(person);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void createUserWithFailBirthday (){
        Person person = Person.builder()
                .id(1)
                .email("asdf@email.com")
                .birthday(LocalDate.of(2023, 12, 20))
                .name("Name")
                .build();
        Set<ConstraintViolation<Person>> violations = validator.validate(person);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void createUserWithoutName (){
        Person person = Person.builder()
                .id(1)
                .login("Login")
                .email("asdfe@mail.com")
                .birthday(LocalDate.of(2022, 12, 20))
                .build();
        personController.createPerson(person);
        assertEquals(personController.getAllUsers().get(personController.getAllUsers().size()-1).getName(), person.getLogin());
    }

    @Test
    public void createCorrectUser (){
        Person person = createUser();
        assertEquals(personController.createPerson(person), person);
    }

    @Test
    public void getTwoUser (){
        Person personFirst = createUser();
        personController.createPerson(personFirst);
        Person personSecond = createUser();
        personController.createPerson(personSecond);
        assertEquals(personController.getAllUsers().size(), 2);
    }

    @Test
    public void updateUserWithCorrectId (){
        Person personFirst = createUser();
        personController.createPerson(personFirst);
        Person personSecond = Person.builder()
                .id(1)
                .login("Login")
                .email("asdfe@mail.com")
                .name("NameSecond")
                .birthday(LocalDate.of(2022, 12, 20))
                .build();
        personController.updatePerson(personSecond);
        assertEquals(personController.getPersonById(1L).getName(), personSecond.getName());
    }

    @Test
    public void updateUserWithIncorrectId (){
        Person personFirst = createUser();
        personController.createPerson(personFirst);
        Person personSecond = Person.builder()
                .id(50)
                .login("Login")
                .email("asdfe@mail.com")
                .name("NameSecond")
                .birthday(LocalDate.of(2022, 12, 20))
                .build();
        ExistenceException exception = Assertions.assertThrows(ExistenceException.class,
                () -> personController.updatePerson(personSecond));
        Assertions.assertEquals("Пользователь с id 50 не найден.", exception.getMessage());
    }

    @Test
    public void getUserByCorrectId (){
        Person personFirst = createUser();
        personController.createPerson(personFirst);
        Assertions.assertEquals(personFirst, personController.getPersonById(1L));
    }

    @Test
    public void getUserByIncorrectId (){
        Person personFirst = createUser();
        personController.createPerson(personFirst);
        ExistenceException exception = Assertions.assertThrows(ExistenceException.class,
                () -> personController.getPersonById(50L));
        Assertions.assertEquals("Пользователь с id 50 не найден.", exception.getMessage());
    }

}