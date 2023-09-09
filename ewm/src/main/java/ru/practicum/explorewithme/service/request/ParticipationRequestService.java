package ru.practicum.explorewithme.service.request;

import ru.practicum.explorewithme.dto.participationRequest.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.dto.participationRequest.EventRequestStatusUpdateResult;
import ru.practicum.explorewithme.dto.participationRequest.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestService {
    ParticipationRequestDto addRequest(long userId, long eventId);

    List<ParticipationRequestDto> getRequests(long userId);

    ParticipationRequestDto updateRequestByRequester(long userId, long requestId);

    List<ParticipationRequestDto> getEventRequests(long userId, long eventId);

    EventRequestStatusUpdateResult updateRequestByEventOwner(long userId, long eventId, EventRequestStatusUpdateRequest updateRequest);

    long getNumberOfConfirmed(long eventId);
}
