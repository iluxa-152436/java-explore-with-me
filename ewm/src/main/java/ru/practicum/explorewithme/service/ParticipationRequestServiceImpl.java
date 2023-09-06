package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.dto.ParticipationRequestDto;
import ru.practicum.explorewithme.entity.*;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.exception.ParticipationRequestException;
import ru.practicum.explorewithme.mapper.ParticipationRequestMapper;
import ru.practicum.explorewithme.storage.EventStorage;
import ru.practicum.explorewithme.storage.ParticipationRequestStorage;
import ru.practicum.explorewithme.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

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

    private void checkUser(long userId) {
        if (userStorage.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " not found");
        }
    }
}
