package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reviews")
@AllArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping
    public List<Review> findAll(@RequestParam(name = "filmId", required = false) Optional<Long> filmId,
                                @RequestParam(name = "count", required = false, defaultValue = "10") Optional<Integer> count) {
        return reviewService.getAll(filmId, count);
    }

    @PostMapping
    public Review add(@RequestBody @Valid Review review) {
        return reviewService.add(review);
    }

    @PutMapping
    public Review update(@RequestBody @Valid Review review) {
        return reviewService.update(review);
    }

    @GetMapping("/{id}")
    public Review findById(@Positive @PathVariable("id") Long id) {
        return reviewService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@Positive @PathVariable("id") Long id) {
        reviewService.delete(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@Positive @PathVariable("id") Long id,
                        @Positive @PathVariable("userId") Long userId) {
        reviewService.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislikeReview(@Positive @PathVariable("id") Long id,
                                 @Positive @PathVariable("userId") Long userId) {
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@Positive @PathVariable("id") Long id,
                           @Positive @PathVariable("userId") Long userId) {
        reviewService.removeLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@Positive @PathVariable("id") Long id,
                              @Positive @PathVariable("userId") Long userId) {
        reviewService.removeDislike(id, userId);
    }
}