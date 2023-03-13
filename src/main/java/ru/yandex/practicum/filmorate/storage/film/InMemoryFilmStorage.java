package ru.yandex.practicum.filmorate.storage.film;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ExistenceException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    final static LocalDate BIRTH_OF_CINEMA = LocalDate.of(1895, 12, 28);
    private int id = 1;
    private Map<Long, Film> films = new HashMap<>();
    private List<Long> userByLike = new ArrayList<>();
    private Map<Long, List<Long>> filmsLikesByUser = new HashMap<>();

    @Override
    public List<Film> getAllFilms() {
        log.debug("Выдача всех фильмов из хранилища.");
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(Long id) {
        if (!films.containsKey(id)) {
            throw new ExistenceException("Данного фильма нет в базе.");
        }
        return films.get(id);
    }

    @Override
    public Film createFilm(Film film) throws ValidationException {
        log.debug("Создание фильма в хранилище.");
        if (validationFilm(film)) {
            throw new ValidationException("Некорректная дата релиза фильма.");
        }
        film.setId(id++);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) throws ValidationException {
        log.debug("Обновление фильма в хранилище.");
        if (validationFilm(film)) {
            throw new ValidationException("Введены некорректные данные фильма, при обновление.");
        }
        if (!films.containsKey(film.getId())) {
            throw new ExistenceException("При обновлении, фильм не найден в базе.");
        }
        films.put(film.getId(), film);
        return film;
    }
    @Override
    public boolean validationFilm(Film film) {
        log.debug("Валидация даты релиза фильма.");
        return film.getReleaseDate().isBefore(BIRTH_OF_CINEMA);
    }

    @Override
    public List<Film> getTopFilm(Long countFilm) {
        List<Film> allFilms = new ArrayList<>(films.values());
        for (Film film : films.values()) {
            if (film.getLikes() > 0) {
                allFilms.add(film);
            }
        }
        allFilms.sort(Comparator.comparingInt(film -> Math.toIntExact(((Film) film).getLikes())).reversed());
        return allFilms.stream().limit(countFilm).collect(Collectors.toList());
    }

    @Override
    public void addLikeByFilm(Long filmId, Long userId) {
        if (!filmsLikesByUser.containsKey(filmId)){
            filmsLikesByUser.put(filmId, new ArrayList<>());
        }
        if (!filmsLikesByUser.get(filmId).contains(userId)){
            filmsLikesByUser.get(filmId).add(userId);
            getFilmById(filmId).setLikes(getFilmById(filmId).getLikes()+1L);
        }else {
        }
    }
    @Override
    public boolean deleteLikeByFilm(Long filmId, Long userId) {
        if (filmsLikesByUser.containsKey(filmId) && filmsLikesByUser.get(filmId).contains(userId)){
            filmsLikesByUser.get(filmId).remove(userId);
            getFilmById(filmId).setLikes(getFilmById(filmId).getLikes()-1L);
        } else {
            return false;
        }
        return  true;
    }

    @Override
    public Map<Long, Film> getFilms() {
        return films;
    }
}
