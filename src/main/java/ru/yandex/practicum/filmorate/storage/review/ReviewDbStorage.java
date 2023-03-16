package ru.yandex.practicum.filmorate.storage.review;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.webjars.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Review> getAll(Long filmId, Integer count) {
        if (filmId == null) {
            String sql = "SELECT * FROM reviews ORDER BY useful DESC ";
            List<Review> reviews = jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs));
            return reviews
                    .stream()
                    .limit(count)
                    .collect(Collectors.toList());
        } else {
            String sql = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC ";
            List<Review> reviews = jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs), filmId)
                    .stream()
                    .limit(count)
                    .collect(Collectors.toList());
            return reviews;
        }
    }

    @Override
    public Review getById(Long reviewId) {
        String sql = "SELECT * FROM reviews WHERE id = ? ";
        Review review;
        try {
            review = jdbcTemplate.query(sql, this::makeReview, reviewId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Отзыв с id=" + reviewId + " не существует");
        }
        return review;
    }

    @Override
    public Review add(Review review) {
        String sql = "INSERT INTO reviews(content,is_positive,film_id,user_id) VALUES(?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.getIsPositive());
            ps.setLong(3, review.getFilmId());
            ps.setLong(4, review.getUserId());
            return ps;
        }, keyHolder);
        Long id = keyHolder.getKey().longValue();
        review.setId(id);
        return review;
    }

    @Override
    public Review update(Review review) {
        getById(review.getId());
        String sql = "UPDATE reviews SET content=?, is_positive=? WHERE id = ? ";
        jdbcTemplate.update(
                sql,
                review.getContent(),
                review.getIsPositive(),
                review.getId()
        );
        return getById(review.getId());
    }

    @Override
    public void delete(Long reviewId) {
        String sql = "DELETE FROM reviews WHERE id = ? ";
        jdbcTemplate.update(sql, reviewId);
    }

    @Override
    public void addLike(Long reviewId, Long userId) {
        String sql = "UPDATE reviews SET useful = useful + 1 WHERE id = ?";
        jdbcTemplate.update(sql, reviewId);
    }

    @Override
    public void addDislike(Long reviewId, Long userId) {
        String sql = "UPDATE reviews SET useful = useful-1 WHERE id = ?";
        jdbcTemplate.update(sql, reviewId);
    }

    @Override
    public void removeLike(Long reviewId, Long userId) {
        String sql1 = "UPDATE reviews SET useful -= ? WHERE id = ?";
        jdbcTemplate.update(sql1, reviewId);
    }

    @Override
    public void removeDislike(Long reviewId, Long userId) {
        String sql1 = "UPDATE reviews SET useful = (useful + 1) WHERE id = ?";
        jdbcTemplate.update(sql1, reviewId);
    }

    private Review makeReview(ResultSet rs) throws SQLException {
        return new Review(
                rs.getLong("id"),
                rs.getString("content"),
                rs.getBoolean("is_positive"),
                rs.getLong("film_id"),
                rs.getLong("user_id"),
                rs.getInt("useful")
        );
    }
}
