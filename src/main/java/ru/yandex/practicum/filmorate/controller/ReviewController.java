package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/reviews")
@AllArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping
    public List<Review> findAll(@RequestParam(name = "filmId", required = false) Optional<Long> filmId,
                                @RequestParam(name = "count", required = false, defaultValue = "10") Optional<Integer> count) {
        log.info("Запрос на получения всех отзывов");
        return reviewService.getAll(filmId, count);
    }

    @GetMapping("/{id}")
    public Review findById(@Positive @PathVariable("id") Long id) {
        log.info("Запрос на получение отзыва с id = {}", id);
        return reviewService.getById(id);
    }

    @PostMapping
    public Review add(@RequestBody @Valid Review review) {
        log.info("Запрос на добавление нового отзыва от пользователя с id= {}", review.getUserId());
        return reviewService.add(review);
    }

    @PutMapping
    public Review update(@RequestBody @Valid Review review) {
        log.info("Запрос на обновление отзыва c id= {} к фильму с id= {}",review.getReviewId(), review.getFilmId());
        return reviewService.update(review);
    }



    @DeleteMapping("/{id}")
    public void deleteById(@Positive @PathVariable("id") Long id) {
        log.info("Удаление отзыва с id= {}",id);
        reviewService.delete(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@Positive @PathVariable("id") Long id,
                        @Positive @PathVariable("userId") Long userId) {
        log.info("Добавление лайка к отзыву с id= {} пользователем с id= {}",id, userId);
        reviewService.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislikeReview(@Positive @PathVariable("id") Long id,
                                 @Positive @PathVariable("userId") Long userId) {
        log.info("Добавление дизлайка к отзыву с id= {} пользователем с id= {}",id, userId);
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@Positive @PathVariable("id") Long id,
                           @Positive @PathVariable("userId") Long userId) {
        log.info("Удаление лайка к отзыву с id= {} пользователем с id= {}",id, userId);
        reviewService.removeLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@Positive @PathVariable("id") Long id,
                              @Positive @PathVariable("userId") Long userId) {
        log.info("Удаление дизлайка к отзыву с id= {} пользователем с id= {}",id, userId);
        reviewService.removeDislike(id, userId);
    }
}