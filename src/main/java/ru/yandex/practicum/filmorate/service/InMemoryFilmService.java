package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@Service
public class InMemoryFilmService implements FilmService{


    List<Long> topFilmsOfLikes;
    FilmStorage filmInMemory;

    @Autowired
    public InMemoryFilmService(FilmStorage filmInMemory) {
        this.filmInMemory = filmInMemory;
        topFilmsOfLikes = new ArrayList<>();
    }

    public Film addFilm (Film film){
        return filmInMemory.createFilm(film);
    }

    public Film updateFilm (Film film){
        return filmInMemory.updateFilm(film);
    }

    public List<Film> getFilms (){
        return filmInMemory.allFilms();
    }

    @Override
    public Long addLike(Long filmId, Long userId) {
        if (getFilmLikes(filmId) == null){
            return null;
        }else{
            getFilmLikes(filmId).add(userId);
            return userId;
        }
    }

    @Override
    public Long deleteLike(Long filmId, Long userId) {
        if (getFilmLikes(filmId) == null){
            return null;
        }else{
            getFilmLikes(filmId).remove(userId);
            return userId;
        }
    }

    @Override
    public Long getTopTenFilmsOfLikes() {
        return null;
    }

    public void addTopTenFilmsOfLikes (Long filmId, Long numberOfLike){

    }

    public void addFilmInTop (Film film){
        if (topFilmsOfLikes.contains(film.getId())){
            Long numberOfRating = (long) topFilmsOfLikes.indexOf(film.getId());
            if (film.getLikes().size() < filmInMemory.getFilms().get(numberOfRating-1).getLikes().size()){

            };
        }else {
            topFilmsOfLikes.add(film.getId());
        }
    }
    public HashSet<Long> getFilmLikes (Long filmId){
        if (filmInMemory.getFilms().containsKey(filmId)){
            return null;
        }else{
            return filmInMemory.getFilms().get(filmId).getLikes();
        }
    }
}
