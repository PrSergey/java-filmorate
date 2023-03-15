package ru.yandex.practicum.filmorate.storage.review;

import lombok.AllArgsConstructor;
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

@Repository
@AllArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Review> getAll(Long filmId) {
        if (filmId == null) {
            String sql = "SELECT * FROM reviews ORDER BY useful DESC ";
            List<Review> reviews = jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs));
            return reviews;
        } else {
            String sql = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC ";
            List<Review> reviews = jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs));
            return reviews;
        }
    }

    @Override
    public Review getById(Long reviewId) {
        String sql = "SELECT * FROM reviews WHERE id=?";
        Review review = jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs), reviewId)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Отзыв с id=" + reviewId + " не существует"))
                ;
        return review;
    }

    @Override
    public Review add(Review review) {
        String sql = "INSERT INTO reviews(content,is_positive,user_id,film_id,useful) VALUES(?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.getIsPositive());
            ps.setLong(3, review.getUserId());
            ps.setLong(4, review.getFilmId());
            ps.setInt(5, review.getUseful());
            return ps;
        }, keyHolder);
        Long id = keyHolder.getKey().longValue();
        review.setId(id);
        return review;
    }

    @Override
    public Review update(Review review) {
        getById(review.getId());
        String sql = "UPDATE reviews SET content=?, is_positive=?, film_id=?, user_id=?, useful=? WHERE id=? ";
        jdbcTemplate.update(
                sql,
                review.getContent(),
                review.getIsPositive(),
                review.getFilmId(),
                review.getUserId(),
                review.getUseful(),
                review.getId()
        );
        return getById(review.getId());
    }

    @Override
    public void delete(Long reviewId) {
        getById(reviewId);
        String sql = "DELETE FROM reviews WHERE id = ?";
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
        return new Review(rs.getLong("id"),
                rs.getString("content"),
                rs.getBoolean("is_positive"),
                rs.getLong("film_id"),
                rs.getLong("user_id"),
                rs.getInt("useful")
        );
    }
}
