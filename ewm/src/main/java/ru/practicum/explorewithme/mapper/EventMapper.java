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
import ru.practicum.explorewithme.storage.CategoryStorage;
import ru.practicum.explorewithme.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Component
public class EventMapper {
    private StatsClient statsClient;
    private final CategoryStorage categoryStorage;
    private final UserStorage userStorage;
    private final ModelMapper mapper;

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
        return mapper.map(event, EventFullDto.class);
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
                        true,
                        List.of("/events/" + event.getId())).stream()
                        .mapToLong(HitGetDto::getHits)
                        .sum())
                .build();
    }
}
