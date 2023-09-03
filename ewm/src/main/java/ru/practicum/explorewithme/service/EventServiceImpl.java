package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.dto.EventFullDto;
import ru.practicum.explorewithme.dto.EventShortDto;
import ru.practicum.explorewithme.dto.UpdateEventUserRequest;
import ru.practicum.explorewithme.entity.Event;
import ru.practicum.explorewithme.entity.EventState;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.mapper.EventMapper;
import ru.practicum.explorewithme.storage.EventStorage;
import ru.practicum.explorewithme.dto.NewEventRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Override
    public List<EventShortDto> getEvents(long userId, int from, int size) {
        log.debug("Get events from={} size={}", from, size);
        userService.verifyUserExistence(userId);
        return eventStorage.findAll(Page.getPageable(from, size)).stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventById(long userId, long eventId) {
        log.debug("Get event user={} event={}", userId, eventId);
        userService.verifyUserExistence(userId);
        Optional<Event> result = eventStorage.findById(eventId);
        if (result.isPresent()) {
            return eventMapper.toEventFullDto(result.get());
        } else {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }
    }

    @Override
    public EventFullDto updateEvent(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest) {
        log.debug("Add new parameters of new event={} from userId={}", updateEventUserRequest, userId);
        userService.verifyUserExistence(userId);
        Optional<Event> result = eventStorage.findByIdAndInitiatorId(eventId, userId);
        if (result.isPresent()) {
            return eventMapper.toEventFullDto(eventStorage.save(eventMapper.toEntity(result.get(),
                    updateEventUserRequest)));
        } else {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }
    }

    @Override
    public List<EventFullDto> getEvents(Optional<List<Long>> users,
                                        Optional<List<EventState>> states,
                                        Optional<List<Long>> categories,
                                        Optional<LocalDateTime> rangeStart,
                                        Optional<LocalDateTime> rangeEnd,
                                        int from,
                                        int size) {
        EventFilter filter = determineFilter(users, states, categories, rangeStart, rangeEnd);
        PageRequest pageRequest = Page.getPageable(from, size);
        return eventMapper.eventFullDtoList(getEventsPage(users,
                states,
                categories,
                rangeStart,
                rangeEnd,
                filter,
                pageRequest));
    }

    private org.springframework.data.domain.Page<Event> getEventsPage(Optional<List<Long>> users,
                                                                      Optional<List<EventState>> states,
                                                                      Optional<List<Long>> categories,
                                                                      Optional<LocalDateTime> rangeStart,
                                                                      Optional<LocalDateTime> rangeEnd,
                                                                      EventFilter filter,
                                                                      PageRequest pageRequest) {
        switch (filter) {
            case U:
                return eventStorage.findAllByInitiatorIdIn(users.get(), pageRequest);
            case C:
                return eventStorage.findAllByCategoryIdIn(categories.get(), pageRequest);
            case S:
                return eventStorage.findAllByStateIn(states.get(), pageRequest);
            case R:
                return eventStorage.findAllByEventDateBetween(rangeStart.get(), rangeEnd.get(), pageRequest);
            case US:
                return eventStorage.findAllByInitiatorIdInAndStateIn(users.get(), states.get(), pageRequest);
            case UC:
                return eventStorage.findAllByInitiatorIdInAndCategoryIdIn(users.get(), categories.get(), pageRequest);
            case UR:
                return eventStorage.findAllByInitiatorIdInAndEventDateBetween(users.get(),
                        rangeStart.get(),
                        rangeEnd.get(),
                        pageRequest);
            case SC:
                return eventStorage.findAllByStateInAndCategoryIdIn(states.get(), categories.get(), pageRequest);
            case SR:
                return eventStorage.findAllByStateInAndEventDateBetween(states.get(),
                        rangeStart.get(),
                        rangeEnd.get(),
                        pageRequest);
            case CR:
                return eventStorage.findAllByCategoryIdInAndEventDateBetween(categories.get(),
                        rangeStart.get(),
                        rangeEnd.get(),
                        pageRequest);
            case SCR:
                return eventStorage.findAllByStateInAndCategoryIdInAndEventDateBetween(states.get(),
                        categories.get(),
                        rangeStart.get(),
                        rangeEnd.get(),
                        pageRequest);
            case UCR:
                return eventStorage.findAllByInitiatorIdInAndCategoryIdInAndEventDateBetween(users.get(),
                        categories.get(),
                        rangeStart.get(),
                        rangeEnd.get(),
                        pageRequest);
            case USC:
                return eventStorage.findAllByInitiatorIdInAndStateInAndCategoryIdIn(users.get(),
                        states.get(),
                        categories.get(),
                        pageRequest);
            case USR:
                return eventStorage.findAllByInitiatorIdInAndStateInAndEventDateBetween(users.get(),
                        states.get(),
                        rangeStart.get(),
                        rangeEnd.get(),
                        pageRequest);
            case USCR:
                return eventStorage.findAllByInitiatorIdInAndStateInAndCategoryIdInAndEventDateBetween(users.get(),
                        states.get(),
                        categories.get(),
                        rangeStart.get(),
                        rangeEnd.get(),
                        pageRequest);
            case EMPTY:
                return eventStorage.findAll(pageRequest);
            default:
                throw new IllegalArgumentException("Ошибка фильтрации");
        }
    }

    private EventFilter determineFilter(Optional<List<Long>> users,
                                        Optional<List<EventState>> states,
                                        Optional<List<Long>> categories,
                                        Optional<LocalDateTime> rangeStart,
                                        Optional<LocalDateTime> rangeEnd) {
        StringBuilder res = new StringBuilder();
        if (users.isPresent()) {
            res.append("U");
        }
        if (states.isPresent()) {
            res.append("S");
        }
        if (categories.isPresent()) {
            res.append("C");
        }
        if (rangeStart.isPresent() && rangeEnd.isPresent()) {
            res.append("R");
        }
        if (users.isEmpty() && states.isEmpty() && categories.isEmpty() && rangeStart.isEmpty() && rangeEnd.isEmpty()) {
            res.append("EMPTY");
        }
        log.debug("Final filter combination={}", EventFilter.valueOf(res.toString()));
        return EventFilter.valueOf(res.toString());
    }
}
