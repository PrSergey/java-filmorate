package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.controller.PersonController;
import ru.yandex.practicum.filmorate.model.Person;
import ru.yandex.practicum.filmorate.service.person.PersonService;
import ru.yandex.practicum.filmorate.service.person.PersonServiceImp;
import ru.yandex.practicum.filmorate.storage.person.DbFriendStorage;
import ru.yandex.practicum.filmorate.storage.person.DbPersonStorage;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class DbPersonStorageTest extends PersonControllerTest {

	private final DbPersonStorage personStorage;
	Validator validator;

	@Override
	@BeforeEach
	void beforeEach() {
		PersonService userInMemory = new PersonServiceImp(personStorage);
		personController = new PersonController(userInMemory);
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		super.validator = factory.getValidator();
	}

	@Override
	public Person createUser() {
		return super.createUser();
	}

	@Override
	@Test
	public void createUserWithFailLogin() {
		super.createUserWithFailLogin();
	}

	@Override
	@Test
	public void createUserWithIncorrectEmail() {
		super.createUserWithIncorrectEmail();
	}

	@Override
	@Test
	public void createUserWithoutEmail() {
		super.createUserWithoutEmail();
	}

	@Override
	@Test
	public void createUserWithEmptyLogin() {
		super.createUserWithEmptyLogin();
	}

	@Override
	@Test
	public void createUserWithFailBirthday() {
		super.createUserWithFailBirthday();
	}

	@Override
	@Test
	public void createUserWithoutName() {
		super.createUserWithoutName();
	}

	@Override
	@Test
	public void createCorrectUser() {
		super.createCorrectUser();
	}

//	@Override
//	@Test
//	public void getTwoUser() {
//		Person personFirst = createUser();
//		personController.createPerson(personFirst);
//		Person personSecond = createUser();
//		personController.createPerson(personSecond);
//		assertEquals(personController.getAllUsers().size(), 13);
//	}

	@Override
	@Test
	public void updateUserWithCorrectId() {
		super.updateUserWithCorrectId();
	}

	@Override
	@Test
	public void updateUserWithIncorrectId() {
		super.updateUserWithIncorrectId();
	}

//	@Override
//	@Test
//	public void getUserByCorrectId() {
//		Person personFirst = createUser();
//		Long id=personController.createPerson(personFirst).getId();
//		Assertions.assertEquals(personFirst, personController.getPersonById(id));
//	}

	@Override
	@Test
	public void getUserByIncorrectId() {
		super.getUserByIncorrectId();
	}

}