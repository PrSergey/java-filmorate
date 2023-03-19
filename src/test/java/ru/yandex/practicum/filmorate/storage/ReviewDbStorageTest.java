package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.webjars.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.storage.eventFeed.EventFeedDBStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.Date;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
    User user;
    Film film;

    @BeforeAll
    public void beforeAllCreateFilmAndUserAndReview() {
        user = new User(null, "agvaga@email.com",
                "user457", "gaerg", Date.valueOf("1997-04-09"), new HashSet<>());
        userStorage.add(user);
        film = Film.builder().name("test").description("test").releaseDate(Date.valueOf("2020-10-10"))
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
                .userId(user.getId())
                .filmId(film.getId())
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