package ru.yandex.practicum.filmorate.storage.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.constant.EventOperation;
import ru.yandex.practicum.filmorate.constant.EventType;
import ru.yandex.practicum.filmorate.model.EventUser;
import ru.yandex.practicum.filmorate.storage.EventFeedStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Objects;


@Repository
@RequiredArgsConstructor
public class EventFeedDbStorage implements EventFeedStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public EventUser setEventFeed(EventUser event) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Date time = new Date();
        long timestamp = time.getTime();
        event.setTimestamp(timestamp);

        String sqlQuery = "INSERT INTO event_feed (user_id, entity_id, event_type, operation,  timestamp) " +
                "VALUES (?, ?, ?, ?, ?);";
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"id"});
            statement.setLong(1, event.getUserId());
            statement.setLong(2, event.getEntityId());
            statement.setString(3, event.getEventType().toString());
            statement.setString(4, event.getOperation().toString());
            statement.setLong(5, event.getTimestamp());
            return statement;
        }, keyHolder);

        event.setEventId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return event;
    }

    @Override
    public List<EventUser>  getEventFeed(long userId) {
        String sqlQuery = "select * from event_feed where user_id = ? ORDER BY timestamp";
        return jdbcTemplate.query(sqlQuery, (rs, rn) -> mapToEventUser(rs), userId);
    }

    private EventUser mapToEventUser(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        long userId = rs.getLong("user_id");
        long entityId = rs.getLong("entity_id");
        String type = rs.getString("event_type");
        EventType eventType = EventType.valueOf(type);
        String eventOperation = rs.getString("operation");
        EventOperation operation = EventOperation.valueOf(eventOperation);
        long timeStamp = rs.getLong("timestamp");

        return new EventUser(id, userId, entityId, eventType, operation, timeStamp);
    }



}
