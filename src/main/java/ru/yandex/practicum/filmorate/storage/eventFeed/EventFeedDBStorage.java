package ru.yandex.practicum.filmorate.storage.eventFeed;

import ru.yandex.practicum.filmorate.model.EventUser;

import java.util.List;

public interface EventFeedDBStorage {

    EventUser setEventFeed (EventUser event);
    List<EventUser> getEventFeed (long userId);
}
