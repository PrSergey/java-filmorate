package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.constant.EventType;
import ru.yandex.practicum.filmorate.constant.EventOperation;

import java.sql.Timestamp;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventUser {

    private long id;
    private long userId;
    private long entityId;
    private EventType eventType;
    private EventOperation eventOperation;
    private Timestamp timeStamp;

}
