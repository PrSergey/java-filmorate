package ru.yandex.practicum.filmorate.storage.mpa;

import org.webjars.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaStorage {

    List<Mpa> getAll();

    Mpa getById(Long id) throws NotFoundException;
}
