package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ReviewService {
    private final ReviewStorage reviewStorage;

    public List<Review> getAll(Long filmId, Integer count) {
        log.info("Запрос на получения всех отзывов");
        return reviewStorage.getAll(filmId, count);
    }

    public Review getById(Long reviewId) {
        log.info("Запрос на получение отзыва с id = {}", reviewId);
        return reviewStorage.getById(reviewId);
    }

    public Review add(Review review) {
        validateReview(review);
        log.info("Запрос на добавление нового отзыва.");
        return reviewStorage.add(review);
    }

    public Review update(Review review) {
        validateReview(review);
        log.info("Запрос на обновление отзыва к фильму с id = {}", review.getFilmId());
        return reviewStorage.update(review);
    }

    public void delete(Long reviewId) {
        reviewStorage.delete(reviewId);
    }

    public void addLike(Long reviewId, Long userId) {
        reviewStorage.addLike(reviewId, userId);
    }

    public void addDislike(Long reviewId, Long userId) {
        reviewStorage.addDislike(reviewId, userId);
    }

    public void removeLike(Long reviewId, Long userId) {
        reviewStorage.removeLike(reviewId, userId);
    }

    public void removeDislike(Long reviewId, Long userId) {
        reviewStorage.removeDislike(reviewId, userId);
    }

    public void validateReview(Review review) {
        if (review.getFilmId() == null || review.getFilmId() < 0) {
            log.error("Попытка добавить отзыв к фильму с неправильным id.");
            throw new NotFoundException("Фильм с таким id не был найден.");
        } else if (review.getUserId() == null || review.getUserId() < 0) {
            log.error("Попытка добавить отзыв от пользователя с несуществующим id.");
            throw new NotFoundException("Пользователь с таким id не был найден.");
        }
    }
}
