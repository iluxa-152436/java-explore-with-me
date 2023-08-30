package ru.explorewithme.service;

import ru.explorewithme.dto.EventFullDto;
import ru.explorewithme.dto.NewEventRequest;

public interface EventService {
    EventFullDto addNewEvent(long userId, NewEventRequest newEventRequest);
}
