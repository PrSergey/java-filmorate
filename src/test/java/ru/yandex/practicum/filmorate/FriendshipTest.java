package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FriendshipController;
import ru.yandex.practicum.filmorate.controller.PersonController;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.model.Person;
import ru.yandex.practicum.filmorate.service.person.FriendService;
import ru.yandex.practicum.filmorate.service.person.PersonService;
import ru.yandex.practicum.filmorate.service.person.PersonServiceImp;
import ru.yandex.practicum.filmorate.storage.person.DbFriendStorage;
import ru.yandex.practicum.filmorate.storage.person.DbPersonStorage;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.HashSet;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FriendshipTest {

    private final DbFriendStorage dbFriendStorage;
    private final DbPersonStorage dbPersonStorage;
    static FriendshipController friendshipController;
    public Validator validator;

    private final DbPersonStorage personStorage;
    static PersonController personController;

    @BeforeEach
    void beforeEach() {
        FriendService friendService = new FriendService(dbPersonStorage, dbFriendStorage);
        friendshipController = new FriendshipController(friendService);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void beforeEachPersonal() {
        PersonService userInMemory = new PersonServiceImp(personStorage);
        personController = new PersonController(userInMemory);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
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
    public void addFriendWithIncorrectIdUserAndIncorrectIdFriends (){
        Person personFirst = createUser();
        personController.createPerson(personFirst);
        ExistenceException exceptionFriend = Assertions.assertThrows(ExistenceException.class,
                () -> friendshipController.addFriend(1L, -1L));
        Assertions.assertEquals("Id не может быть отрицательный", exceptionFriend.getMessage());
        ExistenceException exceptionUser = Assertions.assertThrows(ExistenceException.class,
                () -> friendshipController.addFriend(-1L, 1L));
        Assertions.assertEquals("Id не может быть отрицательный", exceptionUser.getMessage());
    }

    @Test
    public void addFriendWithCorrectId (){
        Person personFirst = createUser();
        personController.createPerson(personFirst);
        Person personSecond = createUser();
        personController.createPerson(personSecond);
        friendshipController.addFriend(1L, 2L);
        Assertions.assertEquals(personController.getPersonById(1L).getFriends().size(), 1);
    }

    @Test
    public void deleteFriendWithIncorrectId (){
        Person personFirst = createUser();
        personController.createPerson(personFirst);
        ExistenceException exceptionUser = Assertions.assertThrows(ExistenceException.class,
                () -> friendshipController.deleteFriend(1L, 50L));
        Assertions.assertEquals("Пользователь с id 50 не найден.", exceptionUser.getMessage());
    }

    @Test
    public void getMutualFriends (){

            Person personFirst = createUser();
            personController.createPerson(personFirst);
            Person personSecond = createUser();
            personController.createPerson(personSecond);
            Person personThird = createUser();
            personController.createPerson(personThird);
            friendshipController.addFriend(1L, 3L);
            friendshipController.addFriend(2L, 3L);
            Assertions.assertEquals(friendshipController.getMutualFriends(1L, 2L).get(0),
                    personController.getPersonById(3L));
    }
}
