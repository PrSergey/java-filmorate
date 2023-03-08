package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.MpaStorage;

import java.util.List;

@Service
@Slf4j
public class MpaService {
    MpaStorage mpaStorage;

    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public List<Mpa> getAllMpa() {
        log.debug("Выдача всех возрастных ограничений.");
        return mpaStorage.getAllMpa();
    }

    public Mpa getMpaById(Long id) {
        log.debug("Выдача возрастных ограничений по id.");
        for (Mpa mpa : getAllMpa()) {
            if (mpa.getId() == id) {
                return mpa;
            }
        }
        return null;
    }
}
