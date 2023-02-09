
package ru.yandex.practicum.filmorate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
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


class UserControllerTest {

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

    public User createUser(){
        return  User.builder()
                .id(1)
                .login("Login")
                .email("asdfe@mail.com")
                .name("Name")
                .birthday(LocalDate.of(2022, 12, 20))
                .build();
    }

    @Test
    public void createUserWithFailLogin (){
        User user = User.builder()
                .id(1)
                .login("Login ")
                .email("asdf@email.com")
                .birthday(LocalDate.of(2022, 12, 20))
                .name("Name")
                .build();
        ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> userController.createUser(user));
        Assertions.assertEquals("В логине присутсвует пробел.", exception.getMessage());
    }

    @Test
    public void createUserWithIncorrectEmail (){
        User user = User.builder()
                .id(1)
                .login("Login")
                .email("asdfemail.com")
                .birthday(LocalDate.of(2022, 12, 20))
                .name("Name")
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void createUserWithoutEmail (){
        User user = User.builder()
                .id(1)
                .login("Login")
                .birthday(LocalDate.of(2022, 12, 20))
                .name("Name")
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void createUserWithEmptyLogin (){
        User user = User.builder()
                .id(1)
                .email("asdf@email.com")
                .birthday(LocalDate.of(2022, 12, 20))
                .name("Name")
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void createUserWithFailBirthday (){
        User user = User.builder()
                .id(1)
                .email("asdf@email.com")
                .birthday(LocalDate.of(2023, 12, 20))
                .name("Name")
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void createUserWithoutName (){
        User user = User.builder()
                .id(1)
                .login("Login")
                .email("asdfe@mail.com")
                .birthday(LocalDate.of(2022, 12, 20))
                .build();
        userController.createUser(user);
        assertEquals(userController.getAllUsers().get(0).getName(), user.getLogin());
    }

    @Test
    public void createCorrectUser (){
        User user = createUser();
        assertEquals(userController.createUser(user), user);
    }

    @Test
    public void getTwoUser (){
        User userFirst = createUser();
        userController.createUser(userFirst);
        User userSecond = createUser();
        userController.createUser(userSecond);
        assertEquals(userController.getAllUsers().size(), 2);
    }

    @Test
    public void updateUserWithCorrectId (){
        User userFirst = createUser();
        userController.createUser(userFirst);
        User userSecond = User.builder()
                .id(1)
                .login("Login")
                .email("asdfe@mail.com")
                .name("NameSecond")
                .birthday(LocalDate.of(2022, 12, 20))
                .build();
        userController.updateUser(userSecond);
        assertEquals(userController.getUserById(1L), userSecond);
    }

    @Test
    public void updateUserWithIncorrectId (){
        User userFirst = createUser();
        userController.createUser(userFirst);
        User userSecond = User.builder()
                .id(2)
                .login("Login")
                .email("asdfe@mail.com")
                .name("NameSecond")
                .birthday(LocalDate.of(2022, 12, 20))
                .build();
        ExistenceException exception = Assertions.assertThrows(ExistenceException.class,
                () -> userController.updateUser(userSecond));
        Assertions.assertEquals("Пользователь с id 2 не найден.", exception.getMessage());
    }

    @Test
    public void getUserByCorrectId (){
        User userFirst = createUser();
        userController.createUser(userFirst);
        Assertions.assertEquals(userFirst, userController.getUserById(1L));
    }

    @Test
    public void getUserByIncorrectId (){
        User userFirst = createUser();
        userController.createUser(userFirst);
        ExistenceException exception = Assertions.assertThrows(ExistenceException.class,
                () -> userController.getUserById(2L));
        Assertions.assertEquals("Пользователь с id 2 не найден", exception.getMessage());
    }

    @Test
    public void addFriendWithIncorrectIdUserAndIncorrectIdFriends (){
        User userFirst = createUser();
        userController.createUser(userFirst);
        ValidationException exceptionFriend = Assertions.assertThrows(ValidationException.class,
                () -> userController.addFriend(1L, -1L));
        Assertions.assertEquals("Id не может быть отрицательный", exceptionFriend.getMessage());
        ValidationException exceptionUser = Assertions.assertThrows(ValidationException.class,
                () -> userController.addFriend(-1L, 1L));
        Assertions.assertEquals("Id не может быть отрицательный", exceptionUser.getMessage());
    }

    @Test
    public void addFriendWithCorrectId (){
        User userFirst = createUser();
        userController.createUser(userFirst);
        User userSecond = createUser();
        userController.createUser(userSecond);
        userController.addFriend(1L, 2L);
        Assertions.assertEquals(userController.getUserById(1L).getFriends().size(), 1);
    }

    @Test
    public void deleteFriendWithIncorrectId (){
        User userFirst = createUser();
        userController.createUser(userFirst);
        ExistenceException exceptionUser = Assertions.assertThrows(ExistenceException.class,
                () -> userController.deleteFriend(1L, 2L));
        Assertions.assertEquals("У пользователя с id 1 нет друга с id 2", exceptionUser.getMessage());
    }

    @Test
    public void deleteFriendWithCorrectId (){
        User userFirst = createUser();
        userController.createUser(userFirst);
        User userSecond = createUser();
        userController.createUser(userSecond);
        userController.addFriend(1L, 2L);
        userController.deleteFriend(1L, 2L);
        Assertions.assertEquals(userController.getFriendsOfUser(1L).size(), 0);
    }

    @Test
    public void getMutualFriends (){
        User userFirst = createUser();
        userController.createUser(userFirst);
        User userSecond = createUser();
        userController.createUser(userSecond);
        User userThird = createUser();
        userController.createUser(userThird);
        userController.addFriend(1L, 2L);
        userController.addFriend(3L, 2L);
        Assertions.assertEquals(userController.getMutualFriends(1L, 3L).get(0), userSecond);
    }





}