package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class InMemoryFilmService implements FilmService{

    private final static Logger log = LoggerFactory.getLogger(FilmController.class);
    List <Long> topFilmsOfLikes;
    FilmStorage filmInMemory;

    @Autowired
    public InMemoryFilmService(FilmStorage filmInMemory) {
        this.filmInMemory = filmInMemory;
        topFilmsOfLikes = new ArrayList<>();
    }

    @Override
    public List<Film> getAllFilms(){
        return filmInMemory.allFilms();
    }

    @Override
    public Film getFilm(Long id) throws ValidationException{
        if (filmInMemory.getFilms().get(id)==null){
            throw new ValidationException("Данного фильма нет в базе.");
        }
        return filmInMemory.getFilms().get(id);
    }

    @Override
    public Film createFilm(Film film) {
        HashSet<Long> likes = new HashSet<>();
        film.setLikes(likes);
        return filmInMemory.createFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        return filmInMemory.updateFilm(film);
    }

    @Override
    public Long addLike(Long filmId, Long userId) throws ValidationException {
        log.debug("Добавление лайка.");
        getFilmLikes(filmId).add(userId);

            return userId;
    }

    @Override
    public Long deleteLike(Long filmId, Long userId) throws ValidationException {
            getFilmLikes(filmId).remove(userId);
            return userId;
    }

    @Override
    public List getTopFilmsOfLikes(Long countFilms) {
        List<Film> allFilms = new ArrayList<>(filmInMemory.getFilms().values());
        allFilms.sort(Comparator.comparingInt(film -> film.getLikes().size()));
        if (allFilms.size()<10){
            return allFilms;
        }
        return allFilms.stream().limit(countFilms).collect(Collectors.toList());
    }

    public HashSet<Long> getFilmLikes (Long filmId) throws ValidationException{
        if (!filmInMemory.getFilms().containsKey(filmId)){
            throw new ValidationException("Фильм с данным Id не найден.");
        }else{
            return filmInMemory.getFilms().get(filmId).getLikes();
        }
    }

}
