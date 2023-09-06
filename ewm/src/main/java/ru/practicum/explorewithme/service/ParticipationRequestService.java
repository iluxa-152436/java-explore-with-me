package ru.practicum.explorewithme.service;

import ru.practicum.explorewithme.dto.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestService {
    ParticipationRequestDto addRequest(long userId, long eventId);

    List<ParticipationRequestDto> getRequests(long userId);

    ParticipationRequestDto updateRequestByRequester(long userId, long requestId);
}
