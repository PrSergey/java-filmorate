package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.webjars.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.Date;
import java.util.HashSet;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReviewDbStorageTest {
    private static ReviewStorage reviewStorage;
    private static UserStorage userStorage;
    private static FilmStorage filmStorage;
    private static ReviewService reviewService;

    @Autowired
    @Qualifier("reviewDbStorage")
    public void setReviewStorage(ReviewStorage reviewStorage) {
        ReviewDbStorageTest.reviewStorage = reviewStorage;
    }

    @Autowired
    public void setUserStorage(UserStorage userStorage) {
        ReviewDbStorageTest.userStorage = userStorage;
    }

    @Autowired
    public void setFilmStorage(FilmStorage filmStorage) {
        ReviewDbStorageTest.filmStorage = filmStorage;
    }

    @Autowired
    public void setReviewService(ReviewService reviewService) {
        ReviewDbStorageTest.reviewService = reviewService;
    }

    @BeforeAll
    public static void beforeAllCreateFilmAndUserAndReview() {
        User user = new User(null, "agvaga@email.com",
                "user457", "gaerg", Date.valueOf("1997-04-09"), new HashSet<>());
        userStorage.add(user);
        Film film = Film.builder().name("test").description("test").releaseDate(Date.valueOf("2020-10-10"))
                .duration(100).mpa(new Mpa(1L, null)).build();
        filmStorage.add(film);
        Review review = Review.builder()
                .content("test content")
                .isPositive(true)
                .userId(user.getId())
                .filmId(film.getId())
                .build();
        reviewStorage.add(review);
    }

    @Test
    public void testCreate() {
        Review review2 = Review.builder()
                .content("test content film2")
                .isPositive(true)
                .userId(1L)
                .filmId(1L)
                .build();
        reviewStorage.add(review2);
        assertThat(review2).
                hasFieldOrPropertyWithValue("reviewId", review2.getReviewId()).
                hasFieldOrPropertyWithValue("content", "test content film2");
    }

    @Test
    public void testGetReviewById() {
        Review review = reviewStorage.getById(1L);
        assertThat(review).
                hasFieldOrPropertyWithValue("reviewId", review.getReviewId()).
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
        assertThat(updateReview)
                .hasFieldOrPropertyWithValue("reviewId", updateReview.getReviewId())
                .hasFieldOrPropertyWithValue("isPositive", false);
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