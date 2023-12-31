package ru.practicum.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;
import ru.practicum.explorewithme.dto.event.NewEventRequest;
import ru.practicum.explorewithme.dto.event.UpdateEventUserRequest;
import ru.practicum.explorewithme.dto.participationRequest.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.dto.participationRequest.EventRequestStatusUpdateResult;
import ru.practicum.explorewithme.dto.participationRequest.ParticipationRequestDto;
import ru.practicum.explorewithme.service.event.EventService;
import ru.practicum.explorewithme.service.request.ParticipationRequestService;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.explorewithme.constant.DefaultValue.FROM;
import static ru.practicum.explorewithme.constant.DefaultValue.SIZE;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}")
public class UserController {
    private final EventService eventService;
    private final ParticipationRequestService requestService;

    @PostMapping("/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto postEvent(@RequestBody @Valid NewEventRequest newEventRequest,
                                  @PathVariable long userId) {
        log.debug("Поступил запрос пользователя={} на создание события", userId);
        return eventService.addNewEvent(userId, newEventRequest);
    }

    @GetMapping("/events")
    public List<EventShortDto> getEvents(@PathVariable long userId,
                                         @RequestParam(defaultValue = FROM) int from,
                                         @RequestParam(defaultValue = SIZE) int size) {
        log.debug("Поступил запрос пользователя={} на получение событий", userId);
        return eventService.getEvents(userId, from, size);
    }

    @GetMapping("/events/{eventId}")
    public EventFullDto getEventById(@PathVariable long userId, @PathVariable long eventId) {
        log.debug("Поступил запрос пользователя={} на создание события с id={}", userId, eventId);
        return eventService.getEventById(userId, eventId);
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto patchEvent(@PathVariable long userId,
                                   @PathVariable long eventId,
                                   @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        log.debug("Поступил запрос пользователя={} на изменение события с id={}", userId, eventId);
        return eventService.updateEvent(userId, eventId, updateEventUserRequest);
    }

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto postRequest(@PathVariable long userId,
                                               @RequestParam long eventId) {
        log.debug("Поступил запрос пользователя={} на создание запроса на участие в событии id={}", userId, eventId);
        return requestService.addRequest(userId, eventId);
    }

    @GetMapping("/requests")
    public List<ParticipationRequestDto> getRequests(@PathVariable long userId) {
        log.debug("Поступил запрос пользователя={} на получение запросов на участие в событии", userId);
        return requestService.getRequests(userId);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public ParticipationRequestDto patchRequest(@PathVariable long userId,
                                                @PathVariable long requestId) {
        log.debug("Поступил запрос пользователя={} на отмену заявки id={} на участие в событии", userId, requestId);
        return requestService.updateRequestByRequester(userId, requestId);
    }

    @GetMapping("/events/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequests(@PathVariable long userId,
                                                          @PathVariable long eventId) {
        log.debug("Поступил запрос пользователя={} на получение заявок на участие по событию id={}", userId, eventId);
        return requestService.getEventRequests(userId, eventId);
    }

    @PatchMapping("/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequests(@PathVariable long userId,
                                                         @PathVariable long eventId,
                                                         @RequestBody @Valid EventRequestStatusUpdateRequest updateRequest) {
        log.debug("Поступил запрос пользователя={} на одобрение или отказ заявок с id={} на участие в событии id={}, выполняется {}",
                userId,
                updateRequest.getRequestIds(),
                eventId,
                updateRequest.getStatus());
        return requestService.updateRequestByEventOwner(userId, eventId, updateRequest);
    }
}
