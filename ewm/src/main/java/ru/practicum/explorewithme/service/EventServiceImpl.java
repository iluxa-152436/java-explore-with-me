package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.StatsClient;
import ru.practicum.explorewithme.dto.*;
import ru.practicum.explorewithme.entity.Event;
import ru.practicum.explorewithme.entity.EventState;
import ru.practicum.explorewithme.exception.IllegalEventStateException;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.mapper.EventMapper;
import ru.practicum.explorewithme.storage.EventStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional
public class EventServiceImpl implements EventService {
    private final EventStorage eventStorage;
    private final EventMapper eventMapper;
    private final UserService userService;
    private final StatsClient statsClient;
    private final ParticipationRequestService requestService;

    @Override
    public EventFullDto addNewEvent(long userId, NewEventRequest newEventRequest) {
        log.debug("Add new event={} from userId={}", newEventRequest, userId);
        userService.verifyUserExistence(userId);
        if (newEventRequest.getRequestModeration() == null) {
            newEventRequest.setRequestModeration(true);
        }
        Event event = eventMapper.toEntity(userId, newEventRequest, EventState.PENDING);
        log.debug("Event entity={}", event);
        return eventMapper.toEventFullDto(eventStorage.save(event), requestService.getNumberOfConfirmed(event.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEvents(long userId, int from, int size) {
        log.debug("Get events from={} size={}", from, size);
        userService.verifyUserExistence(userId);
        return eventStorage.findAll(Page.getPageable(from, size, Optional.empty())).stream()
                .map((event) -> eventMapper.toEventShortDto(event, requestService.getNumberOfConfirmed(event.getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventById(long userId, long eventId) {
        log.debug("Get event user={} event={}", userId, eventId);
        userService.verifyUserExistence(userId);
        return eventMapper.toEventFullDto(getEvent(eventId), requestService.getNumberOfConfirmed(eventId));
    }

    @Override
    public EventFullDto updateEvent(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest) {
        log.debug("Update event, parameters of new event={} from userId={}", updateEventUserRequest, userId);
        userService.verifyUserExistence(userId);
        Event event = eventStorage.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new IllegalEventStateException("Статус события не позволяет изменить событие");
        } else {
            return eventMapper.toEventFullDto(eventStorage.save(eventMapper.toEntity(event,
                    updateEventUserRequest)), requestService.getNumberOfConfirmed(event.getId()));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getEvents(Optional<List<Long>> users,
                                        Optional<List<EventState>> states,
                                        Optional<List<Long>> categories,
                                        Optional<LocalDateTime> rangeStart,
                                        Optional<LocalDateTime> rangeEnd,
                                        int from,
                                        int size) {
        EventAdminFilter filter = setAdminFilter(users, states, categories, rangeStart, rangeEnd);
        PageRequest pageRequest = Page.getPageable(from, size, Optional.empty());
        org.springframework.data.domain.Page<Event> eventPage = getEventsPage(users,
                states,
                categories,
                rangeStart,
                rangeEnd,
                filter,
                pageRequest);
        return eventMapper.toEventFullDtoList(eventPage, getMapEventConfirmed(eventPage));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEvents(Optional<String> text,
                                         Optional<List<Long>> categories,
                                         Optional<Boolean> paid,
                                         Optional<LocalDateTime> rangeStart,
                                         Optional<LocalDateTime> rangeEnd,
                                         boolean onlyAvailable,
                                         int from,
                                         int size,
                                         TypeOfSorting sort,
                                         String ip) {
        PageRequest pageRequest = Page.getPageable(from, size, Optional.of(sort));
        org.springframework.data.domain.Page<Event> eventPage = getPublicEventsPage(text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                pageRequest);
        List<EventShortDto> result = eventMapper.toEventShortDtoList(eventPage, getMapEventConfirmed(eventPage));
        statsClient.postHit("/events", ip);
        return result;
    }

    @Override
    public EventFullDto updateEvent(long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        log.debug("Update event by admin, parameters of new event={} eventId={}", updateEventAdminRequest, eventId);
        Event oldEvent = getEvent(eventId);
        if (oldEvent.getPublished() != null && oldEvent.getEventDate().isBefore(oldEvent.getPublished().plusHours(1))) {
            throw new IllegalArgumentException("The publication date must be no later than an hour before the event date");
        } else {
            return eventMapper.toEventFullDto(eventStorage.save(eventMapper.toEntity(oldEvent,
                    updateEventAdminRequest)), requestService.getNumberOfConfirmed(eventId));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getPublicEventById(long eventId, String ip) {
        Optional<Event> event = eventStorage.findByIdAndState(eventId, EventState.PUBLISHED);
        if (event.isEmpty()) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }
        statsClient.postHit("/events/" + eventId, ip);
        return eventMapper.toEventFullDto(event.get(), requestService.getNumberOfConfirmed(eventId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getEvents(Set<Long> events) {
        return eventStorage.findAllByIdIn(events);
    }

    @Transactional(readOnly = true)
    private Event getEvent(long eventId) {
        Optional<Event> event = eventStorage.findById(eventId);
        if (event.isEmpty()) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        } else {
            return event.get();
        }
    }

    @Transactional(readOnly = true)
    private org.springframework.data.domain.Page<Event> getPublicEventsPage(Optional<String> text,
                                                                            Optional<List<Long>> categories,
                                                                            Optional<Boolean> paid,
                                                                            Optional<LocalDateTime> rangeStart,
                                                                            Optional<LocalDateTime> rangeEnd,
                                                                            boolean onlyAvailable,
                                                                            PageRequest pageRequest) {
        if (rangeStart.isPresent() && rangeEnd.isPresent()) {
            if (rangeStart.get().isAfter(rangeEnd.get())) {
                throw new IllegalArgumentException("range start and range end must be valid");
            }
        }
        if (rangeStart.isEmpty() && rangeEnd.isEmpty()) {
            rangeStart = Optional.of(LocalDateTime.now());
        }
        return eventStorage.findAllForPublicWithFilters(rangeEnd.orElse(null),
                rangeStart.orElse(null),
                paid.orElse(null),
                categories.orElse(null),
                onlyAvailable,
                text.orElse(null),
                EventState.PUBLISHED.name(),
                pageRequest);
    }

    @Transactional(readOnly = true)
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

    private Map<Long, Long> getMapEventConfirmed(org.springframework.data.domain.Page<Event> eventPage) {
        return eventPage.stream()
                .collect(Collectors.toMap(Event::getId, (event) -> requestService.getNumberOfConfirmed(event.getId())));
    }
}
