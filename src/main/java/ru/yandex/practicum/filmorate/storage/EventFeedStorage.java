package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.EventUser;

import java.util.List;

public interface EventFeedStorage {

    EventUser setEventFeed (EventUser event);
    List<EventUser> getEventFeed (long userId);
}
