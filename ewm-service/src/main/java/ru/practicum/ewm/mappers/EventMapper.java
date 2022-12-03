package ru.practicum.ewm.mappers;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.entities.Event;
import ru.practicum.ewm.entities.Location;
import ru.practicum.ewm.util.EventState;

import java.time.LocalDateTime;

@Component
public class EventMapper {
    public EventFullDto toDto(Event e, CategoryDto cat, int confirmed, UserShortDto u, int views, int rating) {
        return new EventFullDto(e.getAnnotation(), cat, confirmed, e.getCreatedOn(),
                e.getDescription(), e.getEventDate(), e.getId(), u, new Location(e.getLat(), e.getLon()),
                e.getPaid(), e.getParticipantLimit(), e.getPublishedOn(), e.getRequestModeration(),
                e.getState(), e.getTitle(), views, rating);
    }

    public EventShortDto toShortDto(Event e, CategoryDto cat, int confirmed, UserShortDto u, int views, int rating) {
        return new EventShortDto(e.getAnnotation(), cat, confirmed, e.getEventDate(),
                e.getId(), u, e.getPaid(), e.getTitle(), views, rating);
    }

    public Event fromDto(NewEventDto e, LocalDateTime created, int userId, EventState state) {
        return new Event(e.getAnnotation(), e.getCategory(), created, e.getDescription(),
                e.getEventDate(), userId, e.getLocation().getLat(), e.getLocation().getLon(), e.getPaid(),
                e.getParticipantLimit(), e.getRequestModeration(), state, e.getTitle());
    }
}
