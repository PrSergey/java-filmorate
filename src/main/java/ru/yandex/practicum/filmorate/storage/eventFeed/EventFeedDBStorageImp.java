package ru.yandex.practicum.filmorate.storage.eventFeed;


import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.constant.EventOperation;
import ru.yandex.practicum.filmorate.constant.EventType;
import ru.yandex.practicum.filmorate.model.EventUser;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;


@Repository
@RequiredArgsConstructor
public class EventFeedDBStorageImp implements EventFeedDBStorage {

    private final JdbcTemplate jdbcTemplate;



    @Override
    public EventUser setEventFeed(EventUser event) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
        event.setTimeStamp(timeStamp);

        String sqlQuery = "INSERT INTO event_feed (user_id, entity_id, event_type, operation,  time_stamp) " +
                "VALUES (?, ?, ?, ?, ?);";
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"id"});
            statement.setLong(1, event.getUserId());
            statement.setLong(2, event.getEntityId());
            statement.setString(3, event.getEventType().toString());
            statement.setString(4, event.getOperation().toString());
            statement.setTimestamp(5, event.getTimeStamp());
            return statement;
        }, keyHolder);
        event.setEventId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return event;
    }

    @Override
    public List<EventUser>  getEventFeed(long userId) {
        String sqlQuery = "select * from event_feed where user_id = ?";
        return jdbcTemplate.query(sqlQuery, (rs, rn) -> mapToEventUser(rs), userId);
    }

    private EventUser mapToEventUser(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        long userId = rs.getLong("user_id");
        long entityId = rs.getLong("entity_id");
        String type = rs.getString("event_type");
        EventType eventType = toEventType(type);
        String eventOperation = rs.getString("operation");
        EventOperation operation = toEventOperation(eventOperation);
        Timestamp timeStamp = rs.getTimestamp("time_stamp");

        return new EventUser(id, userId, entityId, eventType, operation, timeStamp);
    }

    private EventOperation toEventOperation(String nameOperation) {
        switch (nameOperation) {
            case ("REMOVE"):
                return EventOperation.REMOVE;
            case ("ADD"):
                return EventOperation.ADD;
            default:
                return EventOperation.UPDATE;
        }
    }

    private EventType toEventType(String eventType) {
        switch (eventType) {
            case ("LIKE"):
                return EventType.LIKE;
            case ("REVIEW"):
                return EventType.REVIEW;
            default:
                return EventType.FRIEND;
        }
    }



}
