package ru.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.explorewithme.dto.EventFullDto;
import ru.explorewithme.dto.NewEventRequest;
import ru.explorewithme.entity.Event;
import ru.explorewithme.entity.EventState;
import ru.explorewithme.mapper.EventMapper;
import ru.explorewithme.storage.EventStorage;

@RequiredArgsConstructor
@Slf4j
@Service
public class EventServiceImpl implements EventService {
    private final EventStorage eventStorage;
    private final EventMapper eventMapper;
    private final UserService userService;

    @Override
    public EventFullDto addNewEvent(long userId, NewEventRequest newEventRequest) {
        log.debug("Add new event={} from userId={}", newEventRequest, userId);
        userService.verifyUserExistence(userId);
        if (newEventRequest.getRequestModeration() == null) {
            newEventRequest.setRequestModeration(true);
        }
        Event event = eventMapper.toEntity(userId, newEventRequest, EventState.PENDING);
        log.debug("Event entity={}", event);
        return eventMapper.toEventFullDto(eventStorage.save(event));
    }
}
