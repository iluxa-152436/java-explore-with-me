package ru.practicum.explorewithme.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.dto.ParticipationRequestDto;
import ru.practicum.explorewithme.entity.Event;
import ru.practicum.explorewithme.entity.ParticipationRequest;
import ru.practicum.explorewithme.entity.ParticipationRequestState;
import ru.practicum.explorewithme.entity.User;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class ParticipationRequestMapper {
    public ParticipationRequest toEntity(User requester, Event event, ParticipationRequestState state) {
        return ParticipationRequest.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(requester)
                .state(state)
                .build();
    }

    public ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest) {
        return ParticipationRequestDto.builder()
                .id(participationRequest.getId())
                .status(participationRequest.getState().name())
                .requester(participationRequest.getRequester().getId())
                .event(participationRequest.getEvent().getId())
                .created(participationRequest.getCreated())
                .build();
    }
}
