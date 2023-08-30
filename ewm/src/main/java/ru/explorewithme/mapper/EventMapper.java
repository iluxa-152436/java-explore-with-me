package ru.explorewithme.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.explorewithme.dto.EventFullDto;
import ru.explorewithme.dto.NewEventRequest;
import ru.explorewithme.entity.Event;
import ru.explorewithme.entity.EventState;
import ru.explorewithme.entity.Location;
import ru.explorewithme.storage.CategoryStorage;
import ru.explorewithme.storage.UserStorage;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class EventMapper {
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
}
