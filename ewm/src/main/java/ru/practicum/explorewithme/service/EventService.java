package ru.practicum.explorewithme.service;

import ru.practicum.explorewithme.dto.*;
import ru.practicum.explorewithme.entity.Event;
import ru.practicum.explorewithme.entity.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventService {
    EventFullDto addNewEvent(long userId, NewEventRequest newEventRequest);

    List<EventShortDto> getEvents(long userId, int from, int size);

    EventFullDto getEventById(long userId, long eventId);

    EventFullDto updateEvent(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<EventFullDto> getEvents(Optional<List<Long>> users,
                                 Optional<List<EventState>> states,
                                 Optional<List<Long>> categories,
                                 Optional<LocalDateTime> rangeStart,
                                 Optional<LocalDateTime> rangeEnd,
                                 int from,
                                 int size);

    EventFullDto updateEvent(long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventShortDto> getEvents(Optional<String> text,
                                  Optional<List<Long>> categories,
                                  Optional<Boolean> paid,
                                  Optional<LocalDateTime> rangeStart,
                                  Optional<LocalDateTime> rangeEnd,
                                  boolean onlyAvailable,
                                  int from,
                                  int size,
                                  TypeOfSorting sort,
                                  String ip);

    EventFullDto getPublicEventById(long eventId, String ip);

    List<Event> getEvents(Set<Long> events);
}
