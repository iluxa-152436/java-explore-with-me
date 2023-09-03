package ru.practicum.explorewithme.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.HitGetDto;
import ru.practicum.explorewithme.StatsClient;
import ru.practicum.explorewithme.dto.*;
import ru.practicum.explorewithme.entity.Event;
import ru.practicum.explorewithme.entity.EventState;
import ru.practicum.explorewithme.entity.Location;
import ru.practicum.explorewithme.entity.StateAction;
import ru.practicum.explorewithme.exception.IllegalEventStateException;
import ru.practicum.explorewithme.storage.CategoryStorage;
import ru.practicum.explorewithme.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class EventMapper {
    private final StatsClient statsClient;
    private final CategoryStorage categoryStorage;
    private final UserStorage userStorage;
    private final ModelMapper mapper;
    private final boolean uniqueIp = true;

    public Event toEntity(long userId, NewEventRequest newEventRequest, EventState state) {
        return Event.builder().annotation(newEventRequest.getAnnotation())
                .category(categoryStorage.findById(newEventRequest.getCategory()).get())
                .description(newEventRequest.getDescription())
                .eventDate(newEventRequest.getEventDate())
                .location(mapper.map(newEventRequest.getLocation(), Location.class))
                .paid(newEventRequest.isPaid())
                .participantLimit(newEventRequest.getParticipantLimit())
                .title(newEventRequest.getTitle())
                .created(LocalDateTime.now())
                .state(state)
                .requestModeration(newEventRequest.getRequestModeration())
                .initiator(userStorage.findById(userId).get())
                .build();
    }

    public EventFullDto toEventFullDto(Event event) {
        EventFullDto result = mapper.map(event, EventFullDto.class);
        result.setViews(statsClient.getStats(event.getCreated(),
                        LocalDateTime.now(),
                        uniqueIp,
                        List.of("/events/" + event.getId())).stream()
                .mapToLong(HitGetDto::getHits)
                .sum());
        return result;
    }

    public EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(mapper.map(event.getCategory(), CategoryDto.class))
                //TODO заполнить .confirmedRequests()
                .eventDate(event.getEventDate())
                .initiator(mapper.map(event.getInitiator(), UserShortDto.class))
                .paid(event.isPaid())
                .title(event.getTitle())
                .views(statsClient.getStats(event.getCreated(),
                                LocalDateTime.now(),
                                uniqueIp,
                                List.of("/events/" + event.getId())).stream()
                        .mapToLong(HitGetDto::getHits)
                        .sum())
                .build();
    }

    public Event toEntity(Event event, UpdateEventUserRequest updateEventUserRequest) {
        Optional.ofNullable(updateEventUserRequest.getEventDate()).ifPresent(event::setEventDate);
        Optional.ofNullable(updateEventUserRequest.getRequestModeration()).ifPresent(event::setRequestModeration);
        Optional.ofNullable(updateEventUserRequest.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(updateEventUserRequest.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(updateEventUserRequest.getLocation())
                .ifPresent(location -> event.setLocation(mapper.map(location, Location.class)));
        Optional.of(updateEventUserRequest.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(updateEventUserRequest.getCategory())
                .ifPresent(categoryId -> event.setCategory(categoryStorage.findById(categoryId).get()));
        Optional.ofNullable(updateEventUserRequest.getTitle()).ifPresent(event::setTitle);
        Optional.ofNullable(updateEventUserRequest.getStateAction())
                .ifPresent(stateAction -> eventSetState(event, stateAction));
        Optional.ofNullable(updateEventUserRequest.getPaid()).ifPresent(event::setPaid);
        return event;
    }

    private void eventSetState(Event event, StateAction stateAction) {
        switch (stateAction) {
            case CANCEL_REVIEW:
                if (event.getState().equals(EventState.PENDING) || (event.getState().equals(EventState.CANCELED))) {
                    event.setState(EventState.CANCELED);
                } else {
                    throw new IllegalEventStateException("Статус не может быть изменен");
                }
                break;
            case SEND_TO_REVIEW:
                if (event.getState().equals(EventState.CANCELED)) {
                    event.setState(EventState.PENDING);
                } else {
                    throw new IllegalEventStateException("Статус не может быть изменен");
                }
                break;
        }
    }
}