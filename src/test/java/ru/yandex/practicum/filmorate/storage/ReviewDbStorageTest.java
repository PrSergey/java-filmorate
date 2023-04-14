package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.util.HashSet;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReviewDbStorageTest {
    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
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

}