package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.dto.EventRequestStatusUpdateResult;
import ru.practicum.explorewithme.dto.ParticipationRequestDto;
import ru.practicum.explorewithme.entity.*;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.exception.ParticipationRequestException;
import ru.practicum.explorewithme.mapper.ParticipationRequestMapper;
import ru.practicum.explorewithme.storage.EventStorage;
import ru.practicum.explorewithme.storage.ParticipationRequestStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final ParticipationRequestStorage requestStorage;
    private final UserService userService;
    private final EventStorage eventStorage;
    private final ParticipationRequestMapper requestMapper;

    @Override
    public ParticipationRequestDto addRequest(long userId, long eventId) {
        User user = userService.getUser(userId);
        Event event = eventStorage.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + userId + " not found"));
        long countOfConfirmed = requestStorage.countByEventIdAndState(eventId, ParticipationRequestState.CONFIRMED);
        log.debug("Количество подтвержденных запросов={}", countOfConfirmed);
        log.debug("Лимит запросов на событие ={}", event.getParticipantLimit());
        ParticipationRequestState state;
        if (userId == event.getInitiator().getId() || !event.getState().equals(EventState.PUBLISHED)) {
            log.debug("Не пройдена проверка владельца события, или событие не опубликовано");
            throw new ParticipationRequestException("Владелец или событие не опубликовано");
        } else if (event.isRequestModeration()) {
            if (event.getParticipantLimit() > countOfConfirmed) {
                log.debug("Установлен новый статус для запроса PENDING");
                state = ParticipationRequestState.PENDING;
            } else {
                log.debug("Нет свободных мест на событие");
                throw new ParticipationRequestException("Нет мест");
            }
        } else {
            log.debug("Событие не требует подтверждения");
            state = ParticipationRequestState.CONFIRMED;
        }
        log.debug("Сохранение события");
        return requestMapper.toParticipationRequestDto(requestStorage.save(requestMapper.toEntity(user, event, state)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequests(long userId) {
        userService.checkUser(userId);
        return requestStorage.findByRequesterId(userId).stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto updateRequestByRequester(long userId, long requestId) {
        userService.checkUser(userId);
        ParticipationRequest request = requestStorage.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " not found"));
        request.setState(ParticipationRequestState.REJECTED);
        return requestMapper.toParticipationRequestDto(requestStorage.save(request));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getEventRequests(long userId, long eventId) {
        userService.checkUser(userId);
        checkEventByUserId(userId, eventId);
        return requestStorage.findByEventId(eventId).stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestByEventOwner(long userId,
                                                                    long eventId,
                                                                    EventRequestStatusUpdateRequest updateRequest) {
        userService.checkUser(userId);
        checkEventByUserId(userId, eventId);
        List<ParticipationRequest> requests = requestStorage.findAllByIdIn(updateRequest.getRequestIds());
        long available = requestStorage.countByEventIdAndRequesterIdAndState(eventId,
                userId,
                ParticipationRequestState.CONFIRMED);
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
                    default:
                        log.debug("Заявка не была изменена");
                }
            } else if (request.getState() == ParticipationRequestState.PENDING) {
                request.setState(ParticipationRequestState.REJECTED);
                available++;
                rejected.add(request);
            }
        }
        return requestMapper.toEventRequestStatusUpdateResult(confirmed, rejected);
    }

    @Override
    @Transactional(readOnly = true)
    public long getNumberOfConfirmed(long eventId) {
        return requestStorage.countByEventIdAndState(eventId, ParticipationRequestState.CONFIRMED);
    }

    @Transactional(readOnly = true)
    private void checkEventByUserId(long userId, long eventId) {
        if (!eventStorage.existsByIdAndInitiatorId(eventId, userId)) {
            throw new NotFoundException("Event with id=" + eventId + " not found");
        }
    }
}
