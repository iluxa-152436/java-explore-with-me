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
        ParticipationRequestState newState = calculateState(userId, event);
        log.debug("Сохранение запроса на участие в событии id={}", event.getId());
        ParticipationRequest participationRequest = requestMapper.toEntity(user, event, newState);
        log.debug("Создан запрос для сохранения {} ", participationRequest);
        ParticipationRequest saved = requestStorage.save(participationRequest);
        log.debug("Сохранен запрос {} ", saved);
        return requestMapper.toParticipationRequestDto(saved);
    }

    private ParticipationRequestState calculateState(long userId, Event event) {
        ParticipationRequestState newState;
        long countOfConfirmed = requestStorage.countByEventIdAndState(event.getId(),
                ParticipationRequestState.CONFIRMED);
        log.debug("Количество подтвержденных запросов {}", countOfConfirmed);
        log.debug("Лимит запросов на участие в событии {}", event.getParticipantLimit());

        if (userId == event.getInitiator().getId() || !event.getState().equals(EventState.PUBLISHED)) {
            throw new ParticipationRequestException("Запрос от владельца события, или событие не опубликовано");
        }
        if (event.isRequestModeration() && event.getParticipantLimit() != 0) {
            if (event.getParticipantLimit() > countOfConfirmed) {
                newState = ParticipationRequestState.PENDING;
            } else {
                throw new ParticipationRequestException("Нет мест");
            }
        } else {
            newState = ParticipationRequestState.CONFIRMED;
            log.debug("Событие не требует подтверждения");
        }
        log.debug("Установлен новый статус для запроса {}", newState);
        return newState;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequests(long userId) {
        userService.verifyUserExistence(userId);
        return requestStorage.findByRequesterId(userId).stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto updateRequestByRequester(long userId, long requestId) {
        userService.verifyUserExistence(userId);
        ParticipationRequest request = requestStorage.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " not found"));
        request.setState(ParticipationRequestState.CANCELED);
        log.debug("Отменен запрос={} на участие пользователя={} в событии={}",
                request.getId(),
                userId,
                request.getEvent().getId());
        return requestMapper.toParticipationRequestDto(requestStorage.save(request));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getEventRequests(long userId, long eventId) {
        userService.verifyUserExistence(userId);
        checkEventByUserId(userId, eventId);
        return requestStorage.findByEventId(eventId).stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestByEventOwner(long userId,
                                                                    long eventId,
                                                                    EventRequestStatusUpdateRequest updateRequest) {
        userService.verifyUserExistence(userId);
        checkEventByUserId(userId, eventId);
        List<ParticipationRequest> requests = requestStorage.findAllByIdIn(updateRequest.getRequestIds());
        long booked = requestStorage.countByEventIdAndRequesterIdAndState(eventId,
                userId,
                ParticipationRequestState.CONFIRMED);
        long available = eventStorage.findById(eventId).get().getParticipantLimit() - booked;
        List<ParticipationRequest> confirmed = new ArrayList<>();
        List<ParticipationRequest> rejected = new ArrayList<>();
        for (ParticipationRequest request : requests) {
            log.debug("Заявка id {} из списка находится в статусе {}", request.getId(), request.getState());
            if (request.getState() == ParticipationRequestState.PENDING && available >= 1) {
                log.debug("Количество доступных мест {} в событии id={}", available, eventId);
                log.debug("Обработка заявки в статусе {}", ParticipationRequestState.PENDING);
                switch (updateRequest.getStatus()) {
                    case REJECTED:
                        request.setState(ParticipationRequestState.REJECTED);
                        log.debug("Установлен новый статус {} ", request.getState());
                        available++;
                        rejected.add(request);
                        break;
                    case CONFIRMED:
                        request.setState(ParticipationRequestState.CONFIRMED);
                        log.debug("Установлен новый статус {} ", request.getState());
                        available--;
                        confirmed.add(request);
                        break;
                    default:
                        log.debug("Заявка не была изменена");
                }
            } else if (request.getState() == ParticipationRequestState.PENDING) {
                log.debug("Количество доступных мест {} в событии id={}", available, eventId);
                request.setState(ParticipationRequestState.REJECTED);
                log.debug("Установлен новый статус {} ", request.getState());
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
