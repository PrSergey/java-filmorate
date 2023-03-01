
package ru.yandex.practicum.filmorate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Person;
import ru.yandex.practicum.filmorate.service.InMemoryUserService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


class PersonControllerTest {

    static UserController userController;

    private Validator validator;

    @BeforeEach
    void beforeEach() {
        UserStorage userStorage = new InMemoryUserStorage();
        UserService userInMemory = new InMemoryUserService(userStorage);
        userController = new UserController(userInMemory);
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
                () -> userController.createUser(person));
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
        userController.createUser(person);
        assertEquals(userController.getAllUsers().get(0).getName(), person.getLogin());
    }

    @Test
    public void createCorrectUser (){
        Person person = createUser();
        assertEquals(userController.createUser(person), person);
    }

    @Test
    public void getTwoUser (){
        Person personFirst = createUser();
        userController.createUser(personFirst);
        Person personSecond = createUser();
        userController.createUser(personSecond);
        assertEquals(userController.getAllUsers().size(), 2);
    }

    @Test
    public void updateUserWithCorrectId (){
        Person personFirst = createUser();
        userController.createUser(personFirst);
        Person personSecond = Person.builder()
                .id(1)
                .login("Login")
                .email("asdfe@mail.com")
                .name("NameSecond")
                .birthday(LocalDate.of(2022, 12, 20))
                .build();
        userController.updateUser(personSecond);
        assertEquals(userController.getUserById(1L), personSecond);
    }

    @Test
    public void updateUserWithIncorrectId (){
        Person personFirst = createUser();
        userController.createUser(personFirst);
        Person personSecond = Person.builder()
                .id(2)
                .login("Login")
                .email("asdfe@mail.com")
                .name("NameSecond")
                .birthday(LocalDate.of(2022, 12, 20))
                .build();
        ExistenceException exception = Assertions.assertThrows(ExistenceException.class,
                () -> userController.updateUser(personSecond));
        Assertions.assertEquals("Пользователь с id 2 не найден.", exception.getMessage());
    }

    @Test
    public void getUserByCorrectId (){
        Person personFirst = createUser();
        userController.createUser(personFirst);
        Assertions.assertEquals(personFirst, userController.getUserById(1L));
    }

    @Test
    public void getUserByIncorrectId (){
        Person personFirst = createUser();
        userController.createUser(personFirst);
        ExistenceException exception = Assertions.assertThrows(ExistenceException.class,
                () -> userController.getUserById(2L));
        Assertions.assertEquals("Пользователь с id 2 не найден", exception.getMessage());
    }

    @Test
    public void addFriendWithIncorrectIdUserAndIncorrectIdFriends (){
        Person personFirst = createUser();
        userController.createUser(personFirst);
        ExistenceException exceptionFriend = Assertions.assertThrows(ExistenceException.class,
                () -> userController.addFriend(1L, -1L));
        Assertions.assertEquals("Id не может быть отрицательный", exceptionFriend.getMessage());
        ExistenceException exceptionUser = Assertions.assertThrows(ExistenceException.class,
                () -> userController.addFriend(-1L, 1L));
        Assertions.assertEquals("Id не может быть отрицательный", exceptionUser.getMessage());
    }

    @Test
    public void addFriendWithCorrectId (){
        Person personFirst = createUser();
        userController.createUser(personFirst);
        Person personSecond = createUser();
        userController.createUser(personSecond);
        userController.addFriend(1L, 2L);
        Assertions.assertEquals(userController.getUserById(1L).getFriends().size(), 1);
    }

    @Test
    public void deleteFriendWithIncorrectId (){
        Person personFirst = createUser();
        userController.createUser(personFirst);
        ExistenceException exceptionUser = Assertions.assertThrows(ExistenceException.class,
                () -> userController.deleteFriend(1L, 2L));
        Assertions.assertEquals("У пользователя с id 1 нет друга с id 2", exceptionUser.getMessage());
    }

    @Test
    public void deleteFriendWithCorrectId (){
        Person personFirst = createUser();
        userController.createUser(personFirst);
        Person personSecond = createUser();
        userController.createUser(personSecond);
        userController.addFriend(1L, 2L);
        userController.deleteFriend(1L, 2L);
        Assertions.assertEquals(userController.getFriendsOfUser(1L).size(), 0);
    }

    @Test
    public void getMutualFriends (){
        Person personFirst = createUser();
        userController.createUser(personFirst);
        Person personSecond = createUser();
        userController.createUser(personSecond);
        Person personThird = createUser();
        userController.createUser(personThird);
        userController.addFriend(1L, 2L);
        userController.addFriend(3L, 2L);
        Assertions.assertEquals(userController.getMutualFriends(1L, 3L).get(0), personSecond);
    }





}