package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.constant.EventOperation;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.storage.eventFeed.EventFeedDBStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.Date;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EventFeedDBStorageImpTest {

    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    User user;
    Film film;

    private final EventFeedDBStorage eventFeedDBStorage;

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
    public void testGetFeedAfterAdd(){
        List<EventUser> eventUser = eventFeedDBStorage.getEventFeed(1);
        Assertions.assertEquals(eventUser.get(eventUser.size()-1).getOperation(), EventOperation.ADD);
    }


    @Test
    public void testGetFeedWithReviewAfterAddUpdate(){
        Review updateReview = Review.builder()
                .reviewId(1L)
                .content("content")
                .isPositive(false)
                .useful(1).build();
        reviewStorage.update(updateReview);
        List<EventUser> eventUser = eventFeedDBStorage.getEventFeed(1);
        Assertions.assertEquals(eventUser.get(eventUser.size()-1).getOperation(), EventOperation.UPDATE);
    }



}