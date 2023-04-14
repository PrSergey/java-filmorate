package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import ru.yandex.practicum.filmorate.constant.EventOperation;
import ru.yandex.practicum.filmorate.constant.EventType;
import ru.yandex.practicum.filmorate.model.EventUser;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.EventFeedStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final EventFeedStorage eventFeedStorage;

    public List<Review> getAll(Optional<Long> filmId, Optional<Integer> count) {
        if (filmId.isEmpty() && count.isEmpty()) {
            return reviewStorage.getAll();
        }
        return reviewStorage.getAll(filmId.orElse(-1L), count.orElse(10));
    }

    public Review getById(Long reviewId) {
        return reviewStorage.getById(reviewId);
    }

    public Review add(Review review) {
        validateReview(review);

        Review reviewAfterAdd = reviewStorage.add(review);
        EventUser eventUser = new EventUser(review.getUserId(), reviewAfterAdd.getReviewId(),
                EventType.REVIEW, EventOperation.ADD);
        eventFeedStorage.setEventFeed(eventUser);

        return reviewAfterAdd;
    }

    public Review update(Review review) {
        validateReview(review);

        Long reviewByUserId = getById(review.getReviewId()).getUserId();
        EventUser eventUser = new EventUser(reviewByUserId, review.getReviewId(),
                EventType.REVIEW, EventOperation.UPDATE);
        eventFeedStorage.setEventFeed(eventUser);

        return reviewStorage.update(review);
    }

    public void delete(Long reviewId) {
        Review review = getById(reviewId);
        EventUser eventUser = new EventUser(review.getUserId(), reviewId,
                EventType.REVIEW, EventOperation.REMOVE);
        eventFeedStorage.setEventFeed(eventUser);
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