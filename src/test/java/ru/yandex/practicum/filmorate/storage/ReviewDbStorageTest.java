package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.webjars.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReviewDbStorageTest {
    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final ReviewService reviewService;
    private Review review;

    @BeforeAll
    @Test
    public void beforeAllCreateFilmAndUser() {
        User user = User.builder().email("email1@example.com").login("user1")
                .name("User 1").birthday(Date.valueOf("1990-01-01")).build();
        userStorage.add(user);
        Film film = Film.builder().name("test").description("test").releaseDate(Date.valueOf("2020-10-10"))
                .duration(100).mpa(new Mpa(1L, null)).build();
        filmStorage.add(film);
        review = Review.builder()
                .content("test content")
                .isPositive(true)
                .userId(1L)
                .filmId(1L)
                .build();
    }

    @Test
    public void testCreateReview() {
        reviewStorage.add(review);
        assertEquals(review.getReviewId(), 1);
        assertThat(review).hasFieldOrPropertyWithValue("reviewId", 1);
    }

    @Test
    public void testGetReview() {
        reviewStorage.getById(1L);
        assertThat(review).
                hasFieldOrPropertyWithValue("reviewId", 1).
                hasFieldOrPropertyWithValue("content", "test content");
    }

    @Test
    public void testUpdateReview() {
        Review updateReview = Review.builder()
                .reviewId(1L)
                .content("content")
                .isPositive(false)
                .useful(1).build();
        reviewStorage.update(updateReview);
        assertThat(updateReview).hasFieldOrPropertyWithValue("reviewId", 1);
    }

    @Test
    public void testDeleteReview() {
        reviewStorage.delete(1L);
        Exception exception = assertThrows(NotFoundException.class, () -> {
            reviewService.getById(1L);
        });
        assertThat(exception.getMessage()).contains("Отзыв с таким id не был найден.");
    }
}
