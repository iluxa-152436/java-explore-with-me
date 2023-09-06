package ru.practicum.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.*;
import ru.practicum.explorewithme.service.EventService;
import ru.practicum.explorewithme.service.ParticipationRequestService;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.explorewithme.constant.DefaultValue.FROM;
import static ru.practicum.explorewithme.constant.DefaultValue.SIZE;

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
        return eventService.addNewEvent(userId, newEventRequest);
    }

    @GetMapping("/events")
    public List<EventShortDto> getEvents(@PathVariable long userId,
                                         @RequestParam(defaultValue = FROM) int from,
                                         @RequestParam(defaultValue = SIZE) int size) {
        return eventService.getEvents(userId, from, size);
    }

    @GetMapping("/events/{eventId}")
    public EventFullDto getEventById(@PathVariable long userId, @PathVariable long eventId) {
        return eventService.getEventById(userId, eventId);
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto patchEvent(@PathVariable long userId,
                                   @PathVariable long eventId,
                                   @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        return eventService.updateEvent(userId, eventId, updateEventUserRequest);
    }

    @PostMapping("/requests")
    public ParticipationRequestDto postRequest(@PathVariable long userId,
                                               @RequestParam long eventId) {
        return requestService.addRequest(userId, eventId);
    }

    @GetMapping("/requests")
    public List<ParticipationRequestDto> getRequests(@PathVariable long userId) {
        return requestService.getRequests(userId);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public ParticipationRequestDto patchRequest(@PathVariable long userId,
                                                @RequestParam long requestId) {
        return requestService.updateRequestByRequester(userId, requestId);
    }
}
