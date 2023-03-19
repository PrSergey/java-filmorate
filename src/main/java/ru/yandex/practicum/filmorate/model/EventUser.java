package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.constant.EventType;
import ru.yandex.practicum.filmorate.constant.EventOperation;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventUser {

    private long eventId;
    private long userId;
    private long entityId;

    private EventType eventType;
    private EventOperation operation;
    private long timestamp;

    public EventUser(long userId, long entityId, EventType eventType, EventOperation operation) {
        this.userId = userId;
        this.entityId = entityId;
        this.eventType = eventType;
        this.operation = operation;
    }
}
