package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
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
import java.util.HashSet;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReviewDbStorageTest {
    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final ReviewService reviewService;

    @BeforeAll
    public void createUserAndFilm() {
        userStorage.add(new User(null, "agvaga@email.com",
                "user457", "gaerg", Date.valueOf("1997-04-09"), new HashSet<>()));
        userStorage.add(new User(null, "hxfht@email.com",
                        "user46542", "hjzt", Date.valueOf("1997-04-09"), new HashSet<>()));
        filmStorage.add(Film.builder().name("test").description("test").releaseDate(Date.valueOf("2020-10-10"))
                .duration(100).mpa(new Mpa(1L, null)).build());
    }

    @Test
    @Order(1)
    public void testCreate() {
        Review review = Review.builder()
                .content("test content")
                .isPositive(true)
                .userId(1L)
                .filmId(1L)
                .useful(0)
                .build();
        reviewStorage.add(review);
        assertThat(review).
                hasFieldOrPropertyWithValue("reviewId", 1L).
                hasFieldOrPropertyWithValue("content", "test content");
    }

    @Test
    @Order(2)
    public void testGetReviewById() {
        Review review = reviewStorage.getById(1L);
        assertThat(review).
                hasFieldOrPropertyWithValue("reviewId", 1L).
                hasFieldOrPropertyWithValue("content", "test content");
    }

    @Test
    @Order(3)
    public void testUpdateReview() {
        Review updateReview = Review.builder()
                .reviewId(1L)
                .content("content")
                .isPositive(false)
                .useful(1).build();
        reviewStorage.update(updateReview);
        assertThat(updateReview)
                .hasFieldOrPropertyWithValue("reviewId", 1L)
                .hasFieldOrPropertyWithValue("isPositive", false);
    }

    @Test
    @Order(4)
    public void testDeleteReview() {
        reviewStorage.delete(1L);
        Exception exception = assertThrows(NotFoundException.class, () -> {
            reviewService.getById(1L);
        });
        assertThat(exception.getMessage()).contains("Отзыв с таким id не был найден.");
    }
}