package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.dto.*;
import ru.practicum.explorewithme.entity.Event;
import ru.practicum.explorewithme.entity.EventState;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.mapper.EventMapper;
import ru.practicum.explorewithme.storage.EventStorage;

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
        return eventStorage.findAll(Page.getPageable(from, size, Optional.empty())).stream()
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
        log.debug("Update event, parameters of new event={} from userId={}", updateEventUserRequest, userId);
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
        EventAdminFilter filter = setAdminFilter(users, states, categories, rangeStart, rangeEnd);
        PageRequest pageRequest = Page.getPageable(from, size, Optional.empty());
        return eventMapper.toEventFullDtoList(getEventsPage(users,
                states,
                categories,
                rangeStart,
                rangeEnd,
                filter,
                pageRequest));
    }

    @Override
    public List<EventShortDto> getEvents(Optional<String> text,
                                         Optional<List<Long>> categories,
                                         Optional<Boolean> paid,
                                         Optional<LocalDateTime> rangeStart,
                                         Optional<LocalDateTime> rangeEnd,
                                         boolean onlyAvailable,
                                         int from,
                                         int size,
                                         TypeOfSorting sort) {
        //EventPublicFilter filter = setPublicFilter(text, categories, paid, rangeStart, rangeEnd, onlyAvailable);
        PageRequest pageRequest = Page.getPageable(from, size, Optional.of(sort));

        return eventMapper.toEventShortDtoList(getPublicEventsPage(text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                pageRequest));
    }

    private org.springframework.data.domain.Page<Event> getPublicEventsPage(Optional<String> text,
                                                                            Optional<List<Long>> categories,
                                                                            Optional<Boolean> paid,
                                                                            Optional<LocalDateTime> rangeStart,
                                                                            Optional<LocalDateTime> rangeEnd,
                                                                            boolean onlyAvailable,
                                                                            PageRequest pageRequest) {
        if (rangeStart.isEmpty()) {
            rangeStart = Optional.of(LocalDateTime.now());
        }
        return eventStorage.findAllForPublicWithFilters(rangeEnd.orElse(null),
                rangeStart.orElse(null),
                paid.orElse(null),
                categories.orElse(null),
                //TODO onlyAvailable,
                text.orElse(null),
                EventState.PUBLISHED.name(),
                pageRequest);
    }

    @Override
    public EventFullDto updateEvent(long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        log.debug("Update event by admin, parameters of new event={} eventId={}", updateEventAdminRequest, eventId);
        Optional<Event> event = eventStorage.findById(eventId);
        if (event.isPresent()) {
            EventFullDto result = eventMapper.toEventFullDto(eventStorage.save(eventMapper.toEntity(event.get(),
                    updateEventAdminRequest)));
            if (result.getPublishedOn() != null && result.getEventDate().isAfter(result.getPublishedOn().plusHours(1))) {
                return result;
            } else {
                throw new IllegalArgumentException("The publication date must be no later than an hour before the event date");
            }

        } else {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }
    }

    private org.springframework.data.domain.Page<Event> getEventsPage(Optional<List<Long>> users,
                                                                      Optional<List<EventState>> states,
                                                                      Optional<List<Long>> categories,
                                                                      Optional<LocalDateTime> rangeStart,
                                                                      Optional<LocalDateTime> rangeEnd,
                                                                      EventAdminFilter filter,
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

    private EventAdminFilter setAdminFilter(Optional<List<Long>> users,
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
        log.debug("Final admin event filter combination={}", EventAdminFilter.valueOf(res.toString()));
        return EventAdminFilter.valueOf(res.toString());
    }
}
