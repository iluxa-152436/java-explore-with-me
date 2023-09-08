package ru.practicum.explorewithme.service;

import ru.practicum.explorewithme.dto.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.dto.EventRequestStatusUpdateResult;
import ru.practicum.explorewithme.dto.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestService {
    ParticipationRequestDto addRequest(long userId, long eventId);

    List<ParticipationRequestDto> getRequests(long userId);

    ParticipationRequestDto updateRequestByRequester(long userId, long requestId);

    List<ParticipationRequestDto> getEventRequests(long userId, long eventId);

    EventRequestStatusUpdateResult updateRequestByEventOwner(long userId, long eventId, EventRequestStatusUpdateRequest updateRequest);

    long getNumberOfConfirmed(long eventId);
}
