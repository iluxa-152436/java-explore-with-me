package ru.practicum.explorewithme.service;

import ru.practicum.explorewithme.dto.EventFullDto;
import ru.practicum.explorewithme.dto.EventShortDto;
import ru.practicum.explorewithme.dto.NewEventRequest;

import java.util.List;

public interface EventService {
    EventFullDto addNewEvent(long userId, NewEventRequest newEventRequest);

    List<EventShortDto> getEvents(long userId, int from, int size);
}
