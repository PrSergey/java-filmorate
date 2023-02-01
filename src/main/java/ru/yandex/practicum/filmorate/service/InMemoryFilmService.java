package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@Service
public class InMemoryFilmService implements FilmService{


    Map <Long, Long> topTenFilmsOfLikes;
    FilmStorage filmInMemory;

    public InMemoryFilmService(FilmStorage filmInMemory) {
        this.filmInMemory = filmInMemory;
        topTenFilmsOfLikes = new TreeMap<>();
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
        if (topTenFilmsOfLikes.size()<10){
            topTenFilmsOfLikes.put(numberOfLike, filmId);
        } else {
            topTenFilmsOfLikes.remove(10);
            topTenFilmsOfLikes.put(numberOfLike, filmId);
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
