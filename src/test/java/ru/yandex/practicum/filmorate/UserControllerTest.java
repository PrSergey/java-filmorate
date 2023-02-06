
package ru.yandex.practicum.filmorate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
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
    public void createUserWithCorrectData (){
        User user = User.builder()
                .id(1)
                .login("Login")
                .email("asdfe@mail.com")
                .name("Name")
                .birthday(LocalDate.of(2022, 12, 20))
                .build();
        assertEquals(userController.createUser(user), user);
    }


}