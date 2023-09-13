package ru.practicum.explorewithme.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.StatsClient;
import ru.practicum.explorewithme.dto.event.*;
import ru.practicum.explorewithme.dto.location.LocationShortDto;
import ru.practicum.explorewithme.entity.Event;
import ru.practicum.explorewithme.entity.EventState;
import ru.practicum.explorewithme.entity.Location;
import ru.practicum.explorewithme.entity.RequestPage;
import ru.practicum.explorewithme.exception.IllegalEventStateException;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.mapper.EventMapper;
import ru.practicum.explorewithme.service.location.LocationService;
import ru.practicum.explorewithme.service.request.ParticipationRequestService;
import ru.practicum.explorewithme.service.user.UserService;
import ru.practicum.explorewithme.storage.EventStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private final LocationService locationService;
    private final ModelMapper mapper;

    @Override
    public EventFullDto addNewEvent(long userId, NewEventRequest newEventRequest) {
        log.debug("Add new event={} from userId={}", newEventRequest, userId);
        userService.verifyUserExistence(userId);
        if (newEventRequest.getRequestModeration() == null) {
            newEventRequest.setRequestModeration(true);
        }
        Location location = calculateLocation(newEventRequest.getLocation());
        Event event = eventMapper.toEntity(userId, newEventRequest, EventState.PENDING, location);
        log.debug("Event location {}", event.getLocation());
        return eventMapper.toEventFullDto(eventStorage.save(event), requestService.getNumberOfConfirmed(event.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEvents(long userId, int from, int size) {
        log.debug("Get events from={} size={}", from, size);
        userService.verifyUserExistence(userId);
        return eventStorage.findAll(RequestPage.getPageable(from, size, Optional.empty())).stream()
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
            Location location = calculateLocation(updateEventUserRequest.getLocation());
            return eventMapper.toEventFullDto(eventStorage.save(eventMapper.toEntity(event,
                    updateEventUserRequest, location)), requestService.getNumberOfConfirmed(event.getId()));
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
        Page<Event> eventPage = eventStorage
                .findAllForAdminWithFilters(rangeEnd.orElse(null),
                        rangeStart.orElse(null),
                        users.orElse(null),
                        categories.orElse(null),
                        states.orElse(null),
                        RequestPage.getPageable(from, size, Optional.empty()));
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
        PageRequest pageRequest = RequestPage.getPageable(from, size, Optional.of(sort));
        Page<Event> eventPage = getPublicEventsPage(text,
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
    public List<Event> getEvents(List<Long> events) {
        return eventStorage.findAllByIdIn(events);
    }

    @Transactional(readOnly = true)
    private Location calculateLocation(LocationShortDto locationShortDto) {
        Location location;
        if (Optional.of(locationShortDto).isPresent() && Optional.ofNullable(locationShortDto.getId()).isPresent()) {
            log.debug("Существующая локация");
            location = locationService.getLocation(locationShortDto.getId());
            log.debug("Получена локация из базы {}", location);
        } else if (locationService.getAdmLocationByGeoAndApproved(locationShortDto.getLon(),
                locationShortDto.getLat(), true).isPresent()) {
            location = locationService.getAdmLocationByGeoAndApproved(locationShortDto.getLon(),
                    locationShortDto.getLat(), true).get();
        } else {
            log.debug("Новая локация");
            location = mapper.map(locationShortDto, Location.class);
            log.debug("Сформирована локация");
        }
        return location;
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
    private Page<Event> getPublicEventsPage(Optional<String> text,
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

    private Map<Long, Long> getMapEventConfirmed(Page<Event> eventPage) {
        return eventPage.stream()
                .collect(Collectors.toMap(Event::getId, (event) -> requestService.getNumberOfConfirmed(event.getId())));
    }
}
