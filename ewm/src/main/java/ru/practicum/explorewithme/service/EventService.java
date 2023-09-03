package ru.practicum.explorewithme.service;

import ru.practicum.explorewithme.dto.EventFullDto;
import ru.practicum.explorewithme.dto.EventShortDto;
import ru.practicum.explorewithme.dto.NewEventRequest;
import ru.practicum.explorewithme.dto.UpdateEventUserRequest;
import ru.practicum.explorewithme.entity.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
}
