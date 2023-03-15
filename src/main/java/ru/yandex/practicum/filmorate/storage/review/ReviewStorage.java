package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    List<Review> getAll(Long filmId);
    Review getById(Long reviewId);
    Review add(Review review);
    Review update(Review review);
    void delete(Long reviewId);
    void addLike(Long reviewId, Long userId);
    void addDislike(Long reviewId, Long userId);

    void removeLike(Long reviewId, Long userId);
    void removeDislike(Long reviewId, Long userId);
}
