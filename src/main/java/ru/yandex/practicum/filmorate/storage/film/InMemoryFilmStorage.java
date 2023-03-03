/*
package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import org.webjars.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private Long id = 1L;

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film addFilm(Film film) {
        film.setId(id);
        film.setLikes(new HashSet<>());
        films.put(film.getId(), film);
        id++;
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            if (film.getLikes() == null || film.getLikes().isEmpty()) {
                film.setLikes(new HashSet<>());
            }
            films.put(film.getId(), film);
        } else {
            throw new NotFoundException("Такого ID нет");
        }
        return film;
    }

    @Override
    public void deleteFilm(Film film) {
        if (films.containsValue(film)) {
            films.remove(film.getId());
        } else {
            throw new NotFoundException("Такого фильма нет");
        }
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilm(Long id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            throw new NotFoundException("Такого ID нет");
        }
    }
}
*/
