package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.webjars.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
public class MpaStorageTest {
    @Autowired
    private MpaStorage mpaStorage;

    @Test
    void getByIdValidMPA() {
        Optional<Mpa> mpaOptional = Optional.ofNullable(mpaStorage.getById(1L));
        assertThat(mpaOptional)
                .isPresent()
                .hasValueSatisfying(mpa ->
                        assertThat(mpa).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    void getByIdNotValid() {
        assertThrows(NotFoundException.class, () -> mpaStorage.getById(10L));
    }

    @Test
    void getAllExecuteItems() {
        List<Mpa> mpa = mpaStorage.getAll();
        assertThat(mpa).hasSize(5);
    }
}
