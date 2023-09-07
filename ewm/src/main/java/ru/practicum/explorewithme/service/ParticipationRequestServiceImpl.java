package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.dto.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.dto.EventRequestStatusUpdateResult;
import ru.practicum.explorewithme.dto.ParticipationRequestDto;
import ru.practicum.explorewithme.entity.*;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.exception.ParticipationRequestException;
import ru.practicum.explorewithme.mapper.ParticipationRequestMapper;
import ru.practicum.explorewithme.storage.EventStorage;
import ru.practicum.explorewithme.storage.ParticipationRequestStorage;
import ru.practicum.explorewithme.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final ParticipationRequestStorage requestStorage;
    private final UserStorage userStorage;
    private final EventStorage eventStorage;
    private final ParticipationRequestMapper requestMapper;

    @Override
    public ParticipationRequestDto addRequest(long userId, long eventId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));
        Event event = eventStorage.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + userId + " not found"));
        Long countOfConfirmed = requestStorage.countByRequesterIdAndEventIdAndState(userId,
                eventId,
                ParticipationRequestState.CONFIRMED);

        if (userId == event.getInitiator().getId() || !event.getState().equals(EventState.PUBLISHED)) {
            throw new ParticipationRequestException("Ошибка валидации запроса на участие в событии");
        } else {
            ParticipationRequestState state;
            if (event.isRequestModeration()) {
                if (event.getParticipantLimit() > countOfConfirmed) {
                    state = ParticipationRequestState.PENDING;
                } else {
                    throw new ParticipationRequestException("Ошибка валидации запроса на участие в событии");
                }
            } else {
                state = ParticipationRequestState.CONFIRMED;
            }
            return requestMapper.toParticipationRequestDto(requestStorage.save(requestMapper.toEntity(user, event, state)));
        }
    }

    @Override
    public List<ParticipationRequestDto> getRequests(long userId) {
        checkUser(userId);
        return requestStorage.findByRequesterId(userId).stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto updateRequestByRequester(long userId, long requestId) {
        checkUser(userId);
        ParticipationRequest request = requestStorage.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " not found"));
        request.setState(ParticipationRequestState.REJECTED);
        return requestMapper.toParticipationRequestDto(requestStorage.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getEventRequests(long userId, long eventId) {
        checkUser(userId);
        eventStorage.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " not found"));
        return requestStorage.findByEventId(eventId).stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestByEventOwner(long userId,
                                                                    long eventId,
                                                                    EventRequestStatusUpdateRequest updateRequest) {
        checkUser(userId);
        eventStorage.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " not found"));
        List<ParticipationRequest> requests = requestStorage.findAllByIdIn(updateRequest.getRequestIds());
        long available = requestStorage.countByEventIdAndRequesterIdAndState(eventId, userId, ParticipationRequestState.CONFIRMED);
        List<ParticipationRequest> confirmed = new ArrayList<>();
        List<ParticipationRequest> rejected = new ArrayList<>();
        for (ParticipationRequest request : requests) {
            if (request.getState() == ParticipationRequestState.PENDING && available >= 1) {
                switch (updateRequest.getStatus()) {
                    case REJECTED:
                        request.setState(ParticipationRequestState.REJECTED);
                        available++;
                        rejected.add(request);
                        break;
                    case CONFIRMED:
                        request.setState(ParticipationRequestState.CONFIRMED);
                        available--;
                        confirmed.add(request);
                        break;
                    default: log.debug("Заявка не была изменена");
                }
            } else if (request.getState() == ParticipationRequestState.PENDING) {
                request.setState(ParticipationRequestState.REJECTED);
                available++;
                rejected.add(request);
            }
        }
        return requestMapper.toEventRequestStatusUpdateResult(confirmed, rejected);
    }

    private void checkUser(long userId) {
        if (userStorage.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " not found");
        }
    }
}
