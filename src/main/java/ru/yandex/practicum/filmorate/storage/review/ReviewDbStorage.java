package ru.yandex.practicum.filmorate.storage.review;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.webjars.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Component
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Review> getAll(Long filmId, Integer count) {
        if (filmId == -1) {
            String sql = "SELECT r.*, IFNULL(SUM(rl.useful), 0) AS useful " +
                    " FROM reviews AS r " +
                    " LEFT JOIN review_likes rl on r.id = rl.review_id " +
                    " GROUP BY r.id " +
                    " ORDER BY useful DESC " +
                    " LIMIT ?";
            return jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs), count);
        } else {
            String sql = "SELECT r.*, IFNULL(SUM(rl.useful), 0) AS useful " +
                    " FROM reviews AS r " +
                    " LEFT JOIN review_likes rl on r.id = rl.review_id " +
                    " WHERE r.film_id = ?" +
                    " GROUP BY r.id " +
                    " ORDER BY useful DESC " +
                    " LIMIT ?";
            return new ArrayList<>(jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs), filmId, count));
        }
    }

    @Override
    public List<Review> getAll() {
        String sqlQuery = "SELECT r.*, IFNULL(SUM(rk.useful), 0) AS useful " +
                " FROM reviews AS r " +
                " LEFT JOIN review_likes rl on r.id = rl.review_id " +
                " GROUP BY r.id " +
                " ORDER BY useful DESC";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeReview(rs));
    }

    @Override
    public Review getById(Long reviewId) {
        String sql = "SELECT r.*, SUM(rl.useful) AS useful " +
                " FROM reviews AS r " +
                " LEFT JOIN review_likes rl on r.id = rl.review_id " +
                " WHERE r.id = ? " +
                " GROUP BY r.id";

        Review review = jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs), reviewId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Отзыв с таким id не был найден."));
        return review;
    }

    @Override
    public Review add(Review review) {
        String sql = "INSERT INTO reviews (content, is_positive, user_id, film_id) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.getIsPositive());
            ps.setLong(3, review.getUserId());
            ps.setLong(4, review.getFilmId());
            return ps;
        }, keyHolder);
        review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return review;
    }

    @Override
    public Review update(Review review) {
        String sql = "UPDATE reviews SET content=?, is_positive=? WHERE id = ? ";
        jdbcTemplate.update(
                sql,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId()
        );
        return getById(review.getReviewId());
    }

    @Override
    public void delete(Long reviewId) {
        String sql = "DELETE FROM reviews WHERE id = ? ";
        jdbcTemplate.update(sql, reviewId);
    }

    @Override
    public void addLike(Long reviewId, Long userId) {
        String sql = "MERGE INTO review_likes (review_id, user_id, useful) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, reviewId, userId, 1);
    }

    @Override
    public void addDislike(Long reviewId, Long userId) {
        String sql = "MERGE INTO review_likes (review_id, user_id, useful) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, reviewId, userId, -1);
    }

    @Override
    public void removeLike(Long reviewId, Long userId) {
        String sql = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND useful = ?";
        if (jdbcTemplate.update(sql, reviewId, userId, 1) == 0) {
            throw new NotFoundException(
                    String.format("Review like with %d id not found", reviewId));
        }
    }

    @Override
    public void removeDislike(Long reviewId, Long userId) {
        String sql = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND useful = ?";
        if (jdbcTemplate.update(sql, reviewId, userId, -1) == 0) {
            throw new NotFoundException(
                    String.format("Review like with %d id not found", reviewId));
        }
    }

    private Review makeReview(ResultSet rs) throws SQLException {
        return new Review(
                rs.getLong("id"),
                rs.getString("content"),
                rs.getBoolean("is_positive"),
                rs.getLong("user_id"),
                rs.getLong("film_id"),
                rs.getInt("useful")
        );
    }
}