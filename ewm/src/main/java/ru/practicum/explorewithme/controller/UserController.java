package ru.practicum.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.EventFullDto;
import ru.practicum.explorewithme.dto.EventShortDto;
import ru.practicum.explorewithme.dto.NewEventRequest;
import ru.practicum.explorewithme.service.EventService;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}")
public class UserController {
    private final EventService eventService;

    @PostMapping("/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto postEvent(@RequestBody @Valid NewEventRequest newEventRequest,
                                  @PathVariable long userId) {
        return eventService.addNewEvent(userId, newEventRequest);
    }

    @GetMapping("/events")
    public List<EventShortDto> getEvents(@PathVariable long userId,
                                         @RequestParam(defaultValue = "0") int from,
                                         @RequestParam(defaultValue = "10") int size) {
        return eventService.getEvents(userId, from, size);
    }
}
